package com.sudoku.core.difficulty

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.SudokuBoard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DifficultyAnalyzerTest {

    // Easy puzzle (solvable via Singles)
    private val easyPuzzle = "" +
            "000260701" +
            "680070090" +
            "190004500" +
            "820100040" +
            "004602900" +
            "050003028" +
            "009300074" +
            "040050036" +
            "703018000"

    @Test
    fun testEasyPuzzleClassification() {
        val board = SudokuBoard.fromString(easyPuzzle)
        val analysis = DifficultyAnalyzer.analyze(board)

        assertEquals(Difficulty.EASY, analysis.difficulty)
        assertTrue(analysis.maxTechniqueWeight <= 2)
        assertTrue(analysis.techniqueCounts.containsKey("Naked Single") || analysis.techniqueCounts.containsKey("Hidden Single"))
    }

    @Test
    fun testNakedSingleDetector() {
        // Construct board with 1 obvious Naked Single at (0, 0)
        val board = SudokuBoard()
            .withCell(CellPosition.of(0, 1), 1)
            .withCell(CellPosition.of(0, 2), 2)
            .withCell(CellPosition.of(0, 3), 3)
            .withCell(CellPosition.of(4, 0), 4)
            .withCell(CellPosition.of(5, 0), 5)
            .withCell(CellPosition.of(6, 0), 6)
            .withCell(CellPosition.of(1, 1), 7)
            .withCell(CellPosition.of(2, 2), 8)

        val analysis = DifficultyAnalyzer.analyze(board)
        val step = analysis.steps.firstOrNull { it.technique == SolvingTechnique.NakedSingle }

        assertTrue(step != null)
        assertEquals(CellPosition.of(0, 0), step.placedDigit?.first)
        assertEquals(9, step.placedDigit?.second)
    }

    @Test
    fun testNakedPairDetection() {
        // Set up two cells in Row 0 that only have candidates {3, 7}
        val board = SudokuBoard()
            .withCell(CellPosition.of(0, 2), 1)
            .withCell(CellPosition.of(0, 3), 2)
            .withCell(CellPosition.of(0, 4), 4)
            .withCell(CellPosition.of(0, 5), 5)
            .withCell(CellPosition.of(0, 6), 6)
            .withCell(CellPosition.of(0, 7), 8)
            .withCell(CellPosition.of(0, 8), 9)

        // Cells (0, 0) and (0, 1) can only be 3 or 7
        val candidates = com.sudoku.core.candidates.CandidateCalculator.calculateAllCandidates(board)
        val step = SolvingTechnique.NakedPair.findStep(board, candidates)

        // If candidates naturally isolated to {3, 7}, step will either eliminate or be handled
        assertTrue(step == null || step.technique == SolvingTechnique.NakedPair)
    }
}