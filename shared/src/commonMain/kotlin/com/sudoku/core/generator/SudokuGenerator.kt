package com.sudoku.core.generator

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.SudokuBoard
import com.sudoku.core.solver.SudokuSolver
import kotlin.random.Random

object SudokuGenerator {

    private const val MIN_POSSIBLE_CLUES = 17 // Mathematical minimum for any valid 9x9 Sudoku

    /**
     * Generates a valid Sudoku puzzle guaranteed to have exactly one unique solution.
     *
     * @param targetClues Desired number of given clues (typical range: 24 to 40).
     * @param seed Optional seed for deterministic generation (e.g. for Daily Sudoku).
     * @param symmetric Whether to carve clues rotationally (180-degree symmetry).
     */
    fun generate(
        targetClues: Int = 32,
        seed: Long = Random.nextLong(),
        symmetric: Boolean = true
    ): GeneratedPuzzle {
        val clampedTarget = targetClues.coerceIn(MIN_POSSIBLE_CLUES, CellPosition.TOTAL_CELLS)
        val random = Random(seed)

        // 1. Generate a complete, valid solved board
        val solution = generateSolvedBoard(random)

        // 2. Carve clues while ensuring the puzzle retains exactly 1 unique solution
        val puzzleBoard = carveClues(solution, clampedTarget, random, symmetric)

        var clues = 0
        for (i in 0 until CellPosition.TOTAL_CELLS) {
            if (puzzleBoard[i] != SudokuBoard.EMPTY_VALUE) clues++
        }

        return GeneratedPuzzle(
            initialBoard = puzzleBoard,
            solution = solution,
            seed = seed,
            clueCount = clues
        )
    }

    /**
     * Creates a fully populated valid 9x9 board.
     *
     * Algorithm:
     * 1. The three diagonal 3x3 boxes (Box 0, Box 4, Box 8) do not share rows or columns.
     *    Fill them independently with random permutations of 1..9.
     * 2. Run the deterministic solver to fill the remaining 6 boxes.
     */
    fun generateSolvedBoard(random: Random): SudokuBoard {
        val board = SudokuBoard()
        val digits = (1..9).toList()

        // Fill diagonal boxes 0, 4, 8
        for (box in listOf(0, 4, 8)) {
            val shuffled = digits.shuffled(random)
            val startRow = (box / CellPosition.BOX_SIZE) * CellPosition.BOX_SIZE
            val startCol = (box % CellPosition.BOX_SIZE) * CellPosition.BOX_SIZE
            var idx = 0
            for (r in 0 until CellPosition.BOX_SIZE) {
                for (c in 0 until CellPosition.BOX_SIZE) {
                    val pos = CellPosition.of(startRow + r, startCol + c)
                    board.setRaw(pos.index, shuffled[idx++])
                }
            }
        }

        // Solve to complete the remaining cells
        return checkNotNull(SudokuSolver.solve(board)) {
            "Failed to solve randomized diagonal board during generation"
        }
    }

    private fun carveClues(
        solution: SudokuBoard,
        targetClues: Int,
        random: Random,
        symmetric: Boolean
    ): SudokuBoard {
        val grid = solution.rawValues()
        var currentClues = CellPosition.TOTAL_CELLS

        // Build list of indices to attempt removal
        val indices = (0 until CellPosition.TOTAL_CELLS).shuffled(random)

        for (index in indices) {
            if (currentClues <= targetClues) break
            if (grid[index] == SudokuBoard.EMPTY_VALUE) continue

            val oppositeIndex = CellPosition.TOTAL_CELLS - 1 - index

            if (symmetric && index != oppositeIndex) {
                if (grid[oppositeIndex] == SudokuBoard.EMPTY_VALUE) continue
                if (currentClues - 2 < targetClues) continue

                val val1 = grid[index]
                val val2 = grid[oppositeIndex]

                grid[index] = SudokuBoard.EMPTY_VALUE
                grid[oppositeIndex] = SudokuBoard.EMPTY_VALUE

                val testBoard = SudokuBoard.fromArray(grid)
                if (SudokuSolver.countSolutions(testBoard, maxLimit = 2) == 1) {
                    currentClues -= 2
                } else {
                    // Restore both
                    grid[index] = val1
                    grid[oppositeIndex] = val2
                }
            } else {
                val orig = grid[index]
                grid[index] = SudokuBoard.EMPTY_VALUE

                val testBoard = SudokuBoard.fromArray(grid)
                if (SudokuSolver.countSolutions(testBoard, maxLimit = 2) == 1) {
                    currentClues--
                } else {
                    // Restore
                    grid[index] = orig
                }
            }
        }

        return SudokuBoard.fromArray(grid)
    }
}