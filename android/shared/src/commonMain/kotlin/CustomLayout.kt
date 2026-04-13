package com.vatoo.erick.shared

/**
 * A user-created custom layout defining all chord mappings and single-swipe actions.
 *
 * Chord maps: Map<Direction, List<String>> where each direction has characters
 * indexed by the right-dial direction order.
 * For 8-section: N=0, NE=1, E=2, SE=3, S=4, SW=5, W=6, NW=7 (8 chars per direction)
 * For 6-section: N=0, NE=1, SE=2, S=3, SW=4, NW=5 (6 chars per direction)
 *
 * Single-swipe map: Map<Direction, SingleSwipeBinding> where each entry is either
 * a character string or an InputAction.
 */
data class CustomLayout(
    val id: String,
    val name: String,
    val normalChordMap: Map<Direction, List<String>>,
    val shiftedChordMap: Map<Direction, List<String>>,
    val singleSwipeNormalMap: Map<Direction, SingleSwipeBinding>,
    val singleSwipeShiftedMap: Map<Direction, SingleSwipeBinding>,
    val sectionCount: Int = 8  // 8 or 6
)

/**
 * Represents a single-swipe binding: either a character to commit or a system action.
 */
sealed class SingleSwipeBinding {
    data class Character(val char: String) : SingleSwipeBinding()
    data class Action(val action: InputAction) : SingleSwipeBinding()

    fun toSerializable(): String {
        return when (this) {
            is Character -> "char:$char"
            is Action -> "action:${action.name}"
        }
    }

    companion object {
        fun fromSerializable(s: String): SingleSwipeBinding? {
            return when {
                s.startsWith("char:") -> Character(s.removePrefix("char:"))
                s.startsWith("action:") -> {
                    val name = s.removePrefix("action:")
                    val action = InputAction.entries.firstOrNull { it.name == name }
                    if (action != null) Action(action) else null
                }
                else -> null
            }
        }
    }
}

/**
 * Manages custom layouts: CRUD operations, validation, duplication from built-in layouts,
 * and JSON serialization. Platform-level persistence is handled via [CustomLayoutStorage].
 */
class CustomLayoutManager(private val storage: CustomLayoutStorage) {

    private val layouts = mutableMapOf<String, CustomLayout>()

    /** Load all layouts from platform storage. Call once at startup. */
    fun loadAll() {
        layouts.clear()
        val json = storage.loadAllLayoutsJson()
        if (json.isNotEmpty()) {
            val parsed = CustomLayoutSerializer.deserializeAll(json)
            parsed.forEach { layouts[it.id] = it }
        }
    }

    /** Persist all layouts to platform storage. */
    private fun persistAll() {
        storage.saveAllLayoutsJson(CustomLayoutSerializer.serializeAll(layouts.values.toList()))
    }

    fun getAll(): List<CustomLayout> = layouts.values.toList()

    fun getById(id: String): CustomLayout? = layouts[id]

    fun save(layout: CustomLayout): List<String> {
        val errors = validateLayout(layout)
        if (errors.isNotEmpty()) return errors
        layouts[layout.id] = layout
        persistAll()
        return emptyList()
    }

    fun delete(id: String) {
        layouts.remove(id)
        persistAll()
    }

    fun rename(id: String, newName: String): Boolean {
        val layout = layouts[id] ?: return false
        val trimmed = newName.trim()
        if (trimmed.isEmpty() || trimmed.length > 30) return false
        layouts[id] = layout.copy(name = trimmed)
        persistAll()
        return true
    }

    /**
     * Clone a built-in layout (Logical or Efficiency) into a new CustomLayout.
     */
    fun duplicateFromBuiltIn(sourceLayout: LayoutType, customName: String, sectionCount: Int = 8): CustomLayout {
        val logic = KeyboardLogic()
        val directions = if (sectionCount == 6) {
            logic.dialSectionMode = DialSectionMode.SIX_SECTION
            KeyboardLogic.directions6
        } else {
            logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
            KeyboardLogic.directions8
        }

        val normalChord = mutableMapOf<Direction, List<String>>()
        val shiftedChord = mutableMapOf<Direction, List<String>>()
        for (dir in directions) {
            normalChord[dir] = logic.getCharactersForDirection(dir, KeyboardMode.NORMAL, sourceLayout)
            shiftedChord[dir] = logic.getCharactersForDirection(dir, KeyboardMode.SHIFTED, sourceLayout)
        }

        // Single-swipe maps are the same for all built-in layouts
        val singleNormal = mutableMapOf<Direction, SingleSwipeBinding>()
        val singleShifted = mutableMapOf<Direction, SingleSwipeBinding>()
        for (dir in directions) {
            val normalResult = logic.getSingleSwipeResult(dir, KeyboardMode.NORMAL)
            if (normalResult != null) {
                singleNormal[dir] = when (normalResult) {
                    is String -> SingleSwipeBinding.Character(normalResult)
                    is InputAction -> SingleSwipeBinding.Action(normalResult)
                    else -> SingleSwipeBinding.Character(normalResult.toString())
                }
            }
            val shiftedResult = logic.getSingleSwipeResult(dir, KeyboardMode.SHIFTED)
            if (shiftedResult != null) {
                singleShifted[dir] = when (shiftedResult) {
                    is String -> SingleSwipeBinding.Character(shiftedResult)
                    is InputAction -> SingleSwipeBinding.Action(shiftedResult)
                    else -> SingleSwipeBinding.Character(shiftedResult.toString())
                }
            }
        }

        val id = generateId()
        return CustomLayout(
            id = id,
            name = customName.trim().ifEmpty { "Custom Layout" },
            normalChordMap = normalChord,
            shiftedChordMap = shiftedChord,
            singleSwipeNormalMap = singleNormal,
            singleSwipeShiftedMap = singleShifted,
            sectionCount = sectionCount
        )
    }

    /**
     * Create a new custom layout pre-populated with the Logical (A–Z) layout.
     */
    fun createBlank(name: String, sectionCount: Int = 8): CustomLayout {
        return duplicateFromBuiltIn(LayoutType.LOGICAL, name, sectionCount)
    }

    companion object {
        private var counter = 0L

        fun generateId(): String {
            counter++
            // Use current time approximation + counter to ensure uniqueness
            return "custom_${counter}_${(counter * 31 + 17).toString(36)}"
        }

        /**
         * Validate a custom layout and return a list of error messages (empty = valid).
         */
        fun validateLayout(layout: CustomLayout): List<String> {
            val errors = mutableListOf<String>()
            val sc = layout.sectionCount
            val directions = if (sc == 6) KeyboardLogic.directions6 else KeyboardLogic.directions8

            if (layout.name.isBlank()) {
                errors.add("Layout name cannot be empty")
            }
            if (layout.name.length > 30) {
                errors.add("Layout name must be 30 characters or fewer")
            }

            // Check chord maps have all required directions
            for (dir in directions) {
                if (dir !in layout.normalChordMap) {
                    errors.add("Normal chord map missing direction: $dir")
                }
                if (dir !in layout.shiftedChordMap) {
                    errors.add("Shifted chord map missing direction: $dir")
                }
            }

            // Check each direction has exactly the right number of entries
            for (dir in directions) {
                val normalList = layout.normalChordMap[dir]
                if (normalList != null && normalList.size != sc) {
                    errors.add("Normal chord map for $dir must have exactly $sc entries (has ${normalList.size})")
                }
                val shiftedList = layout.shiftedChordMap[dir]
                if (shiftedList != null && shiftedList.size != sc) {
                    errors.add("Shifted chord map for $dir must have exactly $sc entries (has ${shiftedList.size})")
                }
            }

            // Check for duplicate non-empty characters in normal map
            val allNormalChars = mutableSetOf<String>()
            for ((_, chars) in layout.normalChordMap) {
                for (c in chars) {
                    if (c.isNotEmpty()) {
                        if (c in allNormalChars) {
                            errors.add("Duplicate character in normal mode: '$c'")
                        }
                        allNormalChars.add(c)
                    }
                }
            }

            // Check for duplicate non-empty characters in shifted map
            val allShiftedChars = mutableSetOf<String>()
            for ((_, chars) in layout.shiftedChordMap) {
                for (c in chars) {
                    if (c.isNotEmpty()) {
                        if (c in allShiftedChars) {
                            errors.add("Duplicate character in shifted mode: '$c'")
                        }
                        allShiftedChars.add(c)
                    }
                }
            }

            return errors
        }
    }
}

/**
 * Platform-specific storage interface for custom layouts.
 * Android implements via DataStore/SharedPreferences, iOS via UserDefaults.
 */
interface CustomLayoutStorage {
    fun loadAllLayoutsJson(): String
    fun saveAllLayoutsJson(json: String)
}
