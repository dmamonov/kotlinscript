import ansi5.algebra.Frame
import ansi5.algebra.Point
import ansi5.algebra.Size
import ansi5.algebra.Row
import ansi5.algebra.Col
import ansi5.algebra.Duration
import ansi5.algebra.Width
import ansi5.algebra.Height
import ansi5.algebra.RowOffset
import ansi5.algebra.ColOffset
import ansi5.algebra.Delta
import ansi5.ansi.Color16
import ansi5.blocks.bg
import ansi5.blocks.line
import ansi5.blocks.render

fun setRawMode() {
    Runtime.getRuntime().exec(arrayOf("sh", "-c", "stty raw -echo < /dev/tty")).waitFor()
}

fun readAnsiInput(): String {
    val c = System.`in`.read()
    if (c == 27) { // ESC
        val buffer = StringBuilder()
        buffer.append('\u001B')
        var next = System.`in`.read()
        buffer.append(next.toChar())
        while (true) {
            next = System.`in`.read()
            buffer.append(next.toChar())
            if (next in 'A'.code..'Z'.code || next in 'a'.code..'z'.code) break
        }
        return buffer.toString()
    } else {
        return c.toChar().toString()
    }
}

object AnsiInput {
    const val UP = "\u001B[A"
    const val DOWN = "\u001B[B"
    const val RIGHT = "\u001B[C"
    const val LEFT = "\u001B[D"

    const val SHIFT_UP = "\u001B[1;2A"
    const val SHIFT_DOWN = "\u001B[1;2B"
    const val SHIFT_RIGHT = "\u001B[1;2C"
    const val SHIFT_LEFT = "\u001B[1;2D"

    const val HOME = "\u001B[H"
    const val END = "\u001B[F"
    const val DELETE = "\u001B[3~"
    const val INSERT = "\u001B[2~"
    const val PAGE_UP = "\u001B[5~"
    const val PAGE_DOWN = "\u001B[6~"
}


data class Brush(val color: Color16)

data class Pixel(val brush: Brush?)

data class Location(val point: Point, val frame: Frame)

data class State(
    val brush: Color16 = Color16.WHITE,
    val size: Size = Size(Width(8), Height(8)),
    val duration: Duration = Duration(1),
    val cursor: Location = Location(
        Point(
            Row(1),
            Col(1),
        ),
        Frame(1)
    ),
    val matrix: Map<Location, Color16> = mapOf(),
) {
    fun render() {
        print("\u001B[2J\u001B[1;1H") //clear screen and goto 1,1
        size.rows1.forEach { row ->
            size.cols1.map { col ->
                val location = Location(Point(row, col), cursor.frame)
                val pixel = matrix.get(location)
                val symbol = if (location == cursor) "[]" else if (pixel != null) "  " else ".."
                val cell = if (pixel != null) symbol.line.bg(pixel) else symbol.line
                print(cell.render())
            }
            print("\r\n")
        }
        println((if (brush.bright) "Bright " else "Dark ").line.bg(brush).render())
        print("\u001B[4 q") //no blink cursor
    }
}

class Hodler(private var state:State = State(), private var undo:State? = null) {
    fun get(): State = state
    fun set(newState: State) {
        this.undo = this.state
        this.state = newState
    }
    fun undo() {
        val undo = this.undo
        if (undo!=null) {
            this.state=undo
        }
    }
}

fun main() {
    if (true){
        ansi5.ansi.main()
        return





    }

    setRawMode()


    var state = State()

    fun switchPixel(reset: Boolean = false) {
        state = state.copy(
            matrix = state.matrix.toMutableMap().apply {
                if (this[state.cursor] == state.brush || reset) {
                    this.remove(state.cursor)
                } else {
                    this[state.cursor] = state.brush
                }
            }
        )
    }

    while (true) {
        state.render()
        val input = readAnsiInput()
        val ROW_PLUS = RowOffset(1)
        val ROW_MINUS = RowOffset(-1)
        val COL_MINUS = ColOffset(-1)

        val COL_PLUS = ColOffset(1)
        when (input) {
            "q", "Q" -> break
            AnsiInput.UP, AnsiInput.SHIFT_UP -> { //"UP"
                state = state.copy(
                    cursor = state.cursor.copy(
                        point = state.cursor.point.copy(
                            row = maxOf(Row(1), state.cursor.point.row + ROW_MINUS),
                        )
                    )
                )
                if (input == AnsiInput.SHIFT_UP) {
                    switchPixel()
                }
            }

            AnsiInput.DOWN, AnsiInput.SHIFT_DOWN -> {//"DOWN"
                state = state.copy(
                    cursor = state.cursor.copy(
                        point = state.cursor.point.copy(
                            row = minOf(state.size.height.asCoordinate, state.cursor.point.row + ROW_PLUS)
                        ),
                    )
                )
                if (input == AnsiInput.SHIFT_DOWN) {
                    switchPixel()
                }
            }

            AnsiInput.LEFT, AnsiInput.SHIFT_LEFT -> {//"RIGHT"
                state = state.copy(
                    cursor = state.cursor.copy(
                        point = state.cursor.point.copy(
                            col = maxOf(Col(1), state.cursor.point.col + COL_MINUS)
                        )
                    )
                )
                if (input == AnsiInput.SHIFT_LEFT) {
                    switchPixel()
                }
            }

            AnsiInput.RIGHT, AnsiInput.SHIFT_RIGHT -> {//"LEFT"
                state = state.copy(
                    cursor = state.cursor.copy(
                        point = state.cursor.point.copy(
                            col = minOf(state.size.width.asCoordinate, state.cursor.point.col + COL_PLUS),
                        )
                    )
                )
                if (input == AnsiInput.SHIFT_RIGHT) {
                    switchPixel()
                }
            }

            "1", "2", "3", "4", "5", "6", "7", "8" -> {
                val index = input[0].code - '1'.code
                val palette = if (state.brush.bright) Color16.BRIGHT_COLORS else Color16.DIM_COLORS
                state = state.copy(brush = palette[index])
            }

            "0" -> {
                state = state.copy(brush = if (state.brush.bright) state.brush.makeDim else state.brush.makeBrigh)
            }

            "]" -> {
                state = state.copy(size = state.size + Delta(ROW_PLUS, COL_PLUS))
            }

            "[" -> {
                state = state.copy(size = state.size + Delta(ROW_MINUS, COL_MINUS))
            }

            " " -> {
                switchPixel()
            }

            "x" -> {
                switchPixel(reset = true)
            }

            else -> {
                //ignore
            }
        }

    }
}
