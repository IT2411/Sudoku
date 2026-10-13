package com.sudoku.ui.board

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.sudoku.core.game.SudokuGame
import com.sudoku.core.interaction.BoardSelectionEngine
import com.sudoku.core.model.CellPosition
import com.sudoku.ui.theme.SudokuColors

@Composable
fun SudokuBoardView(
    game: SudokuGame,
    onCellSelected: (CellPosition) -> Unit,
    modifier: Modifier = Modifier
) {
    val highlights = BoardSelectionEngine.computeHighlights(
        board = game.board,
        notes = { game.getNotes(it) },
        selectedCell = game.selectedCell
    )

    val activeDigit = game.selectedCell?.let { pos ->
        val v = game.board[pos]
        if (v != 0) v else null
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
    ) {
        // 1. Grid Cells
        Column(modifier = Modifier.fillMaxSize()) {
            for (r in 0 until CellPosition.GRID_SIZE) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    for (c in 0 until CellPosition.GRID_SIZE) {
                        val pos = CellPosition.of(r, c)
                        SudokuCellView(
                            value = game.board[pos],
                            notes = game.getNotes(pos),
                            isGiven = game.puzzle.givenPositions.contains(pos),
                            highlight = highlights[pos.index],
                            activeHighlightDigit = activeDigit,
                            onClick = { onCellSelected(pos) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 2. High-performance Canvas Divider Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellWidth = size.width / CellPosition.GRID_SIZE
            val cellHeight = size.height / CellPosition.GRID_SIZE

            for (i in 1 until CellPosition.GRID_SIZE) {
                val isThick = i % CellPosition.BOX_SIZE == 0
                val strokeWidth = if (isThick) 2.5.dp.toPx() else 1.dp.toPx()
                val color = if (isThick) SudokuColors.ThickBorder else SudokuColors.ThinBorder

                // Vertical grid lines
                drawLine(
                    color = color,
                    start = Offset(x = i * cellWidth, y = 0f),
                    end = Offset(x = i * cellWidth, y = size.height),
                    strokeWidth = strokeWidth
                )

                // Horizontal grid lines
                drawLine(
                    color = color,
                    start = Offset(x = 0f, y = i * cellHeight),
                    end = Offset(x = size.width, y = i * cellHeight),
                    strokeWidth = strokeWidth
                )
            }

            // Outer border
            drawRect(
                color = SudokuColors.ThickBorder,
                size = size,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
            )
        }
    }
}