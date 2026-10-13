package com.sudoku.core.solver

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.SudokuBoard
import com.sudoku.core.validator.SudokuValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SudokuSolverTest {

    // Standard known puzzle (Easy/Medium)
    private val puzzle = "" +
            "530070000" +
            "600195000" +
            "098000060" +
            "800060003" +
            "400803001" +
            "700020006" +
            "060000280" +
            "000419005" +
            "000080079"

    private val expectedSolution = "" +
            "534678912" +
            "672195348" +
            "198342567" +
            "859761423" +
            "426853791" +
            "713924856" +
            "961537284" +
            "287419635" +
            "345286179"

    // Hard known puzzle ("Arto Inkala's AI Sudoku" - considered one of the hardest)
    private val notoriouslyHardPuzzle = "" +
            "800000000" +
            "003600000" +
            "070090200" +
            "050007000" +
            "000045700" +
            "000100030" +
            "001000068" +
            "008500010" +
            "090000400"

    @Test
    fun testSolvesStandardPuzzleCorrectly() {
        val board = SudokuBoard.fromString(puzzle)
        val solved = SudokuSolver.solve(board)

        assertNotNull(solved)
        assertTrue(SudokuValidator.isBoardSolved(solved))
        assertEquals(SudokuBoard.fromString(expectedSolution), solved)
    }

    @Test
    fun testSolvesExtremelyHardPuzzle() {
        val board = SudokuBoard.fromString(notoriouslyHardPuzzle)
        val solved = SudokuSolver.solve(board)

        assertNotNull(solved)
        assertTrue(SudokuValidator.isBoardSolved(solved))
    }

    @Test
    fun testDetectsUnsolvableContradictionPuzzle() {
        // Create an impossible puzzle: same row has two identical numbers, but placed as given
        val invalidBoard = SudokuBoard.fromString(puzzle)
            .withCell(CellPosition.of(0, 8), 5) // Row 0 already has 5 at (0, 0)

        val solved = SudokuSolver.solve(invalidBoard)
        assertNull(solved)
    }

    @Test
    fun testCountSolutionsUniquePuzzle() {
        val board = SudokuBoard.fromString(puzzle)
        val count = SudokuSolver.countSolutions(board, maxLimit = 2)
        assertEquals(1, count)
    }

    @Test
    fun testCountSolutionsMultipleSolutions() {
        // An empty board has millions of solutions; counter should hit maxLimit (2) instantly
        val emptyBoard = SudokuBoard()
        val count = SudokuSolver.countSolutions(emptyBoard, maxLimit = 2)
        assertEquals(2, count)
    }

    @Test
    fun testCountSolutionsUnsolvable() {
        val invalidBoard = SudokuBoard.fromString(puzzle)
            .withCell(CellPosition.of(0, 8), 5)

        val count = SudokuSolver.countSolutions(invalidBoard)
        assertEquals(0, count)
    }
}