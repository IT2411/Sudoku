package com.sudoku.core.game

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates

/**
 * Reversible delta of a board modification.
 */
data class Move(
    val position: CellPosition,
    val previousValue: Int,
    val newValue: Int,
    val previousCellNotes: Candidates,
    val newCellNotes: Candidates,
    // Notes automatically eliminated in peer cells (row, col, box) when placing a number
    val secondaryNotesEliminated: Map<CellPosition, Candidates> = emptyMap(),
    val wasMistake: Boolean = false,
    val timestamp: Long = 0L
)