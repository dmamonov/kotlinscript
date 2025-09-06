package org.example.org.game1.hardware

import org.example.org.game1.algebra.*

interface Hardware {
    fun initDisplay(size: Size = size(256, 192)): Display
    fun createImage(pixels: Array<Array<ColorIndex>>): Image
    fun gamepad(device: GamepadDevice): Gamepad
}