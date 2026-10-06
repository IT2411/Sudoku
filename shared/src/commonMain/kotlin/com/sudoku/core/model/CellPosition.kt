package com.sudoku.core.model

/**
 * Represents a discrete coordinate on a standard 9x9 Sudoku grid.
 *
 * All coordinates are 0-indexed:
 * - row: 0..8 (top to bottom)
 * - column: 0..8 (left to right)
 * - box: 0..8 (top-left to bottom-right, 3x3 regions)
 * - index: 0..80 (row-major linear index: row * 9 + column)
 */
data class CellPosition(
    val row: Int,
    val column: Int
) {
    init {
        require(row in 0..8) { "Row must be within 0..8, but was $row" }
        require(column in 0..8) { "Column must be within 0..8, but was $column" }
    }

    val index: Int = row * GRID_SIZE + column
    val box: Int = (row / BOX_SIZE) * BOX_SIZE + (column / BOX_SIZE)

    companion object {
        const val GRID_SIZE = 9
        const val BOX_SIZE = 3
        const val TOTAL_CELLS = 81

        /**
         * Cache containing all 81 predefined CellPositions to avoid runtime allocations.
         */
        private val POOL = Array(TOTAL_CELLS) { idx ->
            CellPosition(idx / GRID_SIZE, idx % GRID_SIZE)
        }

        fun fromIndex(index: Int): CellPosition {
            require(index in 0 until TOTAL_CELLS) { "Index must be in 0..80, but was $index" }
            return POOL[index]
        }

        fun of(row: Int, column: Int): CellPosition {
            require(row in 0..8 && column in 0..8) { "Invalid row=$row or column=$column" }
            return POOL[row * GRID_SIZE + column]
        }

        val allPositions: List<CellPosition> = POOL.toList()
    }

    override fun toString(): String = "CellPosition(r=$row, c=$column, box=$box, idx=$index)"
}