package com.sudoku.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudoku.core.game.GameStatus
import com.sudoku.core.game.SudokuGame
import com.sudoku.core.generator.SudokuGenerator
import com.sudoku.ui.board.SudokuBoardView
import com.sudoku.ui.controls.NumberPad
import com.sudoku.ui.theme.SudokuColors

@Composable
fun GameScreen(
    modifier: Modifier = Modifier
) {
    val game = remember {
        val puzzle = SudokuGenerator.generate(targetClues = 34, seed = 12345L)
        SudokuGame(puzzle)
    }

    // Trigger state re-composition on moves
    var boardVersion by remember { mutableIntStateOf(0) }
    var notesMode by remember { mutableStateOf(false) }

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
            // Header: Mistakes & Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Classic Sudoku",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SudokuColors.TextGiven
                )
                Text(
                    text = "Mistakes: ${game.mistakes}/3",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (game.mistakes > 0) SudokuColors.TextMistake else SudokuColors.TextNote
                )
            }

            // Board View
            key(boardVersion) {
                SudokuBoardView(
                    game = game,
                    onCellSelected = { pos ->
                        game.selectCell(pos)
                        boardVersion++
                    }
                )
            }

            // Toolbar: Undo / Redo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (game.undo()) boardVersion++
                    },
                    enabled = game.undoManager.canUndo,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1), contentColor = Color.Black)
                ) {
                    Text("↩ Undo")
                }

                Button(
                    onClick = {
                        if (game.redo()) boardVersion++
                    },
                    enabled = game.undoManager.canRedo,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1), contentColor = Color.Black)
                ) {
                    Text("↪ Redo")
                }
            }

            // NumberPad & Notes Toggle
            NumberPad(
                isNotesMode = notesMode,
                onDigitClicked = { digit ->
                    game.isNotesModeActive = notesMode
                    if (game.enterDigit(digit)) {
                        boardVersion++
                    }
                },
                onToggleNotes = {
                    notesMode = !notesMode
                    game.isNotesModeActive = notesMode
                },
                onErase = {
                    if (game.erase()) {
                        boardVersion++
                    }
                }
            )

            if (game.status == GameStatus.COMPLETED) {
                Text(
                    text = "🎉 PUZZLE COMPLETED! 🎉",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            } else if (game.status == GameStatus.GAME_OVER) {
                Text(
                    text = "GAME OVER",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SudokuColors.TextMistake
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private inline fun key(key: Any?, content: @Composable () -> Unit) {
    androidx.compose.runtime.key(key) {
        content()
    }
}