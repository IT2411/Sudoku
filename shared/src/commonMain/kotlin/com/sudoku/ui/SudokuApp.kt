package com.sudoku.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sudoku.core.daily.DailyPuzzleEngine
import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.game.SudokuGame
import com.sudoku.core.generator.SudokuGenerator
import com.sudoku.core.storage.GameRepository
import com.sudoku.core.storage.StatisticsRepository
import com.sudoku.ui.screens.GameScreen
import com.sudoku.ui.screens.HomeScreen
import com.sudoku.ui.screens.StatisticsScreen

sealed interface AppScreen {
    data object Home : AppScreen
    data object Statistics : AppScreen
    data class Game(val activeGame: SudokuGame) : AppScreen
}

@Composable
fun SudokuApp() {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

    when (val screen = currentScreen) {
        is AppScreen.Home -> {
            HomeScreen(
                savedSnapshot = GameRepository.loadGame(),
                onContinueGame = {
                    val snapshot = GameRepository.loadGame()
                    if (snapshot != null) {
                        currentScreen = AppScreen.Game(snapshot.toGame())
                    }
                },
                onStartDaily = {
                    val puzzle = DailyPuzzleEngine.generateDaily()
                    val dailyGame = SudokuGame(puzzle = puzzle, difficulty = Difficulty.MEDIUM)
                    StatisticsRepository.recordGameStarted(Difficulty.MEDIUM)
                    currentScreen = AppScreen.Game(dailyGame)
                },
                onOpenStats = {
                    currentScreen = AppScreen.Statistics
                },
                onStartNewGame = { diff ->
                    val newGame = SudokuGame(
                        puzzle = SudokuGenerator.generate(targetClues = diff.baseClueTarget),
                        difficulty = diff
                    )
                    StatisticsRepository.recordGameStarted(diff)
                    currentScreen = AppScreen.Game(newGame)
                }
            )
        }
        is AppScreen.Statistics -> {
            StatisticsScreen(
                stats = StatisticsRepository.loadStats(),
                onBack = { currentScreen = AppScreen.Home }
            )
        }
        is AppScreen.Game -> {
            GameScreen(
                initialGame = screen.activeGame,
                onBackToHome = { currentScreen = AppScreen.Home }
            )
        }
    }
}
