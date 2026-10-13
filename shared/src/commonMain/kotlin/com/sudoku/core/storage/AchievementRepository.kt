package com.sudoku.core.storage

import com.sudoku.core.achievements.Achievement
import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.game.SudokuGame

object AchievementRepository {
    private const val KEY_ACHIEVEMENTS = "sudoku_achievements"

    fun getUnlockedAchievements(): Set<Achievement> {
        val driver = GameRepository.driver ?: return emptySet()
        val raw = driver.load(KEY_ACHIEVEMENTS) ?: return emptySet()
        return raw.split(",")
            .mapNotNull { id -> Achievement.entries.find { it.id == id } }
            .toSet()
    }

    private fun markUnlocked(achievement: Achievement) {
        val current = getUnlockedAchievements()
        if (!current.contains(achievement)) {
            val updated = current + achievement
            val raw = updated.joinToString(",") { it.id }
            GameRepository.driver?.save(KEY_ACHIEVEMENTS, raw)
        }
    }

    /**
     * Evaluates game completion against achievement rules.
     * Returns the list of newly unlocked achievements.
     */
    fun evaluateGameWon(game: SudokuGame, elapsedSeconds: Long): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()
        val alreadyUnlocked = getUnlockedAchievements()

        fun checkAndUnlock(ach: Achievement, condition: Boolean) {
            if (condition && !alreadyUnlocked.contains(ach)) {
                markUnlocked(ach)
                newlyUnlocked.add(ach)
            }
        }

        // 1. First Win
        checkAndUnlock(Achievement.FIRST_WIN, true)

        // 2. Perfect Game (0 mistakes)
        checkAndUnlock(Achievement.PERFECT_GAME, game.mistakes == 0)

        // 3. Speed Demon (< 5 minutes)
        checkAndUnlock(Achievement.SPEED_DEMON, elapsedSeconds < 300L)

        // 4. No Hints
        checkAndUnlock(Achievement.NO_HINTS, game.hintsUsed == 0 && game.difficulty != Difficulty.EASY)

        // 5. Grandmaster
        checkAndUnlock(Achievement.EXPERT_MASTER, game.difficulty == Difficulty.EXPERT)

        // 6. Ten Wins
        val totalWon = StatisticsRepository.loadStats().totalGamesWon
        checkAndUnlock(Achievement.TEN_WINS, totalWon >= 10)

        // 7. Streak 7
        val streak = StatisticsRepository.loadStats().currentStreak
        checkAndUnlock(Achievement.STREAK_7, streak >= 7)

        return newlyUnlocked
    }
}
