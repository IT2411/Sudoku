package com.sudoku.core.candidates

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates
import com.sudoku.core.model.SudokuBoard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CandidateCalculatorTest {

    @Test
    fun testEmptyBoardHasAllCandidatesForEveryCell() {
        val board = SudokuBoard()
        val pos = CellPosition.of(4, 4)
        val candidates = CandidateCalculator.getCandidates(board, pos)

        assertEquals(Candidates.ALL, candidates)
        assertEquals(9, candidates.size)
    }

    @Test
    fun testFilledCellHasNoCandidates() {
        val board = SudokuBoard().withCell(CellPosition.of(0, 0), 5)
        val candidates = CandidateCalculator.getCandidates(board, CellPosition.of(0, 0))

        assertTrue(candidates.isEmpty)
        assertEquals(Candidates.NONE, candidates)
    }

    @Test
    fun testRowElimination() {
        // Place 1, 2, 3 in Row 0
        val board = SudokuBoard()
            .withCell(CellPosition.of(0, 0), 1)
            .withCell(CellPosition.of(0, 1), 2)
            .withCell(CellPosition.of(0, 2), 3)

        val candidates = CandidateCalculator.getCandidates(board, CellPosition.of(0, 8))
        // 1, 2, 3 must be eliminated
        assertEquals(listOf(4, 5, 6, 7, 8, 9), candidates.toList())
    }

    @Test
    fun testColumnElimination() {
        // Place 4, 5, 6 in Column 2
        val board = SudokuBoard()
            .withCell(CellPosition.of(0, 2), 4)
            .withCell(CellPosition.of(1, 2), 5)
            .withCell(CellPosition.of(2, 2), 6)

        val candidates = CandidateCalculator.getCandidates(board, CellPosition.of(8, 2))
        assertEquals(listOf(1, 2, 3, 7, 8, 9), candidates.toList())
    }

    @Test
    fun testBoxElimination() {
        // Top-left box (Box 0): Place 7, 8, 9
        val board = SudokuBoard()
            .withCell(CellPosition.of(0, 0), 7)
            .withCell(CellPosition.of(1, 1), 8)
            .withCell(CellPosition.of(2, 2), 9)

        val candidates = CandidateCalculator.getCandidates(board, CellPosition.of(0, 1))
        assertEquals(listOf(1, 2, 3, 4, 5, 6), candidates.toList())
    }

    @Test
    fun testNakedSingleResolution() {
        // Construct a board where (0, 0) has all numbers 1..8 blocked by its row, col, and box
        // Row 0 has 1, 2, 3
        // Col 0 has 4, 5, 6
        // Box 0 has 7, 8 (at cell (1, 1) and (2, 2))
        val board = SudokuBoard()
            .withCell(CellPosition.of(0, 1), 1)
            .withCell(CellPosition.of(0, 2), 2)
            .withCell(CellPosition.of(0, 3), 3)
            .withCell(CellPosition.of(4, 0), 4)
            .withCell(CellPosition.of(5, 0), 5)
            .withCell(CellPosition.of(6, 0), 6)
            .withCell(CellPosition.of(1, 1), 7)
            .withCell(CellPosition.of(2, 2), 8)

        val candidates = CandidateCalculator.getCandidates(board, CellPosition.of(0, 0))
        assertEquals(1, candidates.size)
        assertEquals(9, candidates.singleOrNull())
    }

    @Test
    fun testCalculateAllCandidatesMatchesIndividualQueries() {
        val sample = "" +
                "530070000" +
                "600195000" +
                "098000060" +
                "800060003" +
                "400803001" +
                "700020006" +
                "060000280" +
                "000419005" +
                "000080079"

        val board = SudokuBoard.fromString(sample)
        val allCandidates = CandidateCalculator.calculateAllCandidates(board)

        for (index in 0 until CellPosition.TOTAL_CELLS) {
            val pos = CellPosition.fromIndex(index)
            val individual = CandidateCalculator.getCandidates(board, pos)
            assertEquals(individual, allCandidates[index])
        }
    }
}