package org.example.org.game1.algebra

typealias Row = Coordinate<RowAxis>
typealias Col = Coordinate<ColAxis>

data class Cell(val row: Row, val col: Col)

typealias RowCount = Length<RowAxis>
typealias ColCount = Length<ColAxis>

val ROWS_ZERO = RowCount(0)
val COLS_ZERO = ColCount(0)

data class GridSize(val rows: RowCount, val cols: ColCount) {
    operator fun contains(cell: Cell): Boolean = cell.row in rows && cell.col in cols

    val positive: Boolean get() = rows.value > 0 && cols.value > 0
}