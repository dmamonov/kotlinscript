package org.example.org.game1.geometry.grid

import org.example.org.game1.algebra.Box
import org.example.org.game1.algebra.Cell
import org.example.org.game1.algebra.GridSize
import org.example.org.game1.algebra.Size
import java.util.stream.Stream


class ArrayGridContainer<TILE>(
    override val cellSize: Size,
    override val gridSize: GridSize
) : GridContainer<TILE> {
    init {
        check(cellSize.positive) { "Cell Size must be positive, $cellSize" }
        check(gridSize.positive) { "Grid Size must be positive, $gridSize" }
    }

    private val cells: Array<Array<MutableList<TILE>>> = Array(gridSize.rows.value) {
        Array(gridSize.cols.value) { mutableListOf() }
    }

    private fun checkCell(cell: Cell) {
        check(cell in gridSize) { "Cell $cell is out of bounds $gridSize" }
    }

    override fun insert(cell: Cell, tile: TILE): Boolean {
        checkCell(cell)
        return cells[cell.row.value][cell.col.value].add(tile)
    }

    override fun delete(cell: Cell, tile: TILE): Boolean {
        checkCell(cell)
        return cells[cell.row.value][cell.col.value].remove(tile)
    }

    override fun get(cell: Cell): Stream<TILE> {
        checkCell(cell)
        return cells[cell.row.value][cell.col.value].stream()
    }

    override fun query(box: Box): Stream<Pair<Cell, TILE>> {
        return box.splitByCells(cellSize)
            .filter { cell -> cell in gridSize }
            .flatMap { cell ->
                get(cell).map { tile -> cell to tile }
            }
    }
}