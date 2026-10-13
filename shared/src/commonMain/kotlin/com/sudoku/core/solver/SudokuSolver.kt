package com.sudoku.core.solver

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates
import com.sudoku.core.model.SudokuBoard
import com.sudoku.core.validator.SudokuValidator

/**
 * High-performance deterministic Sudoku solver utilizing:
 * 1. Constraint Propagation (Row, Column, Box bitmasks)
 * 2. MRV Heuristic (Minimum Remaining Values / Most Constrained Variable)
 * 3. In-place Backtracking
 */
object SudokuSolver {

    /**
     * Solves the given Sudoku board.
     *
     * @param board The starting board (may be empty, partially filled, or complete).
     * @return The solved [SudokuBoard], or `null` if the board is mathematically unsolvable.
     */
    fun solve(board: SudokuBoard): SudokuBoard? {
        if (!SudokuValidator.isBoardConsistent(board)) {
            return null
        }

        val grid = board.rawValues()
        val rowMasks = IntArray(CellPosition.GRID_SIZE)
        val colMasks = IntArray(CellPosition.GRID_SIZE)
        val boxMasks = IntArray(CellPosition.GRID_SIZE)

        initializeMasks(grid, rowMasks, colMasks, boxMasks)

        val solved = solveBacktracking(grid, rowMasks, colMasks, boxMasks)
        return if (solved) SudokuBoard.fromArray(grid) else null
    }

    /**
     * Counts the number of valid solutions up to [maxLimit].
     *
     * Essential for the Puzzle Generator (Phase 4) to confirm uniqueness (solutions == 1)
     * without wasting CPU cycles exploring thousands of branches on ambiguous puzzles.
     *
     * @param board The board to evaluate.
     * @param maxLimit The upper threshold to halt counting (default 2).
     * @return Number of found solutions (up to maxLimit).
     */
    fun countSolutions(board: SudokuBoard, maxLimit: Int = 2): Int {
        require(maxLimit >= 1) { "maxLimit must be at least 1" }
        if (!SudokuValidator.isBoardConsistent(board)) {
            return 0
        }

        val grid = board.rawValues()
        val rowMasks = IntArray(CellPosition.GRID_SIZE)
        val colMasks = IntArray(CellPosition.GRID_SIZE)
        val boxMasks = IntArray(CellPosition.GRID_SIZE)

        initializeMasks(grid, rowMasks, colMasks, boxMasks)

        var count = 0

        fun countBacktracking(): Boolean {
            // Find cell with Minimum Remaining Values (MRV)
            var bestIndex = -1
            var minCandidatesCount = 10
            var bestCandidateMask = 0

            for (index in 0 until CellPosition.TOTAL_CELLS) {
                if (grid[index] == SudokuBoard.EMPTY_VALUE) {
                    val pos = CellPosition.fromIndex(index)
                    val used = rowMasks[pos.row] or colMasks[pos.column] or boxMasks[pos.box]
                    val candidateMask = Candidates.ALL_MASK and used.inv()
                    val candidateCount = candidateMask.countOneBits()

                    if (candidateCount == 0) {
                        // Dead end: empty cell with no possible valid numbers
                        return false
                    }

                    if (candidateCount < minCandidatesCount) {
                        minCandidatesCount = candidateCount
                        bestIndex = index
                        bestCandidateMask = candidateMask
                        if (candidateCount == 1) {
                            // Immediate naked single found
                            break
                        }
                    }
                }
            }

            // If no empty cells remain, a complete solution is found
            if (bestIndex == -1) {
                count++
                return count >= maxLimit // Stop search once limit is reached
            }

            val pos = CellPosition.fromIndex(bestIndex)
            var mask = bestCandidateMask

            while (mask != 0) {
                val bit = mask and -mask // Isolate lowest set bit
                val digit = bit.countTrailingZeroBits() + 1

                // Place digit
                grid[bestIndex] = digit
                rowMasks[pos.row] = rowMasks[pos.row] or bit
                colMasks[pos.column] = colMasks[pos.column] or bit
                boxMasks[pos.box] = boxMasks[pos.box] or bit

                val reachedLimit = countBacktracking()

                // Backtrack
                grid[bestIndex] = SudokuBoard.EMPTY_VALUE
                rowMasks[pos.row] = rowMasks[pos.row] and bit.inv()
                colMasks[pos.column] = colMasks[pos.column] and bit.inv()
                boxMasks[pos.box] = boxMasks[pos.box] and bit.inv()

                if (reachedLimit) return true
                mask = mask and (mask - 1) // Clear lowest set bit
            }

            return false
        }

        countBacktracking()
        return count
    }

    private fun solveBacktracking(
        grid: IntArray,
        rowMasks: IntArray,
        colMasks: IntArray,
        boxMasks: IntArray
    ): Boolean {
        // Find cell with Minimum Remaining Values (MRV)
        var bestIndex = -1
        var minCandidatesCount = 10
        var bestCandidateMask = 0

        for (index in 0 until CellPosition.TOTAL_CELLS) {
            if (grid[index] == SudokuBoard.EMPTY_VALUE) {
                val pos = CellPosition.fromIndex(index)
                val used = rowMasks[pos.row] or colMasks[pos.column] or boxMasks[pos.box]
                val candidateMask = Candidates.ALL_MASK and used.inv()
                val candidateCount = candidateMask.countOneBits()

                if (candidateCount == 0) {
                    return false // Contradiction
                }

                if (candidateCount < minCandidatesCount) {
                    minCandidatesCount = candidateCount
                    bestIndex = index
                    bestCandidateMask = candidateMask
                    if (candidateCount == 1) break
                }
            }
        }

        // Board is complete
        if (bestIndex == -1) {
            return true
        }

        val pos = CellPosition.fromIndex(bestIndex)
        var mask = bestCandidateMask

        while (mask != 0) {
            val bit = mask and -mask
            val digit = bit.countTrailingZeroBits() + 1

            grid[bestIndex] = digit
            rowMasks[pos.row] = rowMasks[pos.row] or bit
            colMasks[pos.column] = colMasks[pos.column] or bit
            boxMasks[pos.box] = boxMasks[pos.box] or bit

            if (solveBacktracking(grid, rowMasks, colMasks, boxMasks)) {
                return true
            }

            // Backtrack
            grid[bestIndex] = SudokuBoard.EMPTY_VALUE
            rowMasks[pos.row] = rowMasks[pos.row] and bit.inv()
            colMasks[pos.column] = colMasks[pos.column] and bit.inv()
            boxMasks[pos.box] = boxMasks[pos.box] and bit.inv()

            mask = mask and (mask - 1)
        }

        return false
    }

    private fun initializeMasks(
        grid: IntArray,
        rowMasks: IntArray,
        colMasks: IntArray,
        boxMasks: IntArray
    ) {
        for (index in 0 until CellPosition.TOTAL_CELLS) {
            val v = grid[index]
            if (v != SudokuBoard.EMPTY_VALUE) {
                val pos = CellPosition.fromIndex(index)
                val bit = 1 shl (v - 1)
                rowMasks[pos.row] = rowMasks[pos.row] or bit
                colMasks[pos.column] = colMasks[pos.column] or bit
                boxMasks[pos.box] = boxMasks[pos.box] or bit
            }
        }
    }
}