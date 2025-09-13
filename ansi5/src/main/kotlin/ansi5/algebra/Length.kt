package ansi5.algebra

@JvmInline
value class Length<AXIS : Axis>(val value: Int) : Comparable<Length<AXIS>> {
    override fun compareTo(other: Length<AXIS>): Int = this.value.compareTo(other.value)

    operator fun plus(offset: Offset<AXIS>): Length<AXIS> = Length(this.value + offset.value)

    operator fun plus(other: Length<AXIS>): Length<AXIS> = Length(this.value + other.value)

    operator fun contains(coordinate: Coordinate<AXIS>): Boolean = coordinate.value in 0 until value

    val asOffset: Offset<AXIS> get() = Offset(value)
    val asCoordinate: Coordinate<AXIS> get() = Coordinate(value)

    override fun toString(): String = "$value"
}

typealias Width = Length<Horizontal>
typealias Height = Length<Vertical>
typealias Duration = Length<Temporal>

fun Width.toHeight(): Height = Height(this.value)
fun Height.toWidth(): Width = Width(this.value)

val WIDTH_ZRO = Width(0)
val WIDTH_ONE = Width(1)

val HEIGHT_ZERO = Height(0)
val HEIGHT_ONE = Height(1)


data class Size(val width: Width, val height: Height) {
    fun transpose(): Size = Size(
        height.toWidth(),
        width.toHeight()
    )

    operator fun plus(delta: Delta): Size = Size(
        this.width + delta.colOffset,
        this.height + delta.rowOffset
    )

    val rows: Iterable<Row> get() = (0 until height.value).map { Row(it) }
    val rows1: Iterable<Row> get() = (1.. height.value).map { Row(it) }

    val cols: Iterable<Col> get() = (0 until width.value).map { Col(it) }
    val cols1: Iterable<Col> get() = (1.. width.value).map { Col(it) }

    operator fun contains(point: Point): Boolean = point.col in width && point.row in height
}