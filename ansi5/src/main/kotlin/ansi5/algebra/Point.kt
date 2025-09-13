package ansi5.algebra

@JvmInline
value class Coordinate<AXIS : Axis>(val value: Int) : Comparable<Coordinate<AXIS>> {
    operator fun minus(other: Coordinate<AXIS>): Offset<AXIS> = Offset(this.value - other.value)

    operator fun plus(offset: Offset<AXIS>): Coordinate<AXIS> = Coordinate(this.value + offset.value)
    operator fun minus(offset: Offset<AXIS>): Coordinate<AXIS> = Coordinate(this.value - offset.value)

    override fun compareTo(other: Coordinate<AXIS>): Int = this.value.compareTo(other.value)

    override fun toString(): String = "$value"
}

typealias Row = Coordinate<Vertical>
typealias Col = Coordinate<Horizontal>
typealias Frame = Coordinate<Temporal>

fun Row.toCol(): Col = Col(this.value)
fun Col.toRos(): Row = Row(this.value)

data class Point(val row: Row, val col: Col) {
    fun transpose(): Point = Point(col.toRos(), row.toCol())

    operator fun minus(delta: Delta): Point = Point(
        this.row - delta.rowOffset,
        this.col - delta.colOffset
    )
}

infix fun Point.minusRows(deltaRows: Offset<Vertical>): Point = Point(
    this.row - deltaRows,
    this.col
)

infix fun Point.minusCols(deltaCols: Offset<Horizontal>): Point = Point(
    this.row,
    this.col - deltaCols
)