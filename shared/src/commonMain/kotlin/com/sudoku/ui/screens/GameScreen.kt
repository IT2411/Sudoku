package com.sudoku.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudoku.core.achievements.Achievement
import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.difficulty.TechniqueStep
import com.sudoku.core.game.GameStatus
import com.sudoku.core.game.SudokuGame
import com.sudoku.core.generator.SudokuGenerator
import com.sudoku.core.haptics.Haptics
import com.sudoku.core.model.CellPosition
import com.sudoku.core.storage.AchievementRepository
import com.sudoku.core.storage.GameRepository
import com.sudoku.core.storage.GameStateSnapshot
import com.sudoku.core.storage.SettingsRepository
import com.sudoku.core.storage.StatisticsRepository
import com.sudoku.ui.board.SudokuBoardView
import com.sudoku.ui.controls.HintCard
import com.sudoku.ui.controls.NumberPad
import com.sudoku.ui.theme.SudokuColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    initialGame: SudokuGame? = null,
    onBackToHome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val settings = remember { SettingsRepository.loadSettings() }
    val maxMistakesAllowed = if (settings.mistakeLimitEnabled) 3 else -1

    var selectedDifficulty by remember { mutableStateOf(initialGame?.difficulty ?: Difficulty.EASY) }
    var game by remember {
        mutableStateOf(
            initialGame ?: SudokuGame(
                puzzle = SudokuGenerator.generate(targetClues = selectedDifficulty.baseClueTarget),
                difficulty = selectedDifficulty,
                maxMistakes = maxMistakesAllowed
            )
        )
    }

    var boardVersion by remember { mutableIntStateOf(0) }
    var notesMode by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableLongStateOf(game.elapsedTimeSeconds) }
    var activeHint by remember { mutableStateOf<TechniqueStep?>(null) }
    var difficultyMenuOpen by remember { mutableStateOf(false) }
    var animatingCells by remember { mutableStateOf<Set<CellPosition>>(emptySet()) }
    var newlyUnlockedAchievements by remember { mutableStateOf<List<Achievement>>(emptyList()) }

    fun autoSave() {
        GameRepository.saveGame(GameStateSnapshot.fromGame(game))
    }

    fun triggerCompletionAnimation(cells: Set<CellPosition>) {
        if (cells.isNotEmpty()) {
            Haptics.success()
            coroutineScope.launch {
                animatingCells = cells
                delay(1200L)
                animatingCells = emptySet()
            }
        }
    }

    fun startNewGame(diff: Difficulty) {
        selectedDifficulty = diff
        game = SudokuGame(
            puzzle = SudokuGenerator.generate(targetClues = diff.baseClueTarget),
            difficulty = diff,
            maxMistakes = maxMistakesAllowed
        )
        StatisticsRepository.recordGameStarted(diff)
        timerSeconds = 0L
        activeHint = null
        notesMode = false
        animatingCells = emptySet()
        newlyUnlockedAchievements = emptyList()
        autoSave()
        boardVersion++
    }

    LaunchedEffect(game.status) {
        while (game.status == GameStatus.PLAYING) {
            delay(1000L)
            timerSeconds++
            game.elapsedTimeSeconds = timerSeconds
            if (timerSeconds % 10 == 0L) autoSave()
        }
    }

    LaunchedEffect(game.status) {
        if (game.status == GameStatus.COMPLETED) {
            Haptics.success()
            StatisticsRepository.recordGameWon(game.difficulty, timerSeconds)
            // Evaluate achievements
            newlyUnlockedAchievements = AchievementRepository.evaluateGameWon(game, timerSeconds)
            GameRepository.clearGame()
        } else if (game.status == GameStatus.GAME_OVER) {
            Haptics.error()
            GameRepository.clearGame()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SudokuColors.Background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏠",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            Haptics.click()
                            autoSave()
                            onBackToHome()
                        }
                        .padding(6.dp)
                )

                Box {
                    Text(
                        text = "${selectedDifficulty.displayName} ▼",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA5B4FC),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2235))
                            .clickable {
                                Haptics.click()
                                difficultyMenuOpen = true
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                    DropdownMenu(
                        expanded = difficultyMenuOpen,
                        onDismissRequest = { difficultyMenuOpen = false }
                    ) {
                        Difficulty.entries.forEach { diff ->
                            DropdownMenuItem(
                                text = { Text(diff.displayName) },
                                onClick = {
                                    Haptics.click()
                                    difficultyMenuOpen = false
                                    startNewGame(diff)
                                }
                            )
                        }
                    }
                }

                if (settings.timerVisible) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = formatSeconds(timerSeconds),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SudokuColors.TextPlayer
                        )
                        OutlinedButton(
                            onClick = {
                                Haptics.click()
                                if (game.status == GameStatus.PLAYING) game.pause() else game.resume()
                                autoSave()
                                boardVersion++
                            },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(if (game.status == GameStatus.PAUSED) "▶" else "⏸", color = Color(0xFF94A3B8))
                        }
                    }
                }

                if (settings.mistakeLimitEnabled) {
                    Text(
                        text = "${game.mistakes}/3 ❌",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (game.mistakes > 0) SudokuColors.TextMistake else SudokuColors.TextNote
                    )
                } else {
                    Text(
                        text = "Practice",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF10B981)
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.runtime.key(boardVersion) {
                    SudokuBoardView(
                        game = game,
                        activeHint = activeHint,
                        animatingCells = animatingCells,
                        onCellSelected = { pos ->
                            if (game.status == GameStatus.PLAYING) {
                                Haptics.click()
                                game.selectCell(pos)
                                boardVersion++
                            }
                        }
                    )
                }

                if (game.status == GameStatus.PAUSED) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xE60F1117)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Game Paused",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Button(
                                onClick = {
                                    Haptics.click()
                                    game.resume()
                                    autoSave()
                                    boardVersion++
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Text("▶ Resume", color = Color.White)
                            }
                        }
                    }
                }
            }

            activeHint?.let { hint ->
                HintCard(
                    hint = hint,
                    onApply = {
                        Haptics.click()
                        val pos = hint.placedDigit?.first
                        game.applyHint(hint)
                        if (pos != null) {
                            triggerCompletionAnimation(game.checkNewlyCompletedUnits(pos))
                        }
                        activeHint = null
                        autoSave()
                        boardVersion++
                    },
                    onDismiss = { activeHint = null }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        Haptics.click()
                        if (game.undo()) {
                            autoSave()
                            boardVersion++
                        }
                    },
                    enabled = game.undoManager.canUndo && game.status == GameStatus.PLAYING,
                    colors = ButtonDefaults.buttonColors(containerColor = SudokuColors.PadButtonBg, contentColor = Color(0xFFCBD5E1))
                ) {
                    Text("↩ Undo")
                }

                Button(
                    onClick = {
                        Haptics.click()
                        if (game.redo()) {
                            autoSave()
                            boardVersion++
                        }
                    },
                    enabled = game.undoManager.canRedo && game.status == GameStatus.PLAYING,
                    colors = ButtonDefaults.buttonColors(containerColor = SudokuColors.PadButtonBg, contentColor = Color(0xFFCBD5E1))
                ) {
                    Text("↪ Redo")
                }

                Button(
                    onClick = {
                        Haptics.click()
                        activeHint = game.findNextHint()
                        boardVersion++
                    },
                    enabled = game.status == GameStatus.PLAYING,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF382F19), contentColor = Color(0xFFFBBF24))
                ) {
                    Text("💡 Hint")
                }
            }

            NumberPad(
                isNotesMode = notesMode,
                completedDigits = game.completedDigits,
                onDigitClicked = { digit ->
                    game.isNotesModeActive = notesMode
                    val targetPos = game.selectedCell
                    val prevMistakes = game.mistakes
                    if (game.enterDigit(digit)) {
                        activeHint = null
                        if (game.mistakes > prevMistakes) {
                            Haptics.error()
                        } else {
                            Haptics.click()
                        }
                        if (!notesMode && targetPos != null) {
                            triggerCompletionAnimation(game.checkNewlyCompletedUnits(targetPos))
                        }
                        autoSave()
                        boardVersion++
                    }
                },
                onToggleNotes = {
                    Haptics.click()
                    notesMode = !notesMode
                    game.isNotesModeActive = notesMode
                },
                onErase = {
                    Haptics.click()
                    if (game.erase()) {
                        activeHint = null
                        autoSave()
                        boardVersion++
                    }
                }
            )

            Spacer(modifier = Modifier.height(4.dp))
        }

        if (game.status == GameStatus.COMPLETED) {
            CompletionDialog(
                elapsedSeconds = timerSeconds,
                mistakes = game.mistakes,
                hintsUsed = game.hintsUsed,
                difficulty = selectedDifficulty,
                newAchievements = newlyUnlockedAchievements,
                onNewGame = { diff -> startNewGame(diff) }
            )
        } else if (game.status == GameStatus.GAME_OVER) {
            GameOverDialog(
                onRetry = { startNewGame(selectedDifficulty) }
            )
        }
    }
}
