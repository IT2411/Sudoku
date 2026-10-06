package com.sudoku.core.validator

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.SudokuBoard

object SudokuValidator {

    /**
     * Evaluates if placing [value] at [position] is legal according to Sudoku constraints.
     * Does not mutate the board.
     */
    fun isValidPlacement(board: SudokuBoard, position: CellPosition, value: Int): Boolean {
        if (value == SudokuBoard.EMPTY_VALUE) return true
        if (value !in SudokuBoard.MIN_VALUE..SudokuBoard.MAX_VALUE) return false

        // Check Row
        for (c in 0 until CellPosition.GRID_SIZE) {
            if (c != position.column && board[position.row, c] == value) return false
        }

        // Check Column
        for (r in 0 until CellPosition.GRID_SIZE) {
            if (r != position.row && board[r, position.column] == value) return false
        }

        // Check 3x3 Box
        val boxRowStart = (position.row / CellPosition.BOX_SIZE) * CellPosition.BOX_SIZE
        val boxColStart = (position.column / CellPosition.BOX_SIZE) * CellPosition.BOX_SIZE
        for (r in 0 until CellPosition.BOX_SIZE) {
            for (c in 0 until CellPosition.BOX_SIZE) {
                val currR = boxRowStart + r
                val currC = boxColStart + c
                if ((currR != position.row || currC != position.column) && board[currR, currC] == value) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * Validates whether a unit (row, col, or box) contains any duplicate numbers (ignoring 0/empty).
     */
    fun isUnitValid(unitValues: IntArray): Boolean {
        var mask = 0
        for (value in unitValues) {
            if (value == SudokuBoard.EMPTY_VALUE) continue
            val bit = 1 shl value
            if ((mask and bit) != 0) return false
            mask = mask or bit
        }
        return true
    }

    /**
     * Checks if the entire board contains any conflicting rules. Empty cells are permitted.
     */
    fun isBoardConsistent(board: SudokuBoard): Boolean {
        return validateBoard(board).isValid
    }

    /**
     * Checks if the board is completely filled and fully valid (solution reached).
     */
    fun isBoardSolved(board: SudokuBoard): Boolean {
        for (i in 0 until CellPosition.TOTAL_CELLS) {
            if (board[i] == SudokuBoard.EMPTY_VALUE) return false
        }
        return isBoardConsistent(board)
    }

    /**
     * Scans the board and identifies all conflicting positions.
     */
    fun validateBoard(board: SudokuBoard): ValidationResult {
        val conflicts = mutableSetOf<CellPosition>()

        // 1. Check Rows
        for (r in 0 until CellPosition.GRID_SIZE) {
            val seen = mutableMapOf<Int, CellPosition>()
            for (c in 0 until CellPosition.GRID_SIZE) {
                val pos = CellPosition.of(r, c)
                val value = board[pos]
                if (value == SudokuBoard.EMPTY_VALUE) continue
                if (seen.containsKey(value)) {
                    conflicts.add(pos)
                    conflicts.add(seen.getValue(value))
                } else {
                    seen[value] = pos
                }
            }
        }

        // 2. Check Columns
        for (c in 0 until CellPosition.GRID_SIZE) {
            val seen = mutableMapOf<Int, CellPosition>()
            for (r in 0 until CellPosition.GRID_SIZE) {
                val pos = CellPosition.of(r, c)
                val value = board[pos]
                if (value == SudokuBoard.EMPTY_VALUE) continue
                if (seen.containsKey(value)) {
                    conflicts.add(pos)
                    conflicts.add(seen.getValue(value))
                } else {
                    seen[value] = pos
                }
            }
        }

        // 3. Check 3x3 Boxes
        for (box in 0 until CellPosition.GRID_SIZE) {
            val seen = mutableMapOf<Int, CellPosition>()
            val startRow = (box / CellPosition.BOX_SIZE) * CellPosition.BOX_SIZE
            val startCol = (box % CellPosition.BOX_SIZE) * CellPosition.BOX_SIZE
            for (r in 0 until CellPosition.BOX_SIZE) {
                for (c in 0 until CellPosition.BOX_SIZE) {
                    val pos = CellPosition.of(startRow + r, startCol + c)
                    val value = board[pos]
                    if (value == SudokuBoard.EMPTY_VALUE) continue
                    if (seen.containsKey(value)) {
                        conflicts.add(pos)
                        conflicts.add(seen.getValue(value))
                    } else {
                        seen[value] = pos
                    }
                }
            }
        }

        return if (conflicts.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(
                conflictingPositions = conflicts,
                reason = "Detected duplicate numbers in row, column, or 3x3 box."
            )
        }
    }
}