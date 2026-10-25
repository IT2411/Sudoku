package com.sudoku.core.achievements

import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.game.SudokuGame
import com.sudoku.core.generator.SudokuGenerator
import com.sudoku.core.storage.AchievementRepository
import com.sudoku.core.storage.GameRepository
import com.sudoku.core.storage.StorageDriver
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class AchievementRepositoryTest {

    // In-memory mock storage driver
    private val memoryStorage = mutableMapOf<String, String>()

    @BeforeTest
    fun setup() {
        memoryStorage.clear()
        GameRepository.driver = object : StorageDriver {
            override fun save(key: String, value: String) { memoryStorage[key] = value }
            override fun load(key: String): String? = memoryStorage[key]
            override fun clear(key: String) { memoryStorage.remove(key) }
        }
    }

    @Test
    fun testEvaluateGameWonUnlocksAchievementsCorrectly() {
        val puzzle = SudokuGenerator.generate(targetClues = 28, seed = 999L)
        val game = SudokuGame(puzzle, difficulty = Difficulty.EXPERT)

        // Solve with 0 mistakes, 0 hints, in 250s (< 5 mins)
        val newAchievements = AchievementRepository.evaluateGameWon(game, elapsedSeconds = 250L)

        // Should unlock: FIRST_WIN, PERFECT_GAME, SPEED_DEMON, NO_HINTS, EXPERT_MASTER
        assertTrue(newAchievements.contains(Achievement.FIRST_WIN))
        assertTrue(newAchievements.contains(Achievement.PERFECT_GAME))
        assertTrue(newAchievements.contains(Achievement.SPEED_DEMON))
        assertTrue(newAchievements.contains(Achievement.NO_HINTS))
        assertTrue(newAchievements.contains(Achievement.EXPERT_MASTER))

        // Second evaluation must NOT duplicate already unlocked badges
        val secondEvaluation = AchievementRepository.evaluateGameWon(game, elapsedSeconds = 250L)
        assertTrue(secondEvaluation.isEmpty())
    }
}
