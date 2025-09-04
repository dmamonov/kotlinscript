package ansi

import java.util.Optional
import kotlin.collections.plus


interface Block {
    val size: Size
    fun symbol(point: Point): Optional<Symbol>
    fun toLines(): List<Line> {
        return size.height.steps.map { row ->
            Line(size.width.steps.map { col ->
                symbol(Point(row, col)).orElse(Symbol(" "))
            })
        }
    }

    fun fg(color: AnsiColor) = StyleBlock(this, fg = color)
    fun bg(color: AnsiColor) = StyleBlock(this, bg = color)

    val bold get() = StyleBlock(this, attrs = setOf(AnsiText.BOLD))
    val faint get() = StyleBlock(this, attrs = setOf(AnsiText.FAINT))
    val italic get() = StyleBlock(this, attrs = setOf(AnsiText.ITALIC))
    val underline get() = StyleBlock(this, attrs = setOf(AnsiText.UNDERLINE))
    val strikethrough get() = StyleBlock(this, attrs = setOf(AnsiText.STRIKETHROUGH))
    val blink get() = StyleBlock(this, attrs = setOf(AnsiText.SLOW_BLINK))
    val inverse get() = StyleBlock(this, attrs = setOf(AnsiText.INVERSE))

    infix fun sequence(right: Block) = SequenceBlocks(this, right)
    infix fun stack(bottom: Block) = StackBlocks(this, bottom)
    infix fun over(back: Block) = OverlayBlocks(this, back)
    infix fun crop(size: Size) = CropBlock(this, size)
    infix fun offset(offset: Point) = OffsetBlock(offset, this)
    fun frame(symbols: FrameSymbols = SINGE_ROUND_FRAME) = FrameBlock(
        Size(size.width + Width(2), size.height + Height(2)),
        symbols
    ) over (this offset Point(ROW_1, COL_1))

    val transpose: Block get() = TransposeBlock(this)

    fun sprite(transparent: String = BLANK_SYMBOL.char) = SpriteBlock(this, transparent)

    infix fun transparentBackground(back: Block) = TransparentBackground(this, back)

    fun padding(
        left: Col = COL_0,
        right: Col = COL_0,
        top: Row = ROW_0,
        bottom: Row = ROW_0
    ) = (this offset Point(top, left)) crop (
            this.size + Point(top + bottom, left + right))


    fun print(): Block {
        println(this.toLines().joinToString("\n"))
        println()
        return this
    }
}

class StackBlocks(val top: Block, val bottom: Block) : Block {
    override val size: Size = Size(
        maxOf(top.size.width, bottom.size.width), top.size.height + bottom.size.height
    )

    override fun symbol(point: Point): Optional<Symbol> {
        return if (point in top.size) {
            top.symbol(point)
        } else {
            bottom.symbol(point.minusHeight(top.size.height))
        }
    }
}

class SequenceBlocks(val left: Block, val right: Block) : Block {
    override val size: Size = Size(
        left.size.width + right.size.width, maxOf(left.size.height, right.size.height)
    )

    override fun symbol(point: Point): Optional<Symbol> {
        return if (point in left.size) {
            left.symbol(point)
        } else {
            right.symbol(point.minusWidth(left.size.width))
        }
    }
}

class OverlayBlocks(val front: Block, val back: Block) : Block {
    override val size: Size = Size(
        maxOf(front.size.width, back.size.width), maxOf(front.size.height, back.size.height)
    )

    override fun symbol(point: Point): Optional<Symbol> {
        return front.symbol(point).or { back.symbol(point) }
    }
}

class StyleBlock(
    val block: Block, val fg: AnsiColor? = null, val bg: AnsiColor? = null, val attrs: Set<AnsiText>? = null
) : Block {
    override val size: Size = block.size

    override fun symbol(point: Point): Optional<Symbol> {
        return block.symbol(point).map {
            it.copy(
                attrs = Attributes(
                    fg ?: it.attrs.fg,
                    bg ?: it.attrs.bg,
                    if (attrs != null) attrs + it.attrs.attrs else it.attrs.attrs
                )
            )
        }
    }
}

class RepeatBlock(val symbol: Symbol) : Block {
    override val size: Size = SIZE_0

    override fun symbol(point: Point): Optional<Symbol> {
        return Optional.of(symbol)
    }
}

val EMPTY_SPACE = RepeatBlock(Symbol(" "))


fun main() {
    "Hello".line().print()
    println()

    val block1 = ("Top".line().fg(RED.light)
            stack
            ("Bottom".line().fg(YELLOW.light).bg(WHITE) sequence "!!!".line().fg(WHITE.light))
            stack
            "Under".line().fg(WHITE.light)).print()


    val block2 = OffsetBlock(Point(col = Col(10)), block1.bg(GREEN) stack block1.bg(YELLOW)).print()

    val block3 = block2 over RepeatBlock(Symbol("X")).underline.print()

    val block4 = (block3 crop Size(Width(17), Height(5))).print()


    block4.frame().print()
        .frame(SINGLE_SQUARE_FRAME).print()
        .frame(DOUBLE_SQUARE_FRAME).print()

    val field = "   ".line()
    val black = field.bg(BLACK)
    val white = field.bg(WHITE)
    val blackWhite2 = black sequence white
    val blackWhite4 = blackWhite2 sequence blackWhite2
    val blackWhite8 = blackWhite4 sequence blackWhite4
    val whiteBlack2 = white sequence black
    val whiteBlack4 = whiteBlack2 sequence whiteBlack2
    val whiteBlack8 = whiteBlack4 sequence whiteBlack4
    val rank2 = blackWhite8 stack whiteBlack8
    val rank4 = rank2 stack rank2
    val rank8 = rank4 stack rank4

    val rankIndex = "12345678".reversed().line().transpose offset Point(row = ROW_1)

    @Suppress("SpellCheckingInspection")
    val fileIndex = "abcdefgh".map { " $it " }.joinToString(separator = "").line()
    val fileIndexOffset = fileIndex offset Point(col = Col(2), row = Row(rank8.size.height.value + 1))

    val legendColor = ColorGray24(18)
    val board = (fileIndexOffset.sprite().bold.fg(legendColor) over
            (rankIndex.fg(legendColor) over rank8.padding(left = COL_1, right = COL_1)
                .frame())).print()

    fun figure(symbol: String): Block {
        return symbol.line().fg(WHITE.light) offset Point(col = COL_1) offset
                Point(row = ROW_1, col = Col(2)) offset Point(row = Row(7))
    }

    val whitePawn = figure(ChessFigure.PAWN.white)


    fun put(figure: Block, place: String): Block {
        check(place.matches(Regex("[a-h][1-8]")))
        val col = Col((place[0] - 'a') * 3)
        val row = Row(-(place[1] - '1'))
        return figure offset Point(row, col)
    }

    val whitePawns = put(whitePawn, "a2") over
            put(whitePawn, "b2") over
            put(whitePawn, "c2") over
            put(whitePawn, "d2") over
            put(whitePawn, "e2") over
            put(whitePawn, "f2") over
            put(whitePawn, "g2") over
            put(whitePawn, "h2")

    (whitePawns transparentBackground board).print()
}

data class SpriteBlock(val block: Block, val transparent: String = BLANK_SYMBOL.char) : Block {
    override val size: Size = block.size

    override fun symbol(point: Point): Optional<Symbol> {
        return block.symbol(point).filter { it.char != transparent }
    }
}

data class TransposeBlock(val block: Block) : Block {
    override val size: Size = Size(
        Width(block.size.height.value),
        Height(block.size.width.value)
    )

    override fun symbol(point: Point): Optional<Symbol> {
        return if (point in size) {
            block.symbol(point.transpose)
        } else Optional.empty()
    }
}

data class TransparentBackground(val front: Block, val back: Block) : Block {
    override val size: Size = Size(
        maxOf(front.size.width, back.size.width),
        maxOf(front.size.height, back.size.height)
    )

    override fun symbol(point: Point): Optional<Symbol> {
        val backSymbol = back.symbol(point)
        return front.symbol(point).map { frontValue ->
            backSymbol.map { backValue ->
                frontValue.copy(attrs = frontValue.attrs.copy(bg = backValue.attrs.bg))
            }.orElse(frontValue)
        }.or { backSymbol }
    }
}

data class CropBlock(val block: Block, override val size: Size) : Block {
    override fun symbol(point: Point): Optional<Symbol> {
        return if (point in size) {
            block.symbol(point)
        } else Optional.empty()
    }
}

class OffsetBlock(val offset: Point, val block: Block) : Block {
    override val size: Size = block.size + offset

    override fun symbol(point: Point): Optional<Symbol> {
        return if (point.row >= offset.row && point.col >= offset.col) {
            block.symbol(point - offset)
        } else Optional.empty()
    }
}

data class FrameSymbols(
    val topLeft: Symbol,
    val topRight: Symbol,
    val bottomLeft: Symbol,
    val bottomRight: Symbol,
    val horizontal: Symbol,
    val vertical: Symbol,
)

val SINGLE_SQUARE_FRAME = FrameSymbols(
    Symbol("┌"),
    Symbol("┐"),
    Symbol("└"),
    Symbol("┘"),
    Symbol("─"),
    Symbol("│")
)

val SINGE_ROUND_FRAME = FrameSymbols(
    Symbol("╭"),
    Symbol("╮"),
    Symbol("╰"),
    Symbol("╯"),
    Symbol("─"),
    Symbol("│")
)

val DOUBLE_SQUARE_FRAME = FrameSymbols(
    Symbol("╔"),
    Symbol("╗"),
    Symbol("╚"),
    Symbol("╝"),
    Symbol("═"),
    Symbol("║")
)

val BLANK_FRAME = FrameSymbols(
    BLANK_SYMBOL,
    BLANK_SYMBOL,
    BLANK_SYMBOL,
    BLANK_SYMBOL,
    BLANK_SYMBOL,
    BLANK_SYMBOL,
)


class FrameBlock(override val size: Size, val symbols: FrameSymbols = SINGE_ROUND_FRAME) : Block {
    override fun symbol(point: Point): Optional<Symbol> {
        val isFirstRow = point.row.isFirst(size.height)
        val isLastRow = point.row.isLast(size.height)
        val isFirstCol = point.col.isFirst(size.width)
        val isLastCol = point.col.isLast(size.width)
        return Optional.ofNullable(
            if (isFirstRow) {
                if (isFirstCol) {
                    symbols.topLeft
                } else if (isLastCol) {
                    symbols.topRight
                } else {
                    symbols.horizontal
                }
            } else if (isLastRow) {
                if (isFirstCol) {
                    symbols.bottomLeft
                } else if (isLastCol) {
                    symbols.bottomRight
                } else {
                    symbols.horizontal
                }
            } else if (isFirstCol || isLastCol) {
                symbols.vertical
            } else {
                null
            }
        )
    }
}


@Deprecated("Use other primitives instead")
data class LinesBlock(val lines: List<Line>) : Block {
    override val size: Size = if (lines.isEmpty()) {
        Size(WIDTH_0, HEIGHT_0)
    } else {
        Size(Width(lines.maxOf { it.length }), Height(lines.size))
    }

    override fun symbol(point: Point): Optional<Symbol> {
        return if (point in size) {
            lines[point.row.value].symbol(Point(ROW_0, point.col))
        } else Optional.empty()
    }
}
