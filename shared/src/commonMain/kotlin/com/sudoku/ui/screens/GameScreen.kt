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
import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.difficulty.TechniqueStep
import com.sudoku.core.game.GameStatus
import com.sudoku.core.game.SudokuGame
import com.sudoku.core.generator.SudokuGenerator
import com.sudoku.core.model.CellPosition
import com.sudoku.ui.board.SudokuBoardView
import com.sudoku.ui.controls.HintCard
import com.sudoku.ui.controls.NumberPad
import com.sudoku.ui.theme.SudokuColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }
    var game by remember {
        mutableStateOf(
            SudokuGame(
                puzzle = SudokuGenerator.generate(targetClues = selectedDifficulty.baseClueTarget),
                difficulty = selectedDifficulty
            )
        )
    }

    var boardVersion by remember { mutableIntStateOf(0) }
    var notesMode by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableLongStateOf(0L) }
    var activeHint by remember { mutableStateOf<TechniqueStep?>(null) }
    var difficultyMenuOpen by remember { mutableStateOf(false) }
    var animatingCells by remember { mutableStateOf<Set<CellPosition>>(emptySet()) }

    fun triggerCompletionAnimation(cells: Set<CellPosition>) {
        if (cells.isNotEmpty()) {
            coroutineScope.launch {
                animatingCells = cells
                // Allow the 55ms-staggered wave to reach column 8 and smoothly fade
                delay(1200L)
                animatingCells = emptySet()
            }
        }
    }

    fun startNewGame(diff: Difficulty) {
        selectedDifficulty = diff
        game = SudokuGame(
            puzzle = SudokuGenerator.generate(targetClues = diff.baseClueTarget),
            difficulty = diff
        )
        timerSeconds = 0L
        activeHint = null
        notesMode = false
        animatingCells = emptySet()
        boardVersion++
    }

    LaunchedEffect(game.status) {
        while (game.status == GameStatus.PLAYING) {
            delay(1000L)
            timerSeconds++
            game.elapsedTimeSeconds = timerSeconds
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
                Box {
                    Text(
                        text = "${selectedDifficulty.displayName} ▼",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA5B4FC),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2235))
                            .clickable { difficultyMenuOpen = true }
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
                                    difficultyMenuOpen = false
                                    startNewGame(diff)
                                }
                            )
                        }
                    }
                }

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
                            if (game.status == GameStatus.PLAYING) game.pause() else game.resume()
                            boardVersion++
                        },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(if (game.status == GameStatus.PAUSED) "▶" else "⏸", color = Color(0xFF94A3B8))
                    }
                }

                Text(
                    text = "Mistakes: ${game.mistakes}/3",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (game.mistakes > 0) SudokuColors.TextMistake else SudokuColors.TextNote
                )
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
                                    game.resume()
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
                        val pos = hint.placedDigit?.first
                        game.applyHint(hint)
                        if (pos != null) {
                            triggerCompletionAnimation(game.checkNewlyCompletedUnits(pos))
                        }
                        activeHint = null
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
                    onClick = { if (game.undo()) boardVersion++ },
                    enabled = game.undoManager.canUndo && game.status == GameStatus.PLAYING,
                    colors = ButtonDefaults.buttonColors(containerColor = SudokuColors.PadButtonBg, contentColor = Color(0xFFCBD5E1))
                ) {
                    Text("↩ Undo")
                }

                Button(
                    onClick = { if (game.redo()) boardVersion++ },
                    enabled = game.undoManager.canRedo && game.status == GameStatus.PLAYING,
                    colors = ButtonDefaults.buttonColors(containerColor = SudokuColors.PadButtonBg, contentColor = Color(0xFFCBD5E1))
                ) {
                    Text("↪ Redo")
                }

                Button(
                    onClick = {
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
                    if (game.enterDigit(digit)) {
                        activeHint = null
                        if (!notesMode && targetPos != null) {
                            triggerCompletionAnimation(game.checkNewlyCompletedUnits(targetPos))
                        }
                        boardVersion++
                    }
                },
                onToggleNotes = {
                    notesMode = !notesMode
                    game.isNotesModeActive = notesMode
                },
                onErase = {
                    if (game.erase()) {
                        activeHint = null
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
                onNewGame = { diff -> startNewGame(diff) }
            )
        } else if (game.status == GameStatus.GAME_OVER) {
            GameOverDialog(
                onRetry = { startNewGame(selectedDifficulty) }
            )
        }
    }
}
