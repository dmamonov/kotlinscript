package org.example.org.game1.algebra

import kotlin.math.sqrt

interface IntScalar {
    val value: Int
    val squared: Int get() = value * value
}

fun intSqrt(value:Int): Double = sqrt(value.toDouble())