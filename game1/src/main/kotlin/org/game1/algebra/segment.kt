package org.example.org.game1.algebra

data class Segment<AXIS : Axis>(val min: Coordinate<AXIS>, val max: Coordinate<AXIS>) {
    init {
        require(min.value <= max.value) { "min must be <= max.value" }
    }

    infix fun intersect(other: Segment<AXIS>): Segment<AXIS>? {
        val newMin = maxOf(this.min, other.min)
        val newMax = minOf(this.max, other.max)
        return if (newMin <= newMax) Segment(newMin, newMax) else null
    }

    operator fun plus(delta: Delta<AXIS>): Segment<AXIS> = Segment(
        min = this.min + delta,
        max = this.max + delta
    )

    operator fun minus(delta: Delta<AXIS>): Segment<AXIS> = Segment(
        min = this.min - delta,
        max = this.max - delta
    )

    val coordinates: Iterable<Coordinate<AXIS>> = (min.value until max.value).map {
        Coordinate(it)
    }
}

typealias SX = Segment<XAxis>
typealias SY = Segment<YAxis>

val ZERO_SX = SX(ZERO_X, ZERO_X)
val ZERO_SY = SY(ZERO_Y, ZERO_Y)

infix fun SX?.nullBox(sy: SY?): Box? = if (this == null || sy == null) null else Box(this, sy)