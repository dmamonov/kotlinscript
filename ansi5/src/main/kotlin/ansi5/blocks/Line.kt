package ansi5.blocks

import ansi5.algebra.HEIGHT_ONE
import ansi5.algebra.Matrix
import ansi5.algebra.Point
import ansi5.algebra.Size
import ansi5.algebra.Width
import ansi5.ansi.ANSI_CLEAR
import ansi5.ansi.ANSI_ZERO
import ansi5.ansi.AnsiColor
import ansi5.ansi.Color16

data class Line(val symbols: List<Symbol>) : Matrix<Symbol> {
    override val size: Size = Size(Width(symbols.size), HEIGHT_ONE)

    override fun get(point: Point): Symbol? = if (point in size) {
        symbols[point.col.value]
    } else null
}

fun Matrix<Symbol>.fg(color: AnsiColor): Matrix<Symbol> = this.process { symbol ->
    symbol.copy(fg = color)
}

fun Matrix<Symbol>.bg(color: AnsiColor): Matrix<Symbol> = this.process { symbol ->
    symbol.copy(bg = color)
}

val Matrix<Symbol>.bold: Matrix<Symbol>
    get() = this.process { symbol ->
        symbol.copy(bold = true)
    }
val Matrix<Symbol>.underline: Matrix<Symbol>
    get() = this.process { symbol ->
        symbol.copy(underline = true)
    }

fun Matrix<Symbol>.show(blank: Symbol = Symbol.BLANK): Matrix<Symbol> {
    val render = this.size.rows.joinToString("\n") { row ->
        this.size.cols.joinToString(separator = "") { col ->
            val point = Point(row, col)
            (this[point] ?: blank).toString()
        }
    }
    println(render)
    System.out.flush()

    return this
}

val String.line get() = Line(this.symbols())

fun main() {
    (0..1000).forEach {
        println(ANSI_ZERO)
        val hello = "Hello World! ".repeat(10).line.fg(Color16.RED)
        val nothing = ("And nothing ".line + ("les".line.bold) + "s".line.underline).fg(Color16.GREEN)
        (hello / nothing / "------$it------".line)
            .show()
        Thread.sleep(50)
    }

}