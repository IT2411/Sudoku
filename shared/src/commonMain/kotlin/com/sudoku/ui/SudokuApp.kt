package com.sudoku.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sudoku.core.daily.DailyPuzzleEngine
import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.game.GameStatus
import com.sudoku.core.game.SudokuGame
import com.sudoku.core.generator.SudokuGenerator
import com.sudoku.core.storage.AchievementRepository
import com.sudoku.core.storage.GameRepository
import com.sudoku.core.storage.GameStateSnapshot
import com.sudoku.core.storage.SettingsRepository
import com.sudoku.core.storage.StatisticsRepository
import com.sudoku.ui.screens.AchievementsScreen
import com.sudoku.ui.screens.GameScreen
import com.sudoku.ui.screens.HomeScreen
import com.sudoku.ui.screens.SettingsScreen
import com.sudoku.ui.screens.StatisticsScreen

sealed interface AppScreen {
    data object Home : AppScreen
    data object Statistics : AppScreen
    data object Badges : AppScreen
    data object Settings : AppScreen
    data class Game(val activeGame: SudokuGame) : AppScreen
}

class NavigationController {
    var canGoBack: Boolean by mutableStateOf(false)
    var onGoBack: (() -> Unit)? = null
}

@Composable
fun SudokuApp(
    navController: NavigationController = remember { NavigationController() }
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

    // Synchronize navigation state with the system back button
    LaunchedEffect(currentScreen) {
        navController.canGoBack = currentScreen != AppScreen.Home
        navController.onGoBack = {
            if (currentScreen is AppScreen.Game) {
                val game = (currentScreen as AppScreen.Game).activeGame
                if (game.status == GameStatus.PLAYING || game.status == GameStatus.PAUSED) {
                    GameRepository.saveGame(GameStateSnapshot.fromGame(game))
                }
            }
            currentScreen = AppScreen.Home
        }
    }

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
                onOpenBadges = {
                    currentScreen = AppScreen.Badges
                },
                onOpenSettings = {
                    currentScreen = AppScreen.Settings
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
        is AppScreen.Badges -> {
            AchievementsScreen(
                unlocked = AchievementRepository.getUnlockedAchievements(),
                onBack = { currentScreen = AppScreen.Home }
            )
        }
        is AppScreen.Settings -> {
            SettingsScreen(
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
