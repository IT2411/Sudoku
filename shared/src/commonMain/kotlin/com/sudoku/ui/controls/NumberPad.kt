package com.sudoku.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudoku.ui.theme.SudokuColors

@Composable
fun NumberPad(
    isNotesMode: Boolean,
    onDigitClicked: (Int) -> Unit,
    onToggleNotes: () -> Unit,
    onErase: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Digits 1 to 9 (Two rows: 1..5 and 6..9)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (d in 1..5) {
                PadButton(
                    text = d.toString(),
                    onClick = { onDigitClicked(d) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (d in 6..9) {
                PadButton(
                    text = d.toString(),
                    onClick = { onDigitClicked(d) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Erase Button
            PadButton(
                text = "⌫",
                onClick = onErase,
                bgColor = Color(0xFFFFEBEE),
                textColor = SudokuColors.TextMistake,
                modifier = Modifier.weight(1f)
            )
        }

        // Action Toolbar: Notes Mode Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isNotesMode) Color(0xFF1976D2) else Color(0xFFE0E0E0))
                    .clickable(onClick = onToggleNotes)
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isNotesMode) "✏️ Notes: ON" else "✏️ Notes: OFF",
                    color = if (isNotesMode) Color.White else Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun PadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bgColor: Color = Color(0xFFE3F2FD),
    textColor: Color = SudokuColors.TextPlayer
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}