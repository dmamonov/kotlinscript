package ansi

import java.util.*

data class Line(val symbols: List<Symbol>) : Block, Spec {
    val length: Int = symbols.size

    operator fun plus(other: Line) = Line(this.symbols + other.symbols)


    override val size: Size = Size(Width(length), HEIGHT_1)
    override fun symbol(point: Point): Optional<Symbol> {
        return if (point in size) {
            Optional.of(symbols[point.col.value])
        } else Optional.empty()
    }

    override val denseSize: Size get() = size

    override fun fit(bounds: Optional<Size>): Block {
        val fitSize = bounds.orElse(denseSize)
        return CropBlock(this, fitSize)
    }

    override fun toString(): String = symbols.joinToString(separator = "")
}

fun String.line(): Line = Line(this.map { Symbol(it.toString()) })

fun main3() {
    println("Hello, World!".line().fg(RED).bg(YELLOW.light).bold.underline.italic)
}