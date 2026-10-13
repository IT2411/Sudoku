package com.sudoku.core.daily

import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.generator.GeneratedPuzzle
import com.sudoku.core.generator.SudokuGenerator

object DailyPuzzleEngine {

    fun createDateSeed(year: Int, month: Int, day: Int): Long {
        return (year * 10000L) + (month * 100L) + day.toLong()
    }

    /**
     * Generates today's official Daily Sudoku.
     */
    fun generateDaily(year: Int = 2026, month: Int = 3, day: Int = 30): GeneratedPuzzle {
        val seed = createDateSeed(year, month, day)
        return SudokuGenerator.generate(
            targetClues = Difficulty.MEDIUM.baseClueTarget,
            seed = seed,
            symmetric = true
        )
    }
}
