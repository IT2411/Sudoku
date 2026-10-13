package com.sudoku.core.interaction

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates
import com.sudoku.core.model.SudokuBoard
import com.sudoku.core.validator.SudokuValidator
import com.sudoku.core.validator.ValidationResult

object BoardSelectionEngine {

    /**
     * Computes the complete highlighting map for the board based on the currently selected cell.
     *
     * @param board The current state of the board.
     * @param notes Provider for pencil notes for each cell.
     * @param selectedCell Currently focused cell, or null if nothing is selected.
     * @param activeDigitOverride Explicit digit focus (e.g. if the user pressed a number pad button first).
     *                             If null, uses the digit inside [selectedCell].
     */
    fun computeHighlights(
        board: SudokuBoard,
        notes: (CellPosition) -> Candidates,
        selectedCell: CellPosition?,
        activeDigitOverride: Int? = null
    ): Array<CellHighlightState> {
        // 1. Identify active digit
        val activeDigit = activeDigitOverride ?: selectedCell?.let { pos ->
            val v = board[pos]
            if (v != SudokuBoard.EMPTY_VALUE) v else null
        }

        // 2. Identify all conflicting cells on the board
        val conflicts: Set<CellPosition> = when (val res = SudokuValidator.validateBoard(board)) {
            is ValidationResult.Invalid -> res.conflictingPositions
            is ValidationResult.Valid -> emptySet()
        }

        // 3. Evaluate state for each of the 81 cells
        return Array(CellPosition.TOTAL_CELLS) { index ->
            val pos = CellPosition.fromIndex(index)
            val isSelected = pos == selectedCell

            val isPeer = if (selectedCell != null && !isSelected) {
                pos.row == selectedCell.row ||
                        pos.column == selectedCell.column ||
                        pos.box == selectedCell.box
            } else {
                false
            }

            val cellValue = board[pos]
            val isSameDigit = activeDigit != null && cellValue == activeDigit

            val hasMatchingNote = if (activeDigit != null && cellValue == SudokuBoard.EMPTY_VALUE) {
                notes(pos).contains(activeDigit)
            } else {
                false
            }

            val hasConflict = conflicts.contains(pos)

            CellHighlightState(
                isSelected = isSelected,
                isPeer = isPeer,
                isSameDigit = isSameDigit,
                hasMatchingNote = hasMatchingNote,
                hasConflict = hasConflict
            )
        }
    }
}