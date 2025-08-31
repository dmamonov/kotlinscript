package teya.ansi.compose2

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

data class Point(val row: RowIndex, val col: ColIndex)


data class Box(val width: Width, val height: Height) {
    fun stack(other: Box): Box {
        return Box(
            maxOf(this.width, other.width),
            this.height + other.height,
        )
    }

    fun line(other: Box): Box {
        return Box(
            this.width + other.width,
            maxOf(this.height, other.height),
        )
    }

    operator fun contains(point: Point): Boolean {
        return point.row in height && point.col in width
    }


    operator fun minus(cutWidth: Width): Box = Box(this.width - cutWidth, this.height)

    // operator fun minus(cutHeight: Height): Box = Box(this.width, this.height - cutHeight)

    val hasArea: Boolean get() = width.value > 0 && height.value > 0

    fun toWidth(newWidth: Width): Box = Box(newWidth, height)
    fun minWidth(minWidth: Width): Box = Box(maxOf(minWidth, WIDTH1), height)
    fun toHeight(newHeight: Height): Box = Box(width, newHeight)
    fun minHeight(minHeight: Height): Box = Box(width, maxOf(minHeight, height))
    fun minBox(minBox: Box): Box = Box(maxOf(width, minBox.width), maxOf(height, minBox.height))
}


@JvmInline
value class Size<Orientation>(val value: Int) : Comparable<Size<Orientation>> {
    val range: List<Index<Orientation>> get() = (0 until value).map { Index(it) }
    fun check(col: Index<Orientation>) = check(col.value in 0 until value)
    override fun compareTo(other: Size<Orientation>): Int = this.value.compareTo(other.value)

    operator fun plus(other: Size<Orientation>): Size<Orientation> = Size(this.value + other.value)

    operator fun minus(other: Size<Orientation>): Size<Orientation> = Size(this.value - other.value)

    operator fun div(factor: Int): Size<Orientation> = Size(this.value / factor)

    operator fun contains(index: Index<Orientation>): Boolean = index.value in 0 until this.value
}

typealias Width = Size<Col>
typealias Height = Size<Row>

val WIDTH0 = Width(0)
val WIDTH1 = Width(1)

val HEIGHT0 = Height(0)
val HEIGHT1 = Height(1)

val EMPTY_BOX = Box(WIDTH0, HEIGHT0)
val UNIT_BOX = Box(WIDTH1, HEIGHT1)
