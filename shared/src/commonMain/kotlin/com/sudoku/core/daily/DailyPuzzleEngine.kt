package com.sudoku.core.daily

import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.generator.GeneratedPuzzle
import com.sudoku.core.generator.SudokuGenerator
import com.sudoku.core.storage.GameRepository

object DailyPuzzleEngine {

    private const val KEY_DAILY_COMPLETED_DAY = "daily_completed_epoch_day"

    fun getTodayEpochDay(): Long {
        return System.currentTimeMillis() / 86400000L
    }

    fun isTodayCompleted(): Boolean {
        val driver = GameRepository.driver ?: return false
        val saved = driver.load(KEY_DAILY_COMPLETED_DAY)?.toLongOrNull() ?: return false
        return saved == getTodayEpochDay()
    }

    fun markTodayCompleted() {
        val driver = GameRepository.driver ?: return
        driver.save(KEY_DAILY_COMPLETED_DAY, getTodayEpochDay().toString())
    }

    fun createDateSeed(year: Int, month: Int, day: Int): Long {
        return (year * 10000L) + (month * 100L) + day.toLong()
    }

    fun generateDaily(): GeneratedPuzzle {
        val days = getTodayEpochDay().toInt()
        val seed = createDateSeed(year = 2026, month = 3, day = (days % 30) + 1)
        return SudokuGenerator.generate(
            targetClues = Difficulty.MEDIUM.baseClueTarget,
            seed = seed,
            symmetric = true
        )
    }
}
