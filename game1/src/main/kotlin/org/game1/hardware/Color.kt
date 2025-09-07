package org.example.org.game1.hardware

import java.awt.Color

@JvmInline
value class ColorIndex(val value: Int) {

    companion object {
        val TRANSPARENT = ColorIndex(0)
        val colors = listOf(
            Color(0, 0, 0, 255),
            Color.RED,
            Color.RED.darker(),
            Color.PINK,
            Color.PINK.darker(),
            Color.ORANGE,
            Color.ORANGE.darker(),
            Color.YELLOW,
            Color.YELLOW.darker(),
            Color.GREEN,
            Color.GREEN.darker(),
            Color.MAGENTA,
            Color.MAGENTA.darker(),
            Color.CYAN,
            Color.CYAN.darker(),
            Color.BLUE,
            Color.BLUE.darker(),
            Color.WHITE.darker(),
            Color.LIGHT_GRAY,
            Color.GRAY,
            Color.DARK_GRAY,
            Color.BLACK,
        )
    }
    val javaColor: Color get() = colors[this.value]
}


data class ColorRGB(val red: UByte, val green: UByte, val blue: UByte)

interface Palette {
    operator fun get(index: ColorIndex): ColorRGB
}
