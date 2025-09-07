package org.example.org.game1.hardware

import org.example.org.game1.algebra.Size
import org.example.org.game1.algebra.XY

@JvmInline
value class ColorIndex(val value: Byte) {

    companion object {
        val TRANSPARENT = ColorIndex(0.toByte())
    }

    val intValue: Int
        get() = if (value==0.toByte()) 0 else -1

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

interface Canvas {
    val size: Size
    fun clear(color: ColorIndex = ColorIndex.TRANSPARENT)
    operator fun set(xy: XY, color: ColorIndex)
    operator fun set(xy: XY, image: Image)
}

interface Surface : Image {
    fun render(renderer: (Canvas) -> Unit)
}


interface Display {
    val size: Size

    val palette: Palette

    fun render(renderer: (Canvas) -> Unit)

    fun exit()
}
