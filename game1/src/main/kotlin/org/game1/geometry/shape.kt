package org.example.org.game1.geometry

import org.example.org.game1.algebra.Box

@JvmInline
value class ShapeId(private val value: Int = ++sequence) {
    companion object {
        private var sequence: Int = 0
    }
}

data class Shape(val boxes: List<Box>)