package ansi

sealed class Axis
object ColAxis : Axis()
object RowAxis : Axis()

data class Length<AXIS : ansi.Axis>(val value: Int) : Comparable<Length<AXIS>> {
    val steps: List<Coordinate<AXIS>> = (0 until value).map { Coordinate(it) }

    operator fun plus(other: Length<AXIS>): Length<AXIS> = Length(this.value + other.value)

    override fun compareTo(other: Length<AXIS>): Int = this.value.compareTo(other.value)
}
typealias Width = Length<ColAxis>
typealias Height = Length<RowAxis>

val WIDTH_0 = Width(0)
val WIDTH_1 = Width(1)
val HEIGHT_0 = Height(0)
val HEIGHT_1 = Height(1)

data class Coordinate<AXIS : ansi.Axis>(val value: Int) : Comparable<Coordinate<AXIS>> {
    fun isFirst(length: Length<AXIS>) = value == 0 && length.value > 0
    fun isLast(length: Length<AXIS>) = value == length.value - 1

    operator fun plus(other: Coordinate<AXIS>): Coordinate<AXIS> = Coordinate(this.value + other.value)

    override fun compareTo(other: Coordinate<AXIS>): Int {
        return this.value.compareTo(other.value)
    }
}

typealias Row = Coordinate<RowAxis>
typealias Col = Coordinate<ColAxis>

val ROW_0 = Row(0)
val ROW_1 = Row(1)
val COL_0 = Col(0)
val COL_1 = Col(1)

data class Point(val row: Row = ROW_0, val col: Col = COL_0) {
    operator fun minus(other: Point): Point = Point(
        Row(this.row.value - other.row.value),
        Col(this.col.value - other.col.value)
    )

    fun minusHeight(other: Height): Point = Point(
        Row(this.row.value - other.value),
        this.col
    )

    fun minusWidth(other: Width): Point = Point(
        this.row,
        Col(this.col.value - other.value),
    )

    val transpose: Point get() = Point(Row(this.col.value), Col(this.row.value))
}

data class Size(val width: Width, val height: Height) {
    operator fun plus(point: Point): Size = Size(
        Width(this.width.value + point.col.value),
        Height(this.height.value + point.row.value)
    )

    operator fun contains(point: Point): Boolean {
        val containsCol = point.col.value in 0 until width.value
        val containsRow = point.row.value in 0 until height.value
        return containsCol && containsRow
    }

    operator fun contains(inside: Size): Boolean {
        val containsWidth = inside.width.value in 0..width.value
        val containsHeight = inside.height.value in 0..height.value
        return containsWidth && containsHeight
    }
}

val SIZE_0 = Size(WIDTH_0, HEIGHT_0)

