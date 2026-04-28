package com.vatoo.erick.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ControllerConfusionMetricsTest {

    @Test
    fun classifiesAdjacentSlipInEightSectionMode() {
        val sample = ControllerConfusionAnalyzer.classifyDrillSample(
            expectedDirection = Direction.N,
            snapshot = activeSnapshot(Direction.NE),
            deadZone = 0.25f,
            dialSectionMode = DialSectionMode.EIGHT_SECTION,
        )

        assertEquals(ControllerConfusionType.ADJACENT_SLIP, sample.confusionType)
    }

    @Test
    fun classifiesMirrorSlipInSixSectionMode() {
        val sample = ControllerConfusionAnalyzer.classifyDrillSample(
            expectedDirection = Direction.N,
            snapshot = activeSnapshot(Direction.S),
            deadZone = 0.25f,
            dialSectionMode = DialSectionMode.SIX_SECTION,
        )

        assertEquals(ControllerConfusionType.MIRROR_SLIP, sample.confusionType)
    }

    @Test
    fun inactiveSnapshotCountsAsDeadZoneJitter() {
        val sample = ControllerConfusionAnalyzer.classifyDrillSample(
            expectedDirection = Direction.NE,
            snapshot = ControllerStickSnapshot(
                rawX = 0f,
                rawY = 0f,
                adjustedX = 0f,
                adjustedY = 0f,
                directionSpaceX = 0f,
                directionSpaceY = 0f,
                magnitude = 0f,
                isActive = false,
                direction = Direction.NONE,
            ),
            deadZone = 0.25f,
            dialSectionMode = DialSectionMode.EIGHT_SECTION,
        )

        assertEquals(ControllerConfusionType.DEAD_ZONE_JITTER, sample.confusionType)
        assertEquals("25-30%", sample.deadZoneBand)
    }

    @Test
    fun detectsSnapBackReversalOnRelease() {
        val signal = ControllerConfusionAnalyzer.detectSnapBackReversal(
            previousDirection = Direction.N,
            lastDirectionBeforeRelease = Direction.NE,
            deadZone = 0.20f,
        )

        assertNotNull(signal)
        assertEquals(ControllerConfusionType.SNAP_BACK_REVERSAL, signal.confusionType)
        assertEquals("20-25%", signal.deadZoneBand)
    }

    @Test
    fun ignoresReleaseWhenDirectionDidNotChange() {
        val signal = ControllerConfusionAnalyzer.detectSnapBackReversal(
            previousDirection = Direction.SE,
            lastDirectionBeforeRelease = Direction.SE,
            deadZone = 0.20f,
        )

        assertNull(signal)
    }

    private fun activeSnapshot(direction: Direction) = ControllerStickSnapshot(
        rawX = 0.7f,
        rawY = 0.7f,
        adjustedX = 0.5f,
        adjustedY = 0.5f,
        directionSpaceX = 10f,
        directionSpaceY = 10f,
        magnitude = 0.8f,
        isActive = true,
        direction = direction,
    )
}