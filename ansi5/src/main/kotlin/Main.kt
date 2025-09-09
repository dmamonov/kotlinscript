fun setRawMode() {
    Runtime.getRuntime().exec(arrayOf("sh", "-c", "stty raw -echo < /dev/tty")).waitFor()
}

fun readAnsiInput(): String {
    val c = System.`in`.read()
    if (c == 27) { // ESC
        val sb = StringBuilder()
        sb.append('\u001B')
        var next = System.`in`.read()
        sb.append(next.toChar())
        while (true) {
            next = System.`in`.read()
            sb.append(next.toChar())
            if (next in 'A'.code..'Z'.code || next in 'a'.code..'z'.code) break
        }
        return sb.toString()
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


fun main() {
    setRawMode()
    var color = ""
    var colorIndex = 0
    var brigth = true

    fun updateColor() {
        color = "\u001B[${(if (brigth) 100 else 40) + colorIndex}m"
    }

    var cursorRow = 0
    var cursorCol = 0
    var width = 8
    var height = 8
    var matrix = Array(height) { Array(width) { "." } }

    fun render() {
        updateColor()
        print("\u001B[2J\u001B[1;1H") //clear screen and goto 1,1
        matrix.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                val isCurrent = rowIndex == cursorRow && colIndex == cursorCol

                val outCell = if (isCurrent) {
                    cell.replace(".", "+").replace(" ", "+")
                } else {
                    cell
                }
                print(outCell)
                print(outCell)
            }
            print("\r\n")
        }
        print("\r\n")
        print(color)
        print(if (brigth) "Bright " else "Dark ")
        print("\u001B[0m")

        print("\u001B[4 q")
//        print("\u001B[${cursorRow + 1};${cursorCol + 1}H")
    }

    fun switchPixel() {
        val pixel = "${color} \u001B[0m"
        matrix[cursorRow][cursorCol] = when (matrix[cursorRow][cursorCol]) {
            pixel -> "."
            else -> pixel
        }
    }

    while (true) {
        render()

        val input = readAnsiInput()
        when (input) {
            "q", "Q" -> break
            AnsiInput.UP, AnsiInput.SHIFT_UP -> { //"UP"
                cursorRow = maxOf(0, cursorRow - 1)
                if (input == AnsiInput.SHIFT_UP) {
                    switchPixel()
                }
            }

            AnsiInput.DOWN, AnsiInput.SHIFT_DOWN -> {//"DOWN"
                cursorRow = minOf(height - 1, cursorRow + 1)
                if (input == AnsiInput.SHIFT_DOWN) {
                    switchPixel()
                }
            }

            AnsiInput.LEFT, AnsiInput.SHIFT_LEFT -> {//"RIGHT"
                cursorCol = maxOf(0, cursorCol - 1)
                if (input == AnsiInput.SHIFT_LEFT) {
                    switchPixel()
                }
            }

            AnsiInput.RIGHT, AnsiInput.SHIFT_RIGHT -> {//"LEFT"
                cursorCol = minOf(width - 1, cursorCol + 1)

                if (input == AnsiInput.SHIFT_RIGHT) {
                    switchPixel()
                }
            }

            "1", "2", "3", "4", "5", "6", "7", "8" -> {
                colorIndex = input.toInt() - 1
            }

            "0" -> {
                brigth = !brigth
            }

            "r" -> {
                colorIndex = colorIndex xor 0x01
            }

            "g" -> {
                colorIndex = colorIndex xor 0x02
            }

            "b" -> {
                colorIndex = colorIndex xor 0x04
            }

            " " -> {

                switchPixel()

            }

            "x" -> {
                matrix[cursorRow][cursorCol] == "."
            }

            else -> {
                //ignore
            }
        }

    }
}
