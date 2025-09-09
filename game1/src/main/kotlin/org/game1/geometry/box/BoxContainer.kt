package org.example.org.game1.geometry.box

import org.example.org.game1.algebra.Box
import org.example.org.game1.collisions.BoxId

interface BoxContainer {
    fun insert(id: BoxId, box: Box)

    fun update(id: BoxId, box: Box)

    fun delete(id: BoxId)

    fun query(box: Box): Set<BoxId>
}