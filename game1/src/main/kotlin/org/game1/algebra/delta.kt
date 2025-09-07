package org.example.org.game1.algebra

@JvmInline
value class Delta<AXIS : Axis>(override val value: Int) : IntScalar {
    operator fun times(factor: Double): Delta<AXIS> = Delta((value * factor).toInt())
}

typealias DX = Delta<XAxis>
typealias DY = Delta<YAxis>

data class DXY(val dx: DX, val dy: DY) {
    val lengthSquared: Int get() = dx.squared + dy.squared
    val length: Double = intSqrt(lengthSquared)
    infix fun scale(factor: Double): DXY = DXY(dx * factor, dy * factor)
    infix fun scalarProduct(other: DXY): Int = this.dx.value * other.dx.value + this.dy.value * other.dy.value
    infix fun vectorProduct(other: DXY): Int = this.dx.value * other.dy.value - this.dy.value * other.dx.value
}

fun dxy(dx: Int = 0, dy: Int = 0) = DXY(DX(dx), DY(dy))