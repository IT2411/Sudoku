package com.sudoku.core.validator

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.SudokuBoard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SudokuValidatorTest {

    private val validSolvedString = "" +
            "534678912" +
            "672195348" +
            "198342567" +
            "859761423" +
            "426853791" +
            "713924856" +
            "961537284" +
            "287419635" +
            "345286179"

    @Test
    fun testFullySolvedBoardIsValid() {
        val board = SudokuBoard.fromString(validSolvedString)
        val result = SudokuValidator.validateBoard(board)
        assertTrue(result.isValid)
        assertTrue(SudokuValidator.isBoardSolved(board))
    }

    @Test
    fun testEmptyBoardIsConsistentButNotSolved() {
        val emptyBoard = SudokuBoard()
        assertTrue(SudokuValidator.isBoardConsistent(emptyBoard))
        assertFalse(SudokuValidator.isBoardSolved(emptyBoard))
    }

    @Test
    fun testRowConflictDetected() {
        // Place duplicate '5' on row 0
        val board = SudokuBoard.fromString(validSolvedString).withCell(CellPosition.of(0, 8), 5)
        val result = SudokuValidator.validateBoard(board)

        assertFalse(result.isValid)
        val invalid = result as ValidationResult.Invalid
        assertTrue(invalid.conflictingPositions.contains(CellPosition.of(0, 0)))
        assertTrue(invalid.conflictingPositions.contains(CellPosition.of(0, 8)))
    }

    @Test
    fun testBoxConflictDetected() {
        // In top-left box, position (2, 2) is 8. Force position (1, 1) to 8.
        val board = SudokuBoard.fromString(validSolvedString).withCell(CellPosition.of(1, 1), 8)
        val result = SudokuValidator.validateBoard(board)

        assertFalse(result.isValid)
        val invalid = result as ValidationResult.Invalid
        assertTrue(invalid.conflictingPositions.contains(CellPosition.of(1, 1)))
        assertTrue(invalid.conflictingPositions.contains(CellPosition.of(2, 2)))
    }

    @Test
    fun testIsValidPlacement() {
        val board = SudokuBoard()
            .withCell(CellPosition.of(0, 0), 1)
            .withCell(CellPosition.of(1, 1), 2)

        // Placing 1 in the same row should fail
        assertFalse(SudokuValidator.isValidPlacement(board, CellPosition.of(0, 5), 1))

        // Placing 1 in the same column should fail
        assertFalse(SudokuValidator.isValidPlacement(board, CellPosition.of(5, 0), 1))

        // Placing 1 in the same box should fail
        assertFalse(SudokuValidator.isValidPlacement(board, CellPosition.of(2, 2), 1))

        // Placing 3 at (0, 5) should pass
        assertTrue(SudokuValidator.isValidPlacement(board, CellPosition.of(0, 5), 3))
    }
}