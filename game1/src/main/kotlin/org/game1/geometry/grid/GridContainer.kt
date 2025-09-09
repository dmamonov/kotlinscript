package org.example.org.game1.geometry.grid

import org.example.org.game1.algebra.Box
import org.example.org.game1.algebra.Cell
import org.example.org.game1.algebra.GridSize
import org.example.org.game1.algebra.Size
import java.util.stream.Stream

interface GridContainer<TILE> {
    val cellSize: Size
    val gridSize: GridSize

    fun insert(cell: Cell, tile: TILE): Boolean
    fun delete(cell: Cell, tile: TILE): Boolean

    operator fun get(cell: Cell): Stream<TILE>

    fun query(box: Box): Stream<Pair<Cell, TILE>>
}