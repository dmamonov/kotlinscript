package org.example.org.game1.algebra

data class AxisLine<FIRST : Axis, SECOND : Axis>(val coordinate: Coordinate<FIRST>, val segment: Segment<SECOND>)

typealias HorizontalLine = AxisLine<YAxis, XAxis>
typealias VerticalLine = AxisLine<YAxis, YAxis>


data class Line(val tail: XY, val head: XY) {
    init {
        check(head != tail)
    }

    val toDelta: DXY get() = DXY(head.x - tail.x, head.y - tail.y)

    val length: Double get() = toDelta.length

    infix fun closest(point: XY): XY {
        val lineVector = toDelta
        val lengthSquared = lineVector.lengthSquared
        if (lengthSquared == 0) {
            return tail
        } else {
            val pointVector = (point - tail)
            val lineParam = (pointVector scalarProduct lineVector).toDouble() / lengthSquared
            val lineClamped = lineParam.coerceIn(0.0, 1.0)
            val closestXY = tail + (lineVector scale lineClamped)
            return closestXY
        }
    }


    infix fun intersects(other: Line): XY? {
        val thisVector = this.head - this.tail
        val otherVector = other.head - other.tail

        val crossProduct = thisVector vectorProduct otherVector
        if (crossProduct == 0) {
            return null // Segments are parallel or collinear
        } else {
            val tailDifference = other.tail - this.tail

            val thisNumerator = tailDifference vectorProduct otherVector
            val otherNumerator = tailDifference vectorProduct thisVector

            val thisParam = thisNumerator.toDouble() / crossProduct
            val otherParam = otherNumerator.toDouble() / crossProduct

            if (thisParam in 0.0..1.0 && otherParam in 0.0..1.0) {
                val intersectionXY = this.tail + (thisVector scale thisParam)
                return intersectionXY
            } else {
                return null
            }
        }
    }
}