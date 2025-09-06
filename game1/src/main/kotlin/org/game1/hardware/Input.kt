package org.example.org.game1.hardware

enum class GamepadDevice {
    Gamepad1, Gamepad2, Gamepad3, Gamepad4
}

enum class Button {
    UP, DOWN, LEFT, RIGHT, A, B, X, Y, START, SELECT
}

interface Gamepad {
    operator fun contains(button: Button): Boolean
}