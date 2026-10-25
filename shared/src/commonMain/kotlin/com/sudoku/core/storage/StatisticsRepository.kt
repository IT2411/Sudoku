package com.sudoku.core.storage

import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.statistics.DifficultyStats
import com.sudoku.core.statistics.PlayerStatistics
import kotlin.math.max

object StatisticsRepository {
    private const val KEY_STATS = "sudoku_player_stats"

    fun loadStats(): PlayerStatistics {
        val driver = GameRepository.driver ?: return PlayerStatistics()
        val raw = driver.load(KEY_STATS) ?: return PlayerStatistics()

        return try {
            val parts = raw.split(";")
            var currentStreak = parts.getOrNull(0)?.toIntOrNull() ?: 0
            var bestStreak = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val lastEpoch = parts.getOrNull(2)?.toLongOrNull() ?: 0L

            val diffMap = mutableMapOf<Difficulty, DifficultyStats>()
            Difficulty.entries.forEachIndexed { index, difficulty ->
                val rawDiff = parts.getOrNull(3 + index)
                diffMap[difficulty] = if (!rawDiff.isNullOrBlank()) {
                    var s = DifficultyStats.deserialize(rawDiff)
                    // Auto-heal previously double-counted games
                    if (s.gamesWon > 0 && s.gamesPlayed == s.gamesWon * 2) {
                        s = s.copy(gamesPlayed = s.gamesWon)
                    }
                    s
                } else {
                    DifficultyStats()
                }
            }

            val totalWon = diffMap.values.sumOf { it.gamesWon }
            if (totalWon > 0 && currentStreak == 0) {
                currentStreak = totalWon
                bestStreak = max(bestStreak, currentStreak)
            }

            PlayerStatistics(
                currentStreak = currentStreak,
                bestStreak = bestStreak,
                lastCompletedDateEpochDay = lastEpoch,
                statsByDifficulty = diffMap
            )
        } catch (e: Exception) {
            PlayerStatistics()
        }
    }

    fun saveStats(stats: PlayerStatistics) {
        val driver = GameRepository.driver ?: return
        val serialized = buildString {
            append("${stats.currentStreak};${stats.bestStreak};${stats.lastCompletedDateEpochDay};")
            Difficulty.entries.forEachIndexed { i, d ->
                val s = stats.statsByDifficulty[d] ?: DifficultyStats()
                append(s.serialize())
                if (i < Difficulty.entries.lastIndex) append(";")
            }
        }
        driver.save(KEY_STATS, serialized)
    }

    fun recordGameWon(difficulty: Difficulty, timeSeconds: Long) {
        val updated = loadStats().withGameWon(difficulty, timeSeconds)
        saveStats(updated)
    }

    fun recordGameStarted(difficulty: Difficulty) {
        val updated = loadStats().withGameStarted(difficulty)
        saveStats(updated)
    }

    fun recordGameOver() {
        val updated = loadStats().withGameOver()
        saveStats(updated)
    }
}
