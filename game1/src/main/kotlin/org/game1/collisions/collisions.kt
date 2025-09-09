package org.example.org.game1.collisions

@JvmInline
value class BoxId(private val value: Int = ++sequence) {
    companion object {
        private var sequence: Int = 0
    }
}


