package teya.ansi.compose2

abstract class Block  {
    abstract val box: Box
    abstract fun char(point: Point): String

    fun render(bounds: Box): String {
        return bounds.height.range.joinToString(separator = "\n") { row ->
            bounds.width.range.joinToString(separator = "") { col -> char(Point(row, col)) }
        }
    }

    override fun toString(): String {
        return render(box)
    }
}


class TextBlock(input: String) : Block() {
    private val symbols = input.toCharArray().map { it.toString() }
    override val box = Box(Width(symbols.size), HEIGHT1)
    override fun char(point: Point): String {
        return if (point in box) {
            symbols[point.col.value]
        } else nothing
    }
}

class RepeatBlock(override val box: Box, private val symbol: String) : Block() {
    override fun char(point: Point): String = symbol
}


val nothing = " "

data class EmptyBlock(override val box: Box = Box(WIDTH0, HEIGHT0)) : Block() {
    override fun char(point: Point): String = nothing
}

class StackBlock(private val top: Block, private val bottom: Block) : Block() {
    override val box = top.box.stack(bottom.box)

    override fun char(point: Point): String {
        val (blockRow, block) = if (point.row in top.box.height) {
            point.row to top
        } else {
            point.row - top.box.height to bottom
        }
        return if (point.col in block.box.width) {
            block.char(Point(blockRow, point.col))
        } else {
            nothing
        }
    }
}

class LineBlock(private val left: Block, private val right: Block) : Block() {
    override val box = left.box.line(right.box)

    override fun char(point: Point): String {
        val (blockCol, block) = if (point.col in left.box.width) {
            point.col to left
        } else {
            point.col - left.box.width to right
        }
        return if (point.row in block.box.height) {
            return block.char(Point(point.row, blockCol))
        } else {
            nothing
        }
    }
}
