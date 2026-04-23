package com.vatoo.erick.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ControllerInputProcessorTest {

    @Test
    fun deadZoneSuppressesSmallControllerInput() {
        val snapshot = ControllerInputProcessor.resolveStick(
            x = 0.1f,
            y = -0.1f,
            deadZone = 0.25f,
            invertY = false,
            dialSectionMode = DialSectionMode.EIGHT_SECTION
        )

        assertFalse(snapshot.isActive)
        assertEquals(Direction.NONE, snapshot.direction)
        assertEquals(0f, snapshot.adjustedX)
        assertEquals(0f, snapshot.adjustedY)
    }

    @Test
    fun inversionFlipsControllerYAxisDirection() {
        val normal = ControllerInputProcessor.resolveStick(
            x = 0f,
            y = -1f,
            deadZone = 0.25f,
            invertY = false,
            dialSectionMode = DialSectionMode.EIGHT_SECTION
        )
        val inverted = ControllerInputProcessor.resolveStick(
            x = 0f,
            y = -1f,
            deadZone = 0.25f,
            invertY = true,
            dialSectionMode = DialSectionMode.EIGHT_SECTION
        )

        assertTrue(normal.isActive)
        assertEquals(Direction.N, normal.direction)
        assertEquals(Direction.S, inverted.direction)
    }

    @Test
    fun sixSectionDirectionMatchesSharedGeometry() {
        val snapshot = ControllerInputProcessor.resolveStick(
            x = 1f,
            y = 0f,
            deadZone = 0.25f,
            invertY = false,
            dialSectionMode = DialSectionMode.SIX_SECTION
        )

        assertTrue(snapshot.isActive)
        assertEquals(Direction.SE, snapshot.direction)
    }
}