package org.example.org.game1.hardware

import org.example.org.game1.algebra.Size
import org.example.org.game1.algebra.XY


interface Image {
    val size: Size
}

interface Canvas {
    val size: Size
    fun clear(color: ColorIndex = ColorIndex.TRANSPARENT)
    operator fun set(xy: XY, image: Image)
    operator fun set(
        xy: XY,
        color: ColorIndex
    )
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
