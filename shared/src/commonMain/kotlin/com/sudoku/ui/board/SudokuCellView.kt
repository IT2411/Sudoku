package com.sudoku.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sudoku.core.interaction.CellHighlightState
import com.sudoku.core.model.Candidates
import com.sudoku.core.model.SudokuBoard
import com.sudoku.ui.theme.SudokuColors

@Composable
fun SudokuCellView(
    value: Int,
    notes: Candidates,
    isGiven: Boolean,
    highlight: CellHighlightState,
    activeHighlightDigit: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine background color based on interaction priority
    val bgColor = when {
        highlight.hasConflict -> SudokuColors.CellConflict
        highlight.isSelected -> SudokuColors.CellSelected
        highlight.isSameDigit -> SudokuColors.CellSameDigit
        highlight.isPeer -> SudokuColors.CellPeer
        else -> SudokuColors.Surface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (value != SudokuBoard.EMPTY_VALUE) {
            val textColor = when {
                highlight.hasConflict -> SudokuColors.TextMistake
                isGiven -> SudokuColors.TextGiven
                else -> SudokuColors.TextPlayer
            }

            Text(
                text = value.toString(),
                fontSize = 24.sp,
                fontWeight = if (isGiven) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        } else if (notes.isNotEmpty) {
            NotesGrid(
                candidates = notes,
                highlightDigit = activeHighlightDigit
            )
        }
    }
}