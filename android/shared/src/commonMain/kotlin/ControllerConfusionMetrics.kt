package com.vatoo.erick.shared

enum class ControllerConfusionType {
    EXACT_MATCH,
    ADJACENT_SLIP,
    MIRROR_SLIP,
    DEAD_ZONE_JITTER,
    OTHER_MISMATCH,
    SNAP_BACK_REVERSAL,
}

data class ControllerConfusionDrillSample(
    val expectedDirection: Direction,
    val resolvedDirection: Direction,
    val confusionType: ControllerConfusionType,
    val deadZoneBand: String,
)

data class ControllerPassiveSignal(
    val confusionType: ControllerConfusionType,
    val deadZoneBand: String,
)

object ControllerConfusionAnalyzer {
    fun directionsForMode(dialSectionMode: DialSectionMode): List<Direction> {
        return when (dialSectionMode) {
            DialSectionMode.SIX_SECTION -> listOf(
                Direction.N,
                Direction.NE,
                Direction.SE,
                Direction.S,
                Direction.SW,
                Direction.NW,
            )

            DialSectionMode.EIGHT_SECTION -> listOf(
                Direction.N,
                Direction.NE,
                Direction.E,
                Direction.SE,
                Direction.S,
                Direction.SW,
                Direction.W,
                Direction.NW,
            )
        }
    }

    fun classifyDrillSample(
        expectedDirection: Direction,
        snapshot: ControllerStickSnapshot,
        deadZone: Float,
        dialSectionMode: DialSectionMode,
    ): ControllerConfusionDrillSample {
        val resolvedDirection = snapshot.direction
        val confusionType = when {
            !snapshot.isActive || resolvedDirection == Direction.NONE -> ControllerConfusionType.DEAD_ZONE_JITTER
            expectedDirection == resolvedDirection -> ControllerConfusionType.EXACT_MATCH
            angularDistance(expectedDirection, resolvedDirection, dialSectionMode) == 1 -> ControllerConfusionType.ADJACENT_SLIP
            isMirrorDirection(expectedDirection, resolvedDirection, dialSectionMode) -> ControllerConfusionType.MIRROR_SLIP
            else -> ControllerConfusionType.OTHER_MISMATCH
        }

        return ControllerConfusionDrillSample(
            expectedDirection = expectedDirection,
            resolvedDirection = resolvedDirection,
            confusionType = confusionType,
            deadZoneBand = deadZoneBand(deadZone),
        )
    }

    fun detectSnapBackReversal(
        previousDirection: Direction,
        lastDirectionBeforeRelease: Direction,
        deadZone: Float,
    ): ControllerPassiveSignal? {
        if (
            previousDirection == Direction.NONE ||
            lastDirectionBeforeRelease == Direction.NONE ||
            previousDirection == lastDirectionBeforeRelease
        ) {
            return null
        }

        return ControllerPassiveSignal(
            confusionType = ControllerConfusionType.SNAP_BACK_REVERSAL,
            deadZoneBand = deadZoneBand(deadZone),
        )
    }

    fun deadZoneBand(deadZone: Float): String {
        val clamped = deadZone.coerceIn(0f, 1f)
        val start = ((clamped * 100f).toInt() / 5) * 5
        val end = (start + 5).coerceAtMost(100)
        return String.format("%02d-%02d%%", start, end)
    }

    private fun angularDistance(
        expectedDirection: Direction,
        resolvedDirection: Direction,
        dialSectionMode: DialSectionMode,
    ): Int {
        val directions = directionsForMode(dialSectionMode)
        val expectedIndex = directions.indexOf(expectedDirection)
        val resolvedIndex = directions.indexOf(resolvedDirection)
        if (expectedIndex == -1 || resolvedIndex == -1) {
            return Int.MAX_VALUE
        }
        val delta = kotlin.math.abs(expectedIndex - resolvedIndex)
        return minOf(delta, directions.size - delta)
    }

    private fun isMirrorDirection(
        expectedDirection: Direction,
        resolvedDirection: Direction,
        dialSectionMode: DialSectionMode,
    ): Boolean {
        val directions = directionsForMode(dialSectionMode)
        return angularDistance(expectedDirection, resolvedDirection, dialSectionMode) == directions.size / 2
    }
}