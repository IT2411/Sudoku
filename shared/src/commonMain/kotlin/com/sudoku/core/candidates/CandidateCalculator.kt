package com.sudoku.core.candidates

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates
import com.sudoku.core.model.SudokuBoard

object CandidateCalculator {

    fun getCandidates(board: SudokuBoard, position: CellPosition): Candidates {
        if (!board.isEmpty(position)) {
            return Candidates.NONE
        }

        val rowMask = calculateRowUsedMask(board, position.row)
        val colMask = calculateColumnUsedMask(board, position.column)
        val boxMask = calculateBoxUsedMask(board, position.box)

        val usedMask = rowMask or colMask or boxMask
        val candidateMask = Candidates.ALL_MASK and usedMask.inv()

        return Candidates.fromMask(candidateMask)
    }

    /**
     * Returns a Candidates bitmask of all digits currently placed in this cell's row, column, or 3x3 box.
     * Any note matching a digit in this mask is an active contradiction.
     */
    fun getBlockedCandidates(board: SudokuBoard, position: CellPosition): Candidates {
        val rowMask = calculateRowUsedMask(board, position.row)
        val colMask = calculateColumnUsedMask(board, position.column)
        val boxMask = calculateBoxUsedMask(board, position.box)
        return Candidates.fromMask(rowMask or colMask or boxMask)
    }

    fun calculateAllCandidates(board: SudokuBoard): Array<Candidates> {
        val rowMasks = IntArray(CellPosition.GRID_SIZE) { r -> calculateRowUsedMask(board, r) }
        val colMasks = IntArray(CellPosition.GRID_SIZE) { c -> calculateColumnUsedMask(board, c) }
        val boxMasks = IntArray(CellPosition.GRID_SIZE) { b -> calculateBoxUsedMask(board, b) }

        return Array(CellPosition.TOTAL_CELLS) { index ->
            val pos = CellPosition.fromIndex(index)
            if (!board.isEmpty(pos)) {
                Candidates.NONE
            } else {
                val used = rowMasks[pos.row] or colMasks[pos.column] or boxMasks[pos.box]
                Candidates.fromMask(Candidates.ALL_MASK and used.inv())
            }
        }
    }

    private fun calculateRowUsedMask(board: SudokuBoard, row: Int): Int {
        var mask = 0
        val offset = row * CellPosition.GRID_SIZE
        for (c in 0 until CellPosition.GRID_SIZE) {
            val v = board[offset + c]
            if (v != SudokuBoard.EMPTY_VALUE) {
                mask = mask or (1 shl (v - 1))
            }
        }
        return mask
    }

    private fun calculateColumnUsedMask(board: SudokuBoard, column: Int): Int {
        var mask = 0
        for (r in 0 until CellPosition.GRID_SIZE) {
            val v = board[r * CellPosition.GRID_SIZE + column]
            if (v != SudokuBoard.EMPTY_VALUE) {
                mask = mask or (1 shl (v - 1))
            }
        }
        return mask
    }

    private fun calculateBoxUsedMask(board: SudokuBoard, box: Int): Int {
        var mask = 0
        val startRow = (box / CellPosition.BOX_SIZE) * CellPosition.BOX_SIZE
        val startCol = (box % CellPosition.BOX_SIZE) * CellPosition.BOX_SIZE
        for (r in 0 until CellPosition.BOX_SIZE) {
            val rowOffset = (startRow + r) * CellPosition.GRID_SIZE
            for (c in 0 until CellPosition.BOX_SIZE) {
                val v = board[rowOffset + (startCol + c)]
                if (v != SudokuBoard.EMPTY_VALUE) {
                    mask = mask or (1 shl (v - 1))
                }
            }
        }
        return mask
    }
}
