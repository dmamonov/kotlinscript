package ansi

import java.util.Optional

fun ansi(code: String) = "\u001B[${code}m"

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

    val code: String = ansi(code.toString())
}

fun main2() {
    AnsiText.entries.forEach { attribute ->
        println("${attribute.code}${attribute.name}${ansiReset}\n")
    }

    println("${AnsiText.BOLD.code}${AnsiText.UNDERLINE.code}${AnsiText.SLOW_BLINK.code}${AnsiText.ITALIC.code}${AnsiText.FAINT.code}\nHELLO${ansiReset}\n")
}


interface AnsiColor {
    val fg: String
    val bg: String
}

data class Color16(
    private val red: Boolean,
    private val green: Boolean,
    private val blue: Boolean,
    private val bright: Boolean
) : AnsiColor {
    override val fg: String
        get() {
            val base = if (bright) 90 else 30
            val offset = (if (red) 1 else 0) + (if (green) 2 else 0) + (if (blue) 4 else 0)
            return ansi((base + offset).toString())
        }

    override val bg: String
        get() {
            val base = if (bright) 100 else 40
            val offset = (if (red) 1 else 0) + (if (green) 2 else 0) + (if (blue) 4 else 0)
            return ansi((base + offset).toString())
        }

    val light: Color16 get() = this.copy(bright = true)
    val dim: Color16 get() = this.copy(bright = false)
}

val BLACK = Color16(false, false, false, false)
val RED = Color16(true, false, false, false)
val GREEN = Color16(false, true, false, false)
val YELLOW = Color16(true, true, false, false)
val BLUE = Color16(false, false, true, false)
val MAGENTA = Color16(true, false, true, false)
val CYAN = Color16(false, true, true, false)
val WHITE = Color16(true, true, true, false)

val dimColors16 = listOf(BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE)
val lightColors16 = dimColors16.map { it.light }
val colors16 = dimColors16 + lightColors16

data class ColorRgb6(
    val red: Int,
    val green: Int,
    val blue: Int
) : AnsiColor {
    init {
        require(red in 0..5) { "Red component must be in range 0..5" }
        require(green in 0..5) { "Green component must be in range 0..5" }
        require(blue in 0..5) { "Blue component must be in range 0..5" }
    }

    override val fg: String get() = ansi("38;5;${16 + (36 * red) + (6 * green) + blue}")
    override val bg: String get() = ansi("48;5;${16 + (36 * red) + (6 * green) + blue}")
}

data class ColorGray24(val code: Int) : AnsiColor {
    init {
        require(code in 0..23) { "Gray code must be in range 0..23" }
    }

    override val fg: String get() = ansi("38;5;${232 + code}")
    override val bg: String get() = ansi("48;5;${232 + code}")
}


val ansiReset = ansi("0")

fun main1() {
    for (color in colors16) {
        val label = color.fg.replace(Regex("[^0-9]"), "").padStart(3, ' ')
        print("${color.bg}${color.fg}$label$ansiReset")
    }
}

data class Attributes(
    val fg: AnsiColor,
    val bg: AnsiColor,
    val attrs: Set<AnsiText> = emptySet()
) {
    val ansi: String
        get() {
            val attrCodes = attrs.joinToString(separator = "") { it.code }
            return "$attrCodes${fg.fg}${bg.bg}"
        }
}

data class Symbol(
    val char: String,
    val attrs: Attributes = Attributes(WHITE, BLACK)
) {
    val length: Int get() = 1

    override fun toString(): String = "${attrs.ansi}$char$ansiReset"
}

val BLANK_SYMBOL = Symbol(" ")


interface Spec {
    val denseSize: Size

    fun fit(bounds: Optional<Size> = Optional.empty()): Block

    fun right(): Spec = AlignRight(this)
}

class Stack(vararg val specs: Spec) : Spec {
    val minWidth = specs.maxOfOrNull { it.denseSize.width } ?: WIDTH_0
    val minHeight = Height(specs.sumOf { it.denseSize.height.value })
    override val denseSize = Size(minWidth, minHeight)

    override fun fit(bounds: Optional<Size>): Block {
        val fitBounds = bounds.orElse(denseSize)
        check(denseSize in fitBounds) { "Cannot fit $fitBounds into $bounds" }
        return LinesBlock(
            specs
                .map { it.fit(Optional.of(Size(fitBounds.width, it.denseSize.height))) }
                .flatMap { it.toLines() }
        )
    }
}

class AlignRight(val spec: Spec) : Spec {
    override val denseSize: Size = spec.denseSize

    override fun fit(bounds: Optional<Size>): Block {
        val fitBounds = bounds.orElse(denseSize)
        check(denseSize in fitBounds) { "Cannot fit $fitBounds into $bounds" }
        return LinesBlock(
            spec.fit(Optional.of(fitBounds)).toLines().map { line ->
                val padding = fitBounds.width.value - line.length
                check(padding >= 0)
                if (padding > 0) {
                    Line(List(padding) { BLANK_SYMBOL } + line.symbols)
                } else line
            }
        )
    }
}

fun main() {
    println(
        Stack("Hello".line(), "!".line().right(), "World".line()).fit().toLines()
            .joinToString(separator = "\n") { it.toString() })
}
