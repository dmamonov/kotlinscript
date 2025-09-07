package org.example.org.game1.hardware

import org.example.org.game1.algebra.Box
import org.example.org.game1.algebra.Size
import org.example.org.game1.algebra.XY

@JvmInline
value class ColorIndex(val value: Byte) {
    companion object {
        val TRANSPARENT = ColorIndex(0.toByte())
    }

    val isTransparent: Boolean
        get() = this.value == TRANSPARENT.value

    val isColor: Boolean
        get() = this.value != TRANSPARENT.value
}


data class ColorRGB(val red: UByte, val green: UByte, val blue: UByte)

interface Palette {
    operator fun get(index: ColorIndex): ColorRGB
}

interface Image {
    val size: Size
    operator fun get(xy: XY): ColorIndex
}

interface Surface : Image {
    fun clear(color: ColorIndex = ColorIndex.TRANSPARENT)
    operator fun set(xy: XY, color: ColorIndex)
    operator fun set(xy: XY, image: Image)
    fun crop(box: Box): Surface
}


interface Display {
    val size: Size

    val palette: Palette

    fun render(renderer: (Surface) -> Unit)

    fun exit()
}
