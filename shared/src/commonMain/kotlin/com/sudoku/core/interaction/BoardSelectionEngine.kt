package com.sudoku.core.interaction

import com.sudoku.core.difficulty.TechniqueStep
import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates
import com.sudoku.core.model.SudokuBoard
import com.sudoku.core.validator.SudokuValidator
import com.sudoku.core.validator.ValidationResult

object BoardSelectionEngine {

    fun computeHighlights(
        board: SudokuBoard,
        notes: (CellPosition) -> Candidates,
        selectedCell: CellPosition?,
        activeDigitOverride: Int? = null,
        activeHint: TechniqueStep? = null
    ): Array<CellHighlightState> {
        val activeDigit = activeDigitOverride ?: selectedCell?.let { pos ->
            val v = board[pos]
            if (v != SudokuBoard.EMPTY_VALUE) v else null
        }

        val conflicts: Set<CellPosition> = when (val res = SudokuValidator.validateBoard(board)) {
            is ValidationResult.Invalid -> res.conflictingPositions
            is ValidationResult.Valid -> emptySet()
        }

        val hintPrimary = activeHint?.primaryCells ?: emptySet()
        val hintSecondary = activeHint?.secondaryCells ?: emptySet()

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
                hasConflict = hasConflict,
                isHintPrimary = hintPrimary.contains(pos),
                isHintSecondary = hintSecondary.contains(pos)
            )
        }
    }
}