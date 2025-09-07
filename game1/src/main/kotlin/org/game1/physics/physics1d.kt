package org.example.org.game1.physics

@JvmInline
value class Quantity<UNIT : UnitTag>(val value: Double) : Comparable<Quantity<UNIT>> {
    operator fun plus(other: Quantity<UNIT>) = Quantity<UNIT>(value + other.value)
    operator fun minus(other: Quantity<UNIT>) = Quantity<UNIT>(value - other.value)
    operator fun unaryMinus() = Quantity<UNIT>(-value)
    override fun compareTo(other: Quantity<UNIT>): Int = this.value.compareTo(other.value)
}

sealed interface UnitTag
interface Per<OVER : UnitTag, UNDER : UnitTag> : UnitTag
interface Times<LEFT : UnitTag, BRIGHT : UnitTag> : UnitTag

object UnitMeter : UnitTag
object UnitSecond : UnitTag
object UnitKilogram : UnitTag

// Derived units often used
typealias UnitVelocity = Per<UnitMeter, UnitSecond>
typealias UnitAcceleration = Per<UnitMeter, Times<UnitSecond, UnitSecond>>
typealias UnitMomentum = Times<UnitKilogram, UnitVelocity>
typealias UnitForce = Times<UnitKilogram, UnitAcceleration>

// Handy aliases
typealias Meter = Quantity<UnitMeter>
typealias Kilogram = Quantity<UnitKilogram>
typealias Second = Quantity<UnitSecond>
typealias Velocity = Quantity<UnitVelocity>
typealias Acceleration = Quantity<UnitAcceleration>
typealias Force = Quantity<UnitForce>





