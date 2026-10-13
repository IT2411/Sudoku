package com.sudoku.core.generator

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.SudokuBoard
import com.sudoku.core.solver.SudokuSolver
import com.sudoku.core.validator.SudokuValidator
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SudokuGeneratorTest {

    @Test
    fun testGenerateSolvedBoardIsValidAndComplete() {
        val solvedBoard = SudokuGenerator.generateSolvedBoard(Random(42L))

        assertTrue(SudokuValidator.isBoardSolved(solvedBoard))
        for (i in 0 until CellPosition.TOTAL_CELLS) {
            assertTrue(solvedBoard[i] != SudokuBoard.EMPTY_VALUE)
        }
    }

    @Test
    fun testGeneratedPuzzleHasUniqueSolution() {
        val puzzle = SudokuGenerator.generate(targetClues = 34, seed = 12345L)

        // Must have exactly 1 solution
        val solutionCount = SudokuSolver.countSolutions(puzzle.initialBoard, maxLimit = 2)
        assertEquals(1, solutionCount)

        // Solving the puzzle must match the generated solution
        val solved = SudokuSolver.solve(puzzle.initialBoard)
        assertEquals(puzzle.solution, solved)
    }

    @Test
    fun testDeterministicGenerationWithSameSeed() {
        val seed = 987654321L
        val puzzle1 = SudokuGenerator.generate(targetClues = 30, seed = seed)
        val puzzle2 = SudokuGenerator.generate(targetClues = 30, seed = seed)

        assertEquals(puzzle1.initialBoard, puzzle2.initialBoard)
        assertEquals(puzzle1.solution, puzzle2.solution)
        assertEquals(puzzle1.clueCount, puzzle2.clueCount)
    }

    @Test
    fun testSymmetricClueRemoval() {
        val puzzle = SudokuGenerator.generate(targetClues = 32, seed = 555L, symmetric = true)

        for (i in 0 until CellPosition.TOTAL_CELLS) {
            val oppositeIndex = CellPosition.TOTAL_CELLS - 1 - i
            val hasClue = puzzle.initialBoard[i] != SudokuBoard.EMPTY_VALUE
            val oppositeHasClue = puzzle.initialBoard[oppositeIndex] != SudokuBoard.EMPTY_VALUE
            assertEquals(hasClue, oppositeHasClue, "Symmetry violated between index $i and $oppositeIndex")
        }
    }
}