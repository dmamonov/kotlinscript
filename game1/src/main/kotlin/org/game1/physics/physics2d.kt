package org.example.org.game1.physics

data class Vector2D<UNIT : UnitTag>(val x: Double, val y: Double) {
    operator fun plus(other: Vector2D<UNIT>): Vector2D<UNIT> = Vector2D(this.x + other.x, this.y + other.y)
    operator fun minus(other: Vector2D<UNIT>): Vector2D<UNIT> = Vector2D(this.x - other.x, this.y - other.y)
    operator fun times(factor: Double): Vector2D<UNIT> = Vector2D(this.x * factor, this.y * factor)
}

operator fun <OVER : UnitTag, UNDER : UnitTag> Vector2D<Per<OVER, UNDER>>.times(
    factor: Quantity<UNDER>
): Vector2D<OVER> = Vector2D(this.x * factor.value , this.y * factor.value )

typealias Position2D = Vector2D<UnitMeter>
typealias Velocity2D = Vector2D<UnitVelocity>
typealias Acceleration2D = Vector2D<UnitAcceleration>
typealias Force2D = Vector2D<UnitAcceleration>