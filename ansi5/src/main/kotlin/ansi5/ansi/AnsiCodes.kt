package ansi5.ansi

const val ANSI_PREFIX = "\u001B"

private fun ansiM(code: String) = "$ANSI_PREFIX[${code}m"
private fun ansiJ(code: String) = "$ANSI_PREFIX[${code}J"
private fun ansiH(code: String) = "$ANSI_PREFIX[${code}H"

val ANSI_RESET = ansiM("0")

val ANSI_CLEAR = ansiJ("2") + ansiH("")
val ANSI_ZERO = "\u001b[H"

enum class AnsiText(code: Int) {
    BOLD(1),
    FAINT(2),
    ITALIC(3),
    UNDERLINE(4),
    SLOW_BLINK(5),
    RAPID_BLINK(6),
    INVERSE(7),
    CONCEAL(8),
    STRIKETHROUGH(9),
    DOUBLE_UNDERLINE(21),
    OVERLINE(53);

    val code: String = code.toString()
    val command = ansiM(this.code)
}

fun main2() {
    AnsiText.entries.forEach { attribute ->
        println("${attribute.code}${attribute.name}${ANSI_RESET}\n")
    }

    println("${AnsiText.BOLD.code}${AnsiText.UNDERLINE.code}${AnsiText.SLOW_BLINK.code}${AnsiText.ITALIC.code}${AnsiText.FAINT.code}\nHELLO${ANSI_RESET}\n")
}


interface AnsiColor {
    val fgCode: String
    val fgCommand get() = ansiM(fgCode)
    val bgCode: String
    val bgCommand get() = ansiM(bgCode)
}

data class Color16(
    private val red: Boolean,
    private val green: Boolean,
    private val blue: Boolean,
    val bright: Boolean
) : AnsiColor {
    companion object {
        val BLACK = Color16(false, false, false, false)
        val RED = Color16(true, false, false, false)
        val GREEN = Color16(false, true, false, false)
        val YELLOW = Color16(true, true, false, false)
        val BLUE = Color16(false, false, true, false)
        val MAGENTA = Color16(true, false, true, false)
        val CYAN = Color16(false, true, true, false)
        val WHITE = Color16(true, true, true, false)

        val DIM_COLORS = listOf(BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE)
        val BRIGHT_COLORS = DIM_COLORS.map { it.makeBrigh }
        val COLORS = DIM_COLORS + BRIGHT_COLORS
    }

    override val fgCode: String
        get() {
            val base = if (bright) 90 else 30
            val offset = (if (red) 1 else 0) + (if (green) 2 else 0) + (if (blue) 4 else 0)
            return (base + offset).toString()
        }


    override val bgCode: String
        get() {
            val base = if (bright) 100 else 40
            val offset = (if (red) 1 else 0) + (if (green) 2 else 0) + (if (blue) 4 else 0)
            return (base + offset).toString()
        }

    val makeBrigh: Color16 get() = this.copy(bright = true)
    val makeDim: Color16 get() = this.copy(bright = false)
}


data class ColorRgb6(
    val red: Int = 0,
    val green: Int = 0,
    val blue: Int = 0,
) : AnsiColor {
    init {
        require(red in 0..5) { "Red component must be in range 0..5" }
        require(green in 0..5) { "Green component must be in range 0..5" }
        require(blue in 0..5) { "Blue component must be in range 0..5" }
    }

    override val fgCode: String get() = "38;5;${16 + (36 * red) + (6 * green) + blue}"
    override val bgCode: String get() = "48;5;${16 + (36 * red) + (6 * green) + blue}"
}

data class ColorRgb256(
    val red: Int = 0,
    val green: Int = 0,
    val blue: Int = 0,
) : AnsiColor {
    companion object {
        val RANGE = 0..255
    }

    init {
        require(red in RANGE) { "Red component must be in range $RANGE" }
        require(green in RANGE) { "Green component must be in range $RANGE" }
        require(blue in RANGE) { "Blue component must be in range $RANGE" }
    }

    override val fgCode: String get() = "38;2;${red};${green};${blue}"
    override val bgCode: String get() = "48;2;${red};${green};${blue}"
}

data class ColorGray24(val code: Int) : AnsiColor {
    companion object {
        val RANGE = 0..23
        val COLORS = ColorGray24.RANGE.map { ColorGray24(it) }
    }

    init {
        require(code in RANGE) { "Gray code must be in range $RANGE" }
    }

    override val fgCode: String get() = "38;5;${232 + code}"
    override val bgCode: String get() = "48;5;${232 + code}"
}

