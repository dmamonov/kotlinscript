package org.example.org.game1.algebra

@JvmInline
value class Coordinate<AXIS : Axis>(val value: Int) : Comparable<Coordinate<AXIS>> {
    operator fun plus(delta: Delta<AXIS>): Coordinate<AXIS> = Coordinate(this.value + delta.value)
    operator fun minus(delta: Delta<AXIS>): Coordinate<AXIS> = Coordinate(this.value - delta.value)
    override fun compareTo(other: Coordinate<AXIS>): Int = this.value.compareTo(other.value)

}

typealias X = Coordinate<XAxis>
typealias Y = Coordinate<YAxis>

val ZERO_X = X(0)
val ZERO_Y = Y(0)

data class XY(val x: X, val y: Y) {
    operator fun plus(delta: DXY): XY = XY(x + delta.dx, y + delta.dy)
    operator fun minus(delta: DXY): XY = XY(x - delta.dx, y - delta.dy)
    val toDelta: DXY
        get() = DXY(DX(x.value), DY(y.value))
}

fun xy(x: Int, y: Int): XY = XY(X(x), Y(y))

