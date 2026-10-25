package com.sudoku.core.daily

import com.sudoku.core.validator.SudokuValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DailyPuzzleEngineTest {

    @Test
    fun testDateSeedIsDeterministic() {
        val seed1 = DailyPuzzleEngine.createDateSeed(2026, 3, 30)
        val seed2 = DailyPuzzleEngine.createDateSeed(2026, 3, 30)
        assertEquals(20260330L, seed1)
        assertEquals(seed1, seed2)
    }

    @Test
    fun testDailyPuzzleGeneratesValidAndConsistentPuzzle() {
        val dailyA = DailyPuzzleEngine.generateDaily()
        val dailyB = DailyPuzzleEngine.generateDaily()

        // Puzzles on the same day must be identical bit-for-bit
        assertEquals(dailyA.initialBoard, dailyB.initialBoard)
        assertEquals(dailyA.solution, dailyB.solution)
        assertTrue(SudokuValidator.isBoardSolved(dailyA.solution))
    }
}
