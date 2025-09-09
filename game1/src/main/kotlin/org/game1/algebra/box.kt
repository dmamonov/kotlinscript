package org.example.org.game1.algebra

import java.util.stream.Stream

data class Box(val sx: SX, val sy: SY) {
    val min: XY = XY(sx.min, sy.min)
    val max: XY = XY(sx.max, sy.max)

    operator fun plus(delta: DXY): Box = Box(
        sx = SX(sx.min + delta.dx, sx.max + delta.dx),
        sy = SY(sy.min + delta.dy, sy.max + delta.dy)
    )

    infix fun intersect(other: Box): Box? {
        return (this.sx intersect other.sx) nullBox (this.sy intersect other.sy)
    }

    infix fun union(other: Box): Box {
        return Box(
            sx = SX(
                minOf(sx.min, other.sx.min),
                maxOf(sx.max, other.sx.max)
            ),
            sy = SY(
                minOf(sy.min, other.sy.min),
                maxOf(sy.max, other.sy.max)
            )
        )
    }

    val points: Iterable<XY> = sx.coordinates.flatMap { x ->
        sy.coordinates.map { y ->
            XY(x, y)
        }
    }

    val size: Size
        get() = Size(
            Width(sx.max.value - sx.min.value),
            Height(sy.max.value - sy.min.value)
        )

    fun splitByCells(block: Size): Stream<Cell> {
        check(!isEmpty) { "Can't hash an empty box: $this" }

        val xSlices = this.sx.split(block.width).toList()
        val ySlices = this.sy.split(block.height).toList()

        var xIndex = 0
        var yIndex = 0

        fun hash(): Cell {
            val result = Cell(
                Row(ySlices[yIndex].value / block.height.value),
                Col(xSlices[xIndex].value / block.width.value),
            )
            if (++xIndex == xSlices.size) {
                xIndex = 0
                yIndex++
            }
            return result
        }

        return Stream.iterate(
            hash(),
            { yIndex < ySlices.size },
            { hash() }
        )
    }

    val isEmpty: Boolean get() = sx.isEmpty || sy.isEmpty
}

val ZERO_BOX = Box(ZERO_SX, ZERO_SY)