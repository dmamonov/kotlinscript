package org.example.org.game1.collisions

import org.example.org.game1.algebra.Box
import javax.security.auth.callback.Callback

@JvmInline
value class BoxId(private val value: Int = ++sequence) {
    companion object {
        private var sequence: Int = 0
    }
}

interface AABBIndex {
    fun update(id:BoxId, box: Box?)
    fun query(query:Box, callback: (Box)-> Unit)
}

enum class ContactDirection {
    LEFT,
    RIGHT,
    UP,
    DOWN;

    val opposite: ContactDirection
        get() = when (this) {
            LEFT -> RIGHT
            RIGHT -> LEFT
            UP -> DOWN
            DOWN -> UP
        }
}


