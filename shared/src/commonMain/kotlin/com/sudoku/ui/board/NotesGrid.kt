package com.sudoku.ui.board

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sudoku.core.model.Candidates
import com.sudoku.ui.theme.SudokuColors

/**
 * 3x3 sub-grid displaying pencil marks 1 through 9.
 */
@Composable
fun NotesGrid(
    candidates: Candidates,
    highlightDigit: Int?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        for (row in 0..2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                for (col in 0..2) {
                    val digit = row * 3 + col + 1
                    val hasDigit = candidates.contains(digit)
                    val isHighlighted = hasDigit && digit == highlightDigit

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasDigit) {
                            Text(
                                text = digit.toString(),
                                fontSize = 10.sp,
                                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                                color = if (isHighlighted) SudokuColors.NoteHighlighted else SudokuColors.TextNote
                            )
                        }
                    }
                }
            }
        }
    }
}