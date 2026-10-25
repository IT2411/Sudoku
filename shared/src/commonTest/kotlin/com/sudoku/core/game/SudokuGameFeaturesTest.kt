package com.sudoku.core.game

import com.sudoku.core.generator.SudokuGenerator
import com.sudoku.core.model.CellPosition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SudokuGameFeaturesTest {

    @Test
    fun testCompletedDigitsDisablesNumberAfter9CorrectPlacements() {
        val puzzle = SudokuGenerator.generate(targetClues = 30, seed = 777L)
        val game = SudokuGame(puzzle)

        val targetDigit = 1
        assertFalse(game.completedDigits.contains(targetDigit))

        // Fill all instances of digit 1 with their correct solution
        for (i in 0 until CellPosition.TOTAL_CELLS) {
            val pos = CellPosition.fromIndex(i)
            if (game.puzzle.solution[pos] == targetDigit && !game.puzzle.givenPositions.contains(pos)) {
                game.selectCell(pos)
                game.enterDigit(targetDigit)
            }
        }

        // Exactly 9 instances placed correctly -> must be marked completed
        assertTrue(game.completedDigits.contains(targetDigit))

        // Attempting to place an exhausted digit in any other cell must be rejected
        val emptyPos = CellPosition.allPositions.first { game.board[it] == 0 }
        game.selectCell(emptyPos)
        val entered = game.enterDigit(targetDigit)
        assertFalse(entered, "Placing an exhausted completed digit must be blocked")
    }

    @Test
    fun testUnitCompletionWaveDetection() {
        val puzzle = SudokuGenerator.generate(targetClues = 30, seed = 888L)
        val game = SudokuGame(puzzle)

        // Find an empty cell in Row 0
        val targetCol = (0..8).first { c -> !game.puzzle.givenPositions.contains(CellPosition.of(0, c)) }
        val targetPos = CellPosition.of(0, targetCol)

        // Fill all OTHER empty cells in Row 0 first
        for (c in 0..8) {
            val p = CellPosition.of(0, c)
            if (p != targetPos && !game.puzzle.givenPositions.contains(p)) {
                game.selectCell(p)
                game.enterDigit(game.puzzle.solution[p])
            }
        }

        // Before placing the last cell, row is incomplete
        val beforeWave = game.checkNewlyCompletedUnits(targetPos)
        assertTrue(beforeWave.isEmpty())

        // Place the 9th number completing Row 0
        game.selectCell(targetPos)
        game.enterDigit(game.puzzle.solution[targetPos])

        val afterWave = game.checkNewlyCompletedUnits(targetPos)
        // Must contain all 9 cells of Row 0
        for (c in 0..8) {
            assertTrue(afterWave.contains(CellPosition.of(0, c)), "Row cell (0, $c) must be flagged for wave animation")
        }
    }
}
