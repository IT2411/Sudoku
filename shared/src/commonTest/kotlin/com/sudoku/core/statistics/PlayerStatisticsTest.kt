package com.sudoku.core.statistics

import com.sudoku.core.difficulty.Difficulty
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayerStatisticsTest {

    @Test
    fun testGameWonDoesNotDoubleCountGamesPlayed() {
        var stats = PlayerStatistics()

        // 1. Start game
        stats = stats.withGameStarted(Difficulty.EASY)
        assertEquals(1, stats.totalGamesPlayed)
        assertEquals(0, stats.totalGamesWon)
        assertEquals(0, stats.winRatePercentage)

        // 2. Win game in 240 seconds
        stats = stats.withGameWon(Difficulty.EASY, 240L)

        // Total games played must REMAIN 1, not 2!
        assertEquals(1, stats.totalGamesPlayed)
        assertEquals(1, stats.totalGamesWon)
        assertEquals(100, stats.winRatePercentage)
        assertEquals(1, stats.currentStreak)
        assertEquals(1, stats.bestStreak)
        assertEquals(240L, stats.statsByDifficulty[Difficulty.EASY]?.bestTimeSeconds)
    }

    @Test
    fun testStreakIncrementsAndBreaksOnGameOver() {
        var stats = PlayerStatistics()

        stats = stats.withGameStarted(Difficulty.MEDIUM).withGameWon(Difficulty.MEDIUM, 300L)
        stats = stats.withGameStarted(Difficulty.MEDIUM).withGameWon(Difficulty.MEDIUM, 280L)
        stats = stats.withGameStarted(Difficulty.MEDIUM).withGameWon(Difficulty.MEDIUM, 260L)

        assertEquals(3, stats.currentStreak)
        assertEquals(3, stats.bestStreak)

        // Game Over resets current streak to 0, best streak preserved
        stats = stats.withGameOver()
        assertEquals(0, stats.currentStreak)
        assertEquals(3, stats.bestStreak)
    }
}
