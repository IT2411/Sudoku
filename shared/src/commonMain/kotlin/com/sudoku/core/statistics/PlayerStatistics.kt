package com.sudoku.core.statistics

import com.sudoku.core.difficulty.Difficulty

data class DifficultyStats(
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val bestTimeSeconds: Long = 0L,
    val totalTimeSeconds: Long = 0L
) {
    val averageTimeSeconds: Long
        get() = if (gamesWon > 0) totalTimeSeconds / gamesWon else 0L

    fun serialize(): String = "$gamesPlayed,$gamesWon,$bestTimeSeconds,$totalTimeSeconds"

    companion object {
        fun deserialize(raw: String): DifficultyStats {
            val parts = raw.split(",")
            return DifficultyStats(
                gamesPlayed = parts.getOrNull(0)?.toIntOrNull() ?: 0,
                gamesWon = parts.getOrNull(1)?.toIntOrNull() ?: 0,
                bestTimeSeconds = parts.getOrNull(2)?.toLongOrNull() ?: 0L,
                totalTimeSeconds = parts.getOrNull(3)?.toLongOrNull() ?: 0L
            )
        }
    }
}

data class PlayerStatistics(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastCompletedDateEpochDay: Long = 0L,
    val statsByDifficulty: Map<Difficulty, DifficultyStats> = Difficulty.entries.associateWith { DifficultyStats() }
) {
    val totalGamesPlayed: Int get() = statsByDifficulty.values.sumOf { it.gamesPlayed }
    val totalGamesWon: Int get() = statsByDifficulty.values.sumOf { it.gamesWon }
    val winRatePercentage: Int
        get() = if (totalGamesPlayed > 0) (totalGamesWon * 100) / totalGamesPlayed else 0

    fun withGameWon(difficulty: Difficulty, timeSeconds: Long): PlayerStatistics {
        val current = statsByDifficulty[difficulty] ?: DifficultyStats()
        val newBest = if (current.bestTimeSeconds == 0L || timeSeconds < current.bestTimeSeconds) {
            timeSeconds
        } else {
            current.bestTimeSeconds
        }

        val updated = current.copy(
            gamesPlayed = current.gamesPlayed + 1,
            gamesWon = current.gamesWon + 1,
            bestTimeSeconds = newBest,
            totalTimeSeconds = current.totalTimeSeconds + timeSeconds
        )

        return copy(
            statsByDifficulty = statsByDifficulty + (difficulty to updated)
        )
    }

    fun withGameStarted(difficulty: Difficulty): PlayerStatistics {
        val current = statsByDifficulty[difficulty] ?: DifficultyStats()
        return copy(
            statsByDifficulty = statsByDifficulty + (difficulty to current.copy(gamesPlayed = current.gamesPlayed + 1))
        )
    }
}
