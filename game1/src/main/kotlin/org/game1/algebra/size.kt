package org.example.org.game1.algebra

@JvmInline
value class Length<AXIS : Axis>(val value: Int) : Comparable<Length<AXIS>> {

    override fun compareTo(other: Length<AXIS>): Int = this.value.compareTo(other.value)

    operator fun contains(coordinate: Coordinate<AXIS>): Boolean = coordinate.value in 0 until value

    override fun toString(): String = "$value"
}

typealias Width = Length<XAxis>
typealias Height = Length<YAxis>

data class Size(val width: Width, val height: Height) {
    override fun toString(): String {
        return "W${width}xH${height}"
    }

    val box: Box
        get() = Box(
            sx = SX(min = ZERO_X, max = X(width.value)),
            sy = SY(min = ZERO_Y, max = Y(height.value))
        )

    operator fun contains(xy: XY): Boolean {
        return xy.x in width && xy.y in height
    }

    infix fun min(other: Size): Size {
        return Size(
            width = minOf(this.width, other.width),
            height = minOf(this.height, other.height)
        )
    }
}

fun size(width: Int, height: Int): Size = Size(Width(width), Height(height))