package ansi.compose1

sealed interface Orientation
interface Row : Orientation
interface Col : Orientation

@JvmInline
value class Index<Orientation>(val value: Int) : Comparable<Index<Orientation>> {
    override fun compareTo(other: Index<Orientation>): Int {
        return this.value.compareTo(other.value)
    }

    operator fun minus(size: Size<Orientation>): Index<Orientation> {
        return Index(this.value - size.value)
    }
}

typealias RowIndex = Index<Row>
typealias ColIndex = Index<Col>

enum class Position {
    OUTSIDE,
    TOP,
    RIGHT,
    BOTTOM,
    LEFT,
    INSIDE,
}

data class Point(val row: RowIndex, val col: ColIndex)

data class Box(val width: Width, val height: Height) {
    fun position(point: Point): Position {
        return if (point.row in height && point.col in width) {
            val top = point.row.value == 0
            val bottom = point.row.value == width.value - 1
            val left = point.col.value == 0
            val right = point.col.value == height.value - 1


            Position.INSIDE
        } else Position.OUTSIDE
    }
}


@JvmInline
value class Size<Orientation>(val value: Int) : Comparable<Size<Orientation>> {
    val range: List<Index<Orientation>> get() = (0 until value).map { Index(it) }
    fun check(col: Index<Orientation>) = check(col.value in 0 until value)
    override fun compareTo(other: Size<Orientation>): Int {
        return this.value.compareTo(other.value)
    }

    operator fun plus(other: Size<Orientation>): Size<Orientation> {
        return Size(this.value + other.value)
    }

    operator fun contains(index: Index<Orientation>): Boolean {
        return index.value in 0 until this.value
    }
}

typealias Width = Size<Col>
typealias Height = Size<Row>

val width0 = Width(0)
val width1 = Width(1)

val height0 = Height(0)
val height1 = Height(1)

abstract class Block {
    abstract val width: Width
    abstract val height: Height
    abstract fun char(row: RowIndex, col: ColIndex): String

    fun render(): String {
        return height.range.joinToString(separator = "\n") { row ->
            width.range.joinToString(separator = "") { col -> char(row, col) }
        }
    }

    override fun toString(): String {
        return render()
    }
}

class Line(input: String) : Block() {
    private val symbols = input.toCharArray().map { it.toString() }
    override val width = Width(symbols.size)
    override val height = height1
    override fun char(row: RowIndex, col: ColIndex): String {
        width.check(col)
        height.check(row)
        return symbols[col.value]
    }
}

val nothing = " "

object EmptyBlock : Block() {
    override val width: Width = width0
    override val height: Height = height0
    override fun char(row: RowIndex, col: ColIndex): String = nothing

}

class VBox(private val top: Block, private val bottom: Block) : Block() {
    override val width: Width = maxOf(top.width, bottom.width)
    override val height: Height = top.height + bottom.height

    override fun char(row: RowIndex, col: ColIndex): String {
        val (blockRow, block) = if (row in top.height) {
            row to top
        } else {
            row - top.height to bottom
        }
        return if (col in block.width) {
            block.char(blockRow, col)
        } else {
            nothing
        }
    }
}


fun vbox(vararg blocks: Block): Block {
    return when (blocks.size) {
        0 -> EmptyBlock
        1 -> blocks[0]
        else -> VBox(blocks[0], vbox(*blocks.toList().subList(1, blocks.size).toTypedArray()))
    }
}

class HBox(private val left: Block, private val right: Block) : Block() {
    override val width: Width = left.width + right.width
    override val height: Height = maxOf(left.height, right.height)

    override fun char(row: RowIndex, col: ColIndex): String {
        val (blockCol, block) = if (col in left.width) {
            col to left
        } else {
            col - left.width to right
        }
        return if (row in block.height) {
            return block.char(row, blockCol)
        } else {
            nothing
        }
    }
}
fun hbox(vararg blocks: Block): Block {
    return when (blocks.size) {
        0 -> EmptyBlock
        1 -> blocks[0]
        else -> HBox(blocks[0], hbox(*blocks.toList().subList(1, blocks.size).toTypedArray()))
    }
}


fun main() {
    println(Line("Hello"))
    println()
    println(hbox(Line("Hello"), Line("World")))
    println()
    println(vbox(Line("Hello"), Line("World")))
    println()
    println(hbox(Line("Hello"), vbox(Line("Hello"), Line("World")), Line("World")))
}