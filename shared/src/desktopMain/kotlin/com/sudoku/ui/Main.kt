package com.sudoku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sudoku.core.game.SudokuGame
import com.sudoku.core.generator.SudokuGenerator
import com.sudoku.ui.board.SudokuBoardView
import com.sudoku.ui.theme.SudokuColors

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sudoku"
    ) {
        val game = remember {
            val puzzle = SudokuGenerator.generate(targetClues = 34, seed = 42L)
            SudokuGame(puzzle)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SudokuColors.Background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            SudokuBoardView(
                game = game,
                onCellSelected = { pos -> game.selectCell(pos) },
                modifier = Modifier.widthIn(max = 520.dp)
            )
        }
    }
}