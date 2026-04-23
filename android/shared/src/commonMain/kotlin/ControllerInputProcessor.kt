package com.vatoo.erick.shared

import kotlin.math.hypot

data class ControllerStickSnapshot(
    val rawX: Float,
    val rawY: Float,
    val adjustedX: Float,
    val adjustedY: Float,
    val directionSpaceX: Float,
    val directionSpaceY: Float,
    val magnitude: Float,
    val isActive: Boolean,
    val direction: Direction
)

object ControllerInputProcessor {
    private const val DEADZONE_RADIUS = 40f

    fun resolveStick(
        x: Float,
        y: Float,
        deadZone: Float,
        invertY: Boolean,
        dialSectionMode: DialSectionMode
    ): ControllerStickSnapshot {
        val safeDeadZone = deadZone.coerceIn(0f, 1f)
        val clampedX = x.coerceIn(-1f, 1f)
        val clampedY = (if (invertY) -y else y).coerceIn(-1f, 1f)
        val magnitude = hypot(clampedX.toDouble(), clampedY.toDouble()).toFloat()

        if (magnitude <= safeDeadZone) {
            return ControllerStickSnapshot(
                rawX = x,
                rawY = y,
                adjustedX = 0f,
                adjustedY = 0f,
                directionSpaceX = 0f,
                directionSpaceY = 0f,
                magnitude = magnitude,
                isActive = false,
                direction = Direction.NONE
            )
        }

        val adjustedScale = if (safeDeadZone < 1f) {
            ((magnitude - safeDeadZone) / (1f - safeDeadZone)).coerceIn(0f, 1f)
        } else {
            1f
        }

        val adjustedX = if (magnitude > 0f) (clampedX / magnitude) * adjustedScale else 0f
        val adjustedY = if (magnitude > 0f) (clampedY / magnitude) * adjustedScale else 0f
        val directionScale = if (safeDeadZone > 0f) DEADZONE_RADIUS / safeDeadZone else DEADZONE_RADIUS
        val directionSpaceX = clampedX * directionScale
        val directionSpaceY = clampedY * directionScale
        val direction = KeyboardLogic().run {
            this.dialSectionMode = dialSectionMode
            getDirectionFromXY(directionSpaceX, directionSpaceY)
        }

        return ControllerStickSnapshot(
            rawX = x,
            rawY = y,
            adjustedX = adjustedX,
            adjustedY = adjustedY,
            directionSpaceX = directionSpaceX,
            directionSpaceY = directionSpaceY,
            magnitude = magnitude,
            isActive = true,
            direction = direction
        )
    }
}