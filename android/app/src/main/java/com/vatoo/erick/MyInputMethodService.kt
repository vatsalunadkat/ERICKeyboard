package com.vatoo.erick

import android.inputmethodservice.InputMethodService
import android.hardware.input.InputManager
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.InputDevice
import kotlin.math.abs
import android.view.inputmethod.EditorInfo
import com.vatoo.erick.shared.ColorEntry
import com.vatoo.erick.shared.ColorPaletteType
import com.vatoo.erick.shared.CustomLayoutManager
import com.vatoo.erick.shared.DialSectionMode
import com.vatoo.erick.shared.InputAction
import com.vatoo.erick.shared.InputMode
import com.vatoo.erick.shared.KeyboardActionDelegate
import com.vatoo.erick.shared.KeyboardStateMachine
import com.vatoo.erick.shared.LayoutType
import com.vatoo.erick.shared.PredictionDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.ImageButton
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vatoo.erick.shared.ColorPalettes
import com.vatoo.erick.shared.Direction

// Note: If this shows red, use Alt+Enter to import classes from the Shared module (InputAction, KeyboardStateMachine, etc.)

class MyInputMethodService : InputMethodService(), KeyboardActionDelegate {

    private lateinit var leftJoystick: JoystickView
    private lateinit var rightJoystick: JoystickView
    private lateinit var previewContainer: FrameLayout
    private lateinit var previewCapsule: LinearLayout
    private lateinit var shiftIndicator: TextView
    private lateinit var suggestionBar: LinearLayout
    private lateinit var suggestionLabel: TextView
    private lateinit var suggestion1: TextView
    private lateinit var suggestion2: TextView
    private lateinit var suggestion3: TextView
    private var lastHighlightedIndex: Int = -1
    private var pendingSuggestions: List<String> = emptyList()
    private var currentPredictionDomain: PredictionDomain = PredictionDomain.GENERAL

    // --- Coroutine lifecycle management ---
    // Must provide a scope to the state machine; cancel all timer tasks when the IME is destroyed to prevent memory leaks
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var controllerDeadZone = PreferencesManager.DEFAULT_CONTROLLER_DEAD_ZONE
    private var controllerYAxisInverted = false
    private val inputManager by lazy { getSystemService(INPUT_SERVICE) as InputManager }
    // Cross-platform state machine from the Shared module
    private lateinit var stateMachine: KeyboardStateMachine
    private lateinit var preferencesManager: PreferencesManager
    private var connectedControllerName: String? = null
    private var currentControllerDeviceId: Int? = null
    private var isDispatchingControllerInput: Boolean = false
    private val controllerListener = object : InputManager.InputDeviceListener {
        override fun onInputDeviceAdded(deviceId: Int) = refreshControllerStatus()

        override fun onInputDeviceRemoved(deviceId: Int) = refreshControllerStatus()

        override fun onInputDeviceChanged(deviceId: Int) = refreshControllerStatus()
    }
    private lateinit var customLayoutManager: CustomLayoutManager
    private var currentThemeMode: String = PreferencesManager.THEME_SYSTEM
    private var currentFontPreference: String = PreferencesManager.FONT_SYSTEM
    private var keyboardRootView: View? = null
    private var hapticEnabled: Boolean = false
    private var soundsEnabled: Boolean = false
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }
    private val audioManager: AudioManager by lazy {
        getSystemService(AUDIO_SERVICE) as AudioManager
    }
    private val predictionProfilePreferences by lazy {
        getSharedPreferences("prediction_profile", MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        // When the IME is created, initialize the state machine and pass this as the delegate
        stateMachine = KeyboardStateMachine(this, serviceScope)
        stateMachine.setControllerDeadZone(controllerDeadZone)
        stateMachine.setControllerYAxisInverted(false)
        inputManager.registerInputDeviceListener(controllerListener, null)
        refreshControllerStatus()
        // Listen for layout preference changes and switch layouts in real-time (uses the same PreferencesManager as SettingsScreen)
        preferencesManager = PreferencesManager(this)
        customLayoutManager = CustomLayoutManager(preferencesManager.createCustomLayoutStorage())
        customLayoutManager.loadAll()

        // Combine layout type and custom layout ID so we can apply both together
        preferencesManager.layoutType.combine(preferencesManager.customLayoutId) { layout, customId ->
            Pair(layout, customId)
        }.onEach { (layout, customId) ->
            val layoutType = when (layout) {
                PreferencesManager.LAYOUT_EFFICIENCY -> LayoutType.EFFICIENCY
                PreferencesManager.LAYOUT_CUSTOM -> LayoutType.CUSTOM
                else -> LayoutType.LOGICAL
            }
            stateMachine.setLayoutType(layoutType)
            if (layoutType == LayoutType.CUSTOM && customId.isNotEmpty()) {
                customLayoutManager.loadAll()  // Reload in case layouts were edited in settings
                val cl = customLayoutManager.getById(customId)
                stateMachine.activeCustomLayout = cl
                if (::leftJoystick.isInitialized) {
                    leftJoystick.customCharsNormal = cl?.normalChordMap
                    leftJoystick.customCharsShifted = cl?.shiftedChordMap
                }
                if (::rightJoystick.isInitialized) {
                    rightJoystick.customCharsNormal = cl?.normalChordMap
                    rightJoystick.customCharsShifted = cl?.shiftedChordMap
                }
            } else {
                stateMachine.activeCustomLayout = null
                if (::leftJoystick.isInitialized) {
                    leftJoystick.customCharsNormal = null
                    leftJoystick.customCharsShifted = null
                }
                if (::rightJoystick.isInitialized) {
                    rightJoystick.customCharsNormal = null
                    rightJoystick.customCharsShifted = null
                }
            }
            if (::leftJoystick.isInitialized) leftJoystick.layoutType = layoutType
            if (::rightJoystick.isInitialized) rightJoystick.layoutType = layoutType
        }.launchIn(serviceScope)

        preferencesManager.leftHandedMode.onEach { isLeftHanded ->
            stateMachine.setLeftHandedMode(isLeftHanded)
            if (::leftJoystick.isInitialized && ::rightJoystick.isInitialized) {
                leftJoystick.isRightSide = isLeftHanded
                rightJoystick.isRightSide = !isLeftHanded
                leftJoystick.invalidate()
                rightJoystick.invalidate()
            }
        }.launchIn(serviceScope)

        preferencesManager.colorblindMode.combine(preferencesManager.colorPalette) { enabled, palette ->
            if (enabled) {
                when (palette) {
                    PreferencesManager.PALETTE_DEUTERANOPIA -> ColorPaletteType.DEUTERANOPIA
                    PreferencesManager.PALETTE_PROTANOPIA -> ColorPaletteType.PROTANOPIA
                    PreferencesManager.PALETTE_TRITANOPIA -> ColorPaletteType.TRITANOPIA
                    else -> ColorPaletteType.OKABE_ITO
                }
            } else {
                when (palette) {
                    PreferencesManager.PALETTE_PASTEL -> ColorPaletteType.PASTEL
                    PreferencesManager.PALETTE_CUSTOM -> ColorPaletteType.CUSTOM
                    else -> ColorPaletteType.DEFAULT
                }
            }
        }.onEach { paletteType ->
            stateMachine.setColorPalette(paletteType)
            if (::leftJoystick.isInitialized) leftJoystick.colorPaletteType = paletteType
            if (::rightJoystick.isInitialized) rightJoystick.colorPaletteType = paletteType
        }.launchIn(serviceScope)

        // Monitor custom palette color changes
        preferencesManager.customPaletteColors.onEach { colorsStr ->
            val hexList = colorsStr.split(",").map { it.trim() }
            val names = listOf("Color 1", "Color 2", "Color 3", "Color 4", "Color 5", "Color 6", "Color 7", "Color 8")
            val entries = hexList.mapIndexed { i, hex ->
                ColorEntry(names.getOrElse(i) { "Color ${i + 1}" }, hex)
            }
            if (entries.size == 8) {
                ColorPalettes.setCustomPalette(entries)
            }
            if (entries.size >= 6) {
                ColorPalettes.setCustomPalette6(entries.take(6))
            }
            if (::leftJoystick.isInitialized) leftJoystick.invalidate()
            if (::rightJoystick.isInitialized) rightJoystick.invalidate()
        }.launchIn(serviceScope)

        // Monitor theme mode changes
        preferencesManager.themeMode.onEach { mode ->
            currentThemeMode = mode
            applyKeyboardTheme()
        }.launchIn(serviceScope)

        // Monitor font preference changes
        preferencesManager.fontPreference.onEach { font ->
            currentFontPreference = font
            applyKeyboardFont()
        }.launchIn(serviceScope)

        // Monitor haptic + sound preferences
        preferencesManager.hapticFeedback.onEach { hapticEnabled = it }.launchIn(serviceScope)
        preferencesManager.typingSounds.onEach { soundsEnabled = it }.launchIn(serviceScope)

        // Monitor 6-section dial mode preference
        preferencesManager.sixSectionDial.onEach { enabled ->
            val mode = if (enabled) DialSectionMode.SIX_SECTION else DialSectionMode.EIGHT_SECTION
            stateMachine.setDialSectionMode(mode)
            if (::leftJoystick.isInitialized) {
                leftJoystick.sixSectionMode = enabled
                leftJoystick.invalidate()
            }
            if (::rightJoystick.isInitialized) {
                rightJoystick.sixSectionMode = enabled
                rightJoystick.invalidate()
            }
        }.launchIn(serviceScope)

        // Monitor input mode preference
        preferencesManager.inputMode.onEach { mode ->
            val inputMode = when (mode) {
                PreferencesManager.INPUT_MODE_CONFIRM -> InputMode.CONFIRM
                PreferencesManager.INPUT_MODE_ASSISTED -> InputMode.ASSISTED
                else -> InputMode.INSTANT
            }
            stateMachine.setInputMode(inputMode)
        }.launchIn(serviceScope)

        preferencesManager.predictionDomain.onEach { domainKey ->
            currentPredictionDomain = when (domainKey) {
                PreferencesManager.PREDICTION_DOMAIN_CONVERSATION -> PredictionDomain.CONVERSATION
                PreferencesManager.PREDICTION_DOMAIN_PRODUCTIVITY -> PredictionDomain.PRODUCTIVITY
                PreferencesManager.PREDICTION_DOMAIN_ACCESSIBILITY -> PredictionDomain.ACCESSIBILITY
                PreferencesManager.PREDICTION_DOMAIN_GAMING -> PredictionDomain.GAMING
                else -> PredictionDomain.GENERAL
            }
            stateMachine.setPredictionDomain(currentPredictionDomain)
            if (::suggestionBar.isInitialized) updateSuggestionBar()
        }.launchIn(serviceScope)

        preferencesManager.controllerDeadZone.onEach { deadZone ->
            controllerDeadZone = deadZone
            stateMachine.setControllerDeadZone(deadZone)
        }.launchIn(serviceScope)

        preferencesManager.controllerYAxisInverted.onEach { inverted ->
            controllerYAxisInverted = inverted
            stateMachine.setControllerYAxisInverted(inverted)
        }.launchIn(serviceScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        inputManager.unregisterInputDeviceListener(controllerListener)
        serviceJob.cancel() // When the IME is destroyed, clean up all coroutine timers
    }

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_simple, null)

        leftJoystick = view.findViewById(R.id.left_joystick)
        rightJoystick = view.findViewById(R.id.right_joystick)

        // Apply left-handed mode to the newly created joystick views
        val isLeftHanded = stateMachine.leftHandedMode
        leftJoystick.isRightSide = isLeftHanded
        rightJoystick.isRightSide = !isLeftHanded

        // Apply current layout type to the newly created joystick views
        val currentLayout = stateMachine.currentLayoutType
        leftJoystick.layoutType = currentLayout
        rightJoystick.layoutType = currentLayout

        // Apply custom layout data if active
        val cl = stateMachine.activeCustomLayout
        if (currentLayout == LayoutType.CUSTOM && cl != null) {
            leftJoystick.customCharsNormal = cl.normalChordMap
            leftJoystick.customCharsShifted = cl.shiftedChordMap
            rightJoystick.customCharsNormal = cl.normalChordMap
            rightJoystick.customCharsShifted = cl.shiftedChordMap
        }

        // Apply current 6-section dial mode to the newly created joystick views
        val isSixSection = stateMachine.getDialSectionMode() == DialSectionMode.SIX_SECTION
        leftJoystick.sixSectionMode = isSixSection
        rightJoystick.sixSectionMode = isSixSection

        // Apply current color palette to the newly created joystick views
        val currentPalette = stateMachine.currentPaletteType
        leftJoystick.colorPaletteType = currentPalette
        rightJoystick.colorPaletteType = currentPalette

        previewContainer = view.findViewById(R.id.live_preview_container)
        previewCapsule = view.findViewById(R.id.live_preview_capsule)
        shiftIndicator = view.findViewById(R.id.shift_indicator)
        suggestionBar = view.findViewById(R.id.suggestion_bar)
        suggestionLabel = view.findViewById(R.id.suggestion_label)
        suggestion1 = view.findViewById(R.id.suggestion_1)
        suggestion2 = view.findViewById(R.id.suggestion_2)
        suggestion3 = view.findViewById(R.id.suggestion_3)

        // Wire suggestion tap handlers
        suggestion1.setOnClickListener { onSuggestionTapped(0) }
        suggestion2.setOnClickListener { onSuggestionTapped(1) }
        suggestion3.setOnClickListener { onSuggestionTapped(2) }

        keyboardRootView = view
        applyKeyboardTheme()
        applyKeyboardFont()

        leftJoystick.setOnTouchListener { v, event ->
            v.performClick()
            dispatchTouchToStateMachine(event, isLeft = true, joystick = leftJoystick)
            true
        }

        rightJoystick.setOnTouchListener { v, event ->
            v.performClick()
            dispatchTouchToStateMachine(event, isLeft = false, joystick = rightJoystick)
            true
        }
        //Setting
        val settingsBtn = view.findViewById<ImageButton>(R.id.btn_settings)
        settingsBtn?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        // Add bottom padding for 3-button navigation bar overlap
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, navBarInsets.bottom)
            insets
        }

        return view
    }
    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (
            event.action == MotionEvent.ACTION_MOVE &&
            event.isFromSource(InputDevice.SOURCE_JOYSTICK) &&
            event.device?.isCompatibleController() == true
        ) {
            if (!::leftJoystick.isInitialized || !::rightJoystick.isInitialized) {
                return super.onGenericMotionEvent(event)
            }
            val leftX = event.getCenteredAxisValue(MotionEvent.AXIS_X)
            val leftY = event.getCenteredAxisValue(MotionEvent.AXIS_Y)
            val rightX = event.getPreferredAxisValue(MotionEvent.AXIS_Z, MotionEvent.AXIS_RX)
            val rightY = event.getPreferredAxisValue(MotionEvent.AXIS_RZ, MotionEvent.AXIS_RY)

            val effectiveLeftY = if (controllerYAxisInverted) -leftY else leftY
            val effectiveRightY = if (controllerYAxisInverted) -rightY else rightY

            leftJoystick.updateThumbFromController(leftX, effectiveLeftY, controllerDeadZone)
            rightJoystick.updateThumbFromController(rightX, effectiveRightY, controllerDeadZone)

            currentControllerDeviceId = event.deviceId
            dispatchControllerInput(leftX, effectiveLeftY, rightX, effectiveRightY)
            syncAssistedLetterLock(
                letterJoystickIsActivelyDriven = getLetterJoystick().activeDirection != Direction.NONE
            )
            leftJoystick.keyboardMode = stateMachine.currentMode
            rightJoystick.keyboardMode = stateMachine.currentMode
            updateLivePreview()
            return true
        }

        return super.onGenericMotionEvent(event)
    }
    private fun refreshControllerStatus() {
        val controller = InputDevice.getDeviceIds()
            .asSequence()
            .mapNotNull { InputDevice.getDevice(it) }
            .firstOrNull { it.isCompatibleController() }
        connectedControllerName = controller?.name
        currentControllerDeviceId = controller?.id

        if (connectedControllerName == null) {
            stateMachine.handleControllerInput(0f, 0f, 0f, 0f)
            if (::leftJoystick.isInitialized) {
                leftJoystick.resetThumb()
            }
            if (::rightJoystick.isInitialized) {
                rightJoystick.resetThumb()
            }
            updateLivePreview()
        }
    }
    private fun InputDevice.isCompatibleController(): Boolean {
        val isGamepad = sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD
        val isJoystick = sources and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK
        return isGamepad || isJoystick
    }
    private fun MotionEvent.getCenteredAxisValue(axis: Int): Float {
        return getAxisValue(axis).coerceIn(-1f, 1f)
    }

    private fun MotionEvent.getPreferredAxisValue(primaryAxis: Int, fallbackAxis: Int): Float {
        val primary = getCenteredAxisValue(primaryAxis)
        val fallback = getCenteredAxisValue(fallbackAxis)
        return if (abs(primary) >= abs(fallback)) primary else fallback
    }

    private fun dispatchControllerInput(leftX: Float, leftY: Float, rightX: Float, rightY: Float) {
        isDispatchingControllerInput = true
        try {
            stateMachine.handleControllerInput(leftX, leftY, rightX, rightY)
        } finally {
            isDispatchingControllerInput = false
        }
    }

    // --- Core: translate Android touch events and dispatch to the state machine ---
    private fun dispatchTouchToStateMachine(event: MotionEvent, isLeft: Boolean, joystick: JoystickView) {
        // Calculate offset relative to the joystick center
        val dx = event.x - (joystick.width / 2f)
        val dy = event.y - (joystick.height / 2f)

        val actionMasked = event.actionMasked
        val isDownOrMove = actionMasked == MotionEvent.ACTION_DOWN || actionMasked == MotionEvent.ACTION_MOVE
        val isUpOrCancel = actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL

        // 1. Update the UI rendering
        if (isDownOrMove) {
            joystick.updateThumb(dx, dy)
        } else if (isUpOrCancel) {
            joystick.resetThumb()
        }

        // 2. Dispatch data to the cross-platform state machine (it doesn't need to know about MotionEvent)
        stateMachine.handleTouch(dx, dy, isLeft, isDownOrMove, isUpOrCancel)

        // 3. In one-handed mode, sync the left dial to show the locked direction
        //    When the left dial is idle (not being actively touched), show the locked direction.
        //    When a chord fires and the lock clears, this resets the highlight.
        val isThisTheLetterJoystick = (isLeft && !stateMachine.leftHandedMode) || (!isLeft && stateMachine.leftHandedMode)
        syncAssistedLetterLock(letterJoystickIsActivelyDriven = isThisTheLetterJoystick && isDownOrMove)

        // 4. Update the action-wheel joystick mode (whichever currently shows right-side content)
        val actionJoystick = if (stateMachine.leftHandedMode) leftJoystick else rightJoystick
        actionJoystick.keyboardMode = stateMachine.currentMode

        updateLivePreview()
    }

    private fun isEffectiveDarkMode(): Boolean {
        return when (currentThemeMode) {
            PreferencesManager.THEME_DARK -> true
            PreferencesManager.THEME_LIGHT -> false
            else -> {
                val nightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
                nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    private fun applyKeyboardTheme() {
        val root = keyboardRootView ?: return
        val isDark = isEffectiveDarkMode()

        // Keyboard background
        root.setBackgroundColor(if (isDark) Color.parseColor("#1E1E1E") else Color.parseColor("#ECEFF1"))

        // Preview capsule background
        if (::previewCapsule.isInitialized) {
            previewCapsule.background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = 999f * resources.displayMetrics.density
                setColor(if (isDark) Color.argb(245, 50, 50, 50) else Color.argb(245, 255, 255, 255))
            }
            // Rebuild preview views so stroke color matches new theme
            previewCapsule.removeAllViews()
        }

        // Joystick views
        if (::leftJoystick.isInitialized) {
            leftJoystick.isDarkMode = isDark
            leftJoystick.invalidate()
        }
        if (::rightJoystick.isInitialized) {
            rightJoystick.isDarkMode = isDark
            rightJoystick.invalidate()
        }

        // Re-apply shift indicator colors for the new theme
        if (::leftJoystick.isInitialized) {
            updateShiftIndicator(stateMachine.currentMode)
        }
    }

    private fun resolveTypeface(): Typeface? {
        return when (currentFontPreference) {
            PreferencesManager.FONT_VERDANA -> Typeface.SANS_SERIF
            PreferencesManager.FONT_GEORGIA -> Typeface.SERIF
            PreferencesManager.FONT_OPENDYSLEXIC -> {
                try {
                    ResourcesCompat.getFont(this, R.font.opendyslexic_regular)
                } catch (_: Exception) {
                    null
                }
            }
            else -> null // system default
        }
    }

    private fun applyKeyboardFont() {
        val tf = resolveTypeface()
        if (::leftJoystick.isInitialized) leftJoystick.customTypeface = tf
        if (::rightJoystick.isInitialized) rightJoystick.customTypeface = tf
        // Preview bar TextViews will pick up the font on next updateLivePreview() rebuild
        if (::previewCapsule.isInitialized) {
            previewCapsule.removeAllViews()
        }
        updateLivePreview()
    }

    private fun updateLivePreview() {
        if (!::leftJoystick.isInitialized || !::rightJoystick.isInitialized || !::previewContainer.isInitialized) return
        // In left-handed mode the letter-group dial is the physical right joystick,
        // and the color dial is the physical left joystick.
        val isLH = stateMachine.leftHandedMode
        val letterDir = if (isLH) rightJoystick.activeDirection else leftJoystick.activeDirection
        val colorDir  = if (isLH) leftJoystick.activeDirection  else rightJoystick.activeDirection

        val logicalPreviewDirs = stateMachine.getDirections()
        val visualPreviewDirs = stateMachine.getPreviewDirections()

        // Determine preview data: left-dial hold, right-dial hold, or nothing
        data class PreviewChar(val text: String, val colorHex: String, val dirForColor: Direction)
        val previewChars = mutableListOf<PreviewChar>()
        var highlightIndex = -1

        if (letterDir != Direction.NONE) {
            // Left-dial hold: keep characters in logical chord order even when the 6-section
            // dial is visually rotated, so rows like a-f remain intuitive in the preview bar.
            val chars = stateMachine.getCharactersForDirection(letterDir)
            val charsByDirection = logicalPreviewDirs.mapIndexed { index, dir ->
                dir to chars.getOrNull(index).orEmpty()
            }.toMap()
            for (dirForChar in logicalPreviewDirs) {
                val charStr = charsByDirection[dirForChar].orEmpty()
                if (charStr.isBlank()) continue
                val colorHex = if (stateMachine.getDialSectionMode() == DialSectionMode.SIX_SECTION) ColorPalettes.getColorForDirectionHex6(dirForChar, stateMachine.currentPaletteType) else ColorPalettes.getColorForDirectionHex(dirForChar, stateMachine.currentPaletteType)
                previewChars.add(PreviewChar(charStr, colorHex, dirForChar))
                if (dirForChar == colorDir && colorDir != Direction.NONE) {
                    highlightIndex = previewChars.size - 1
                }
            }
        } else if (colorDir != Direction.NONE) {
            // Right-dial-only hold: show character at this color position across all left-dial groups
            val positionCharsByDirection = stateMachine.getCharactersAtPosition(colorDir).toMap()
            val colorHex = if (stateMachine.getDialSectionMode() == DialSectionMode.SIX_SECTION) ColorPalettes.getColorForDirectionHex6(colorDir, stateMachine.currentPaletteType) else ColorPalettes.getColorForDirectionHex(colorDir, stateMachine.currentPaletteType)
            for (dir in visualPreviewDirs) {
                val ch = positionCharsByDirection[dir] ?: continue
                previewChars.add(PreviewChar(ch, colorHex, colorDir))
            }
            // No specific highlight for right-dial-only preview
        }

        if (previewChars.isEmpty()) {
            previewCapsule.visibility = View.GONE
            lastHighlightedIndex = -1
            updateSuggestionBar()
            return
        }

        previewCapsule.visibility = View.VISIBLE
        // Hide suggestions while preview is active
        if (::suggestionBar.isInitialized) suggestionBar.visibility = View.GONE

        // Rebuild capsule child views if count changed
        if (previewCapsule.childCount != previewChars.size) {
            previewCapsule.removeAllViews()
            val spacingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
            val isDark = isEffectiveDarkMode()
            for (i in previewChars.indices) {
                val tv = OutlinedTextView(this).apply {
                    textSize = 17f
                    val baseTf = resolveTypeface() ?: Typeface.DEFAULT
                    typeface = Typeface.create(baseTf, Typeface.BOLD)
                    gravity = Gravity.CENTER
                    minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
                    includeFontPadding = true
                    strokeColor = if (isDark) Color.WHITE else Color.BLACK
                    strokeWidthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.2f, resources.displayMetrics)
                }
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    if (i > 0) marginStart = spacingPx
                }
                previewCapsule.addView(tv, lp)
            }
        }

        // Update each character view
        for (i in previewChars.indices) {
            val tv = previewCapsule.getChildAt(i) as? TextView ?: continue
            val pc = previewChars[i]
            tv.text = pc.text
            tv.setTextColor(Color.parseColor(pc.colorHex))

            val isHighlighted = (i == highlightIndex)
            val targetSize = if (isHighlighted) 21f else 17f
            val targetScale = if (isHighlighted) 1.08f else 1.0f
            val targetTypeface = if (isHighlighted) {
                val baseTf = resolveTypeface() ?: Typeface.DEFAULT
                Typeface.create(baseTf, Typeface.BOLD)
            } else {
                val baseTf = resolveTypeface() ?: Typeface.DEFAULT
                Typeface.create(baseTf, Typeface.BOLD)
            }

            // Animate size and scale
            if ((i == lastHighlightedIndex || i == highlightIndex) && lastHighlightedIndex != highlightIndex) {
                tv.animate()
                    .scaleX(targetScale)
                    .scaleY(targetScale)
                    .setDuration(120)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
                tv.textSize = targetSize
            } else {
                tv.scaleX = targetScale
                tv.scaleY = targetScale
                tv.textSize = targetSize
            }
            tv.typeface = targetTypeface
        }

        lastHighlightedIndex = highlightIndex
    }

    private fun getLetterJoystick(): JoystickView {
        return if (stateMachine.leftHandedMode) rightJoystick else leftJoystick
    }

    private fun syncAssistedLetterLock(letterJoystickIsActivelyDriven: Boolean) {
        if (stateMachine.inputMode != InputMode.ASSISTED || !::leftJoystick.isInitialized || !::rightJoystick.isInitialized) {
            return
        }

        if (!letterJoystickIsActivelyDriven) {
            getLetterJoystick().setLockedDirection(stateMachine.lockedLeftDir)
        }
    }

    // ==========================================
    // KeyboardActionDelegate implementation (receive commands from the state machine and execute)
    // ==========================================

    override fun commitText(text: String) {
        currentInputConnection?.commitText(text, 1)
        performHaptic(strong = false)
        playClickSound(soft = true)
    }

    override fun sendInputAction(action: InputAction) {
        if (action == InputAction.DELETE_WORD) {
            deleteWordBackward()
            performHaptic(strong = true)
            playClickSound(soft = false)
            return
        }
        val keyCode = when (action) {
            InputAction.SPACE -> KeyEvent.KEYCODE_SPACE
            InputAction.ENTER -> KeyEvent.KEYCODE_ENTER
            InputAction.BACKSPACE -> KeyEvent.KEYCODE_DEL
            InputAction.DELETE_FORWARD -> KeyEvent.KEYCODE_FORWARD_DEL
            InputAction.MOVE_HOME -> KeyEvent.KEYCODE_MOVE_HOME
            InputAction.MOVE_END -> KeyEvent.KEYCODE_MOVE_END
            InputAction.DPAD_UP -> KeyEvent.KEYCODE_DPAD_UP
            InputAction.DPAD_DOWN -> KeyEvent.KEYCODE_DPAD_DOWN
            InputAction.DPAD_LEFT -> KeyEvent.KEYCODE_DPAD_LEFT
            InputAction.DPAD_RIGHT -> KeyEvent.KEYCODE_DPAD_RIGHT
            InputAction.PAGE_UP -> KeyEvent.KEYCODE_PAGE_UP
            InputAction.PAGE_DOWN -> KeyEvent.KEYCODE_PAGE_DOWN
            InputAction.TAB -> KeyEvent.KEYCODE_TAB
            InputAction.TOGGLE_SHIFT, InputAction.TOGGLE_CAPS, InputAction.TOGGLE_SYMBOLS -> -1
            else -> -1
        }

        if (keyCode != -1) {
            currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
            performHaptic(strong = true)
            playClickSound(soft = false)
        }
    }

    private fun performHaptic(strong: Boolean) {
        if (!hapticEnabled) return
        if (isDispatchingControllerInput) {
            performControllerHaptic(strong)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val amplitude = if (strong) 128 else 80
            val duration = if (strong) 25L else 18L
            vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(if (strong) 25L else 18L)
        }
    }

    private fun performControllerHaptic(strong: Boolean): Boolean {
        val controllerId = currentControllerDeviceId ?: return false
        val controllerDevice = InputDevice.getDevice(controllerId) ?: return false
        val controllerVibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            controllerDevice.vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            controllerDevice.vibrator
        }

        if (!controllerVibrator.hasVibrator()) return false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val amplitude = if (strong) 128 else 80
            val duration = if (strong) 25L else 18L
            controllerVibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
        } else {
            @Suppress("DEPRECATION")
            controllerVibrator.vibrate(if (strong) 25L else 18L)
        }

        return true
    }

    private fun playClickSound(soft: Boolean) {
        if (!soundsEnabled) return
        val volume = if (soft) 0.2f else 0.25f
        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, volume)
    }

    private fun deleteWordBackward() {
        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(64, 0)?.toString() ?: return
        if (before.isEmpty()) return
        // Walk backwards: skip trailing whitespace, then skip word characters
        var i = before.length
        while (i > 0 && before[i - 1].isWhitespace()) i--
        while (i > 0 && !before[i - 1].isWhitespace()) i--
        val charsToDelete = before.length - i
        if (charsToDelete > 0) {
            ic.deleteSurroundingText(charsToDelete, 0)
        }
    }

    override fun onModeChanged(mode: com.vatoo.erick.shared.KeyboardMode) {
        if (::leftJoystick.isInitialized) {
            leftJoystick.keyboardMode = mode
        }
        if (::rightJoystick.isInitialized) {
            rightJoystick.keyboardMode = mode
        }
        updateShiftIndicator(mode)
    }

    override fun onSuggestionsUpdated(suggestions: List<String>) {
        pendingSuggestions = suggestions
        updateSuggestionBar()
    }

    override fun getCurrentWordPrefix(): String {
        val before = currentInputConnection?.getTextBeforeCursor(64, 0)?.toString() ?: return ""
        if (before.isEmpty()) return ""
        // Walk backward to find the start of the current word
        var i = before.length
        while (i > 0 && (before[i - 1].isLetterOrDigit() || before[i - 1] == '\'')) {
            i--
        }
        return before.substring(i)
    }

    override fun loadPredictionProfile(): String {
        return predictionProfilePreferences.getString("serialized_prediction_profile", "") ?: ""
    }

    override fun savePredictionProfile(serializedProfile: String) {
        predictionProfilePreferences.edit()
            .putString("serialized_prediction_profile", serializedProfile)
            .apply()
    }

    private fun onSuggestionTapped(index: Int) {
        if (index >= pendingSuggestions.size) return
        val suggestion = pendingSuggestions[index]
        val ic = currentInputConnection ?: return
        val result = stateMachine.acceptSuggestion(
            suggestion = suggestion,
            textBeforeCursor = ic.getTextBeforeCursor(64, 0)?.toString() ?: "",
            textAfterCursor = ic.getTextAfterCursor(64, 0)?.toString() ?: ""
        )
        // Delete the partial word
        if (result.charsToDelete > 0) {
            ic.deleteSurroundingText(result.charsToDelete, 0)
        }
        ic.commitText(result.leadingText + result.suggestion + result.trailingText, 1)
    }

    private fun updateSuggestionBar() {
        if (!::suggestionBar.isInitialized) return
        val showSuggestions = stateMachine.areBothDialsAtHome() && pendingSuggestions.isNotEmpty()
        if (showSuggestions) {
            previewCapsule.visibility = View.GONE
            suggestionBar.visibility = View.VISIBLE
            val isDark = isEffectiveDarkMode()
            val textColor = if (isDark) Color.WHITE else Color.parseColor("#333333")
            suggestionLabel.text = suggestionContextLabel()
            suggestionLabel.setTextColor(textColor)
            val views = listOf(suggestion1, suggestion2, suggestion3)
            for (i in views.indices) {
                if (i < pendingSuggestions.size) {
                    views[i].text = pendingSuggestions[i]
                    views[i].setTextColor(textColor)
                    views[i].visibility = View.VISIBLE
                } else {
                    views[i].text = ""
                    views[i].visibility = View.INVISIBLE
                }
            }
        } else {
            suggestionBar.visibility = View.GONE
        }
    }

    private fun suggestionContextLabel(): String {
        val baseLabel = if (stateMachine.isNextWordMode) {
            "Next"
        } else {
            val prefix = getCurrentWordPrefix().lowercase()
            val hasCorrection = pendingSuggestions.any { !it.lowercase().startsWith(prefix) }
            if (hasCorrection) "Complete/Correct" else "Complete"
        }
        val domainLabel = when (currentPredictionDomain) {
            PredictionDomain.CONVERSATION -> "Chat"
            PredictionDomain.PRODUCTIVITY -> "Work"
            PredictionDomain.ACCESSIBILITY -> "Support"
            PredictionDomain.GAMING -> "Gaming"
            PredictionDomain.GENERAL -> ""
        }
        return if (domainLabel.isBlank()) baseLabel else "$baseLabel • $domainLabel"
    }

    private fun updateShiftIndicator(mode: com.vatoo.erick.shared.KeyboardMode) {
        if (!::shiftIndicator.isInitialized) return
        val isDark = isEffectiveDarkMode()
        when (mode) {
            com.vatoo.erick.shared.KeyboardMode.SHIFTED -> {
                shiftIndicator.text = "↑"
                shiftIndicator.setTextColor(if (isDark) Color.WHITE else Color.DKGRAY)
                shiftIndicator.background = null
                shiftIndicator.visibility = View.VISIBLE
                shiftIndicator.contentDescription = "Shift mode active"
            }
            com.vatoo.erick.shared.KeyboardMode.CAPS_LOCKED -> {
                shiftIndicator.text = "↑↑"
                shiftIndicator.setTextColor(Color.parseColor("#D32F2F"))
                shiftIndicator.background = null
                shiftIndicator.visibility = View.VISIBLE
                shiftIndicator.contentDescription = "Caps Lock active"
            }
            com.vatoo.erick.shared.KeyboardMode.SYMBOLS -> {
                shiftIndicator.text = "#"
                shiftIndicator.setTextColor(Color.parseColor("#FF6F00"))
                shiftIndicator.background = null
                shiftIndicator.visibility = View.VISIBLE
                shiftIndicator.contentDescription = "Symbols mode active"
            }
            com.vatoo.erick.shared.KeyboardMode.SYMBOLS_SHIFTED -> {
                shiftIndicator.text = "#↑"
                shiftIndicator.setTextColor(Color.parseColor("#FF6F00"))
                shiftIndicator.background = null
                shiftIndicator.visibility = View.VISIBLE
                shiftIndicator.contentDescription = "Symbols shifted mode active"
            }
            else -> {
                shiftIndicator.visibility = View.GONE
            }
        }
    }

    // --- Prevent fullscreen extract mode (four-layer firewall — keep as-is) ---
    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        attribute?.let { it.imeOptions = it.imeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI }
        super.onStartInput(attribute, restarting)
    }
    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        info?.let { it.imeOptions = it.imeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI }
        super.onStartInputView(info, restarting)
    }
    override fun onEvaluateFullscreenMode(): Boolean = false
    override fun onUpdateExtractingVisibility(ei: EditorInfo?) { setExtractViewShown(false) }
    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }


}
