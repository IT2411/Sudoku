package com.sudoku.core.generator

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.SudokuBoard

/**
 * Encapsulates a newly generated puzzle, its complete solution, and generation metadata.
 */
data class GeneratedPuzzle(
    val initialBoard: SudokuBoard,
    val solution: SudokuBoard,
    val seed: Long,
    val clueCount: Int
) {
    /**
     * Set of positions that are pre-filled clues (givens) and cannot be erased by the player.
     */
    val givenPositions: Set<CellPosition> = buildSet {
        for (i in 0 until CellPosition.TOTAL_CELLS) {
            if (initialBoard[i] != SudokuBoard.EMPTY_VALUE) {
                add(CellPosition.fromIndex(i))
            }
        }
    }
}