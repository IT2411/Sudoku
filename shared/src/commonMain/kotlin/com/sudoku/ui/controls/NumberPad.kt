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
    completedDigits: Set<Int>,
    onDigitClicked: (Int) -> Unit,
    onToggleNotes: () -> Unit,
    onErase: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (d in 1..5) {
                val isCompleted = completedDigits.contains(d)
                PadButton(
                    text = d.toString(),
                    enabled = !isCompleted,
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
                val isCompleted = completedDigits.contains(d)
                PadButton(
                    text = d.toString(),
                    enabled = !isCompleted,
                    onClick = { onDigitClicked(d) },
                    modifier = Modifier.weight(1f)
                )
            }

            PadButton(
                text = "⌫",
                enabled = true,
                onClick = onErase,
                bgColor = Color(0xFF2A1E24),
                textColor = SudokuColors.TextMistake,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isNotesMode) Color(0xFF2563EB) else SudokuColors.PadButtonBg)
                    .clickable(onClick = onToggleNotes)
                    .padding(horizontal = 28.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isNotesMode) "✏️ Notes: ON" else "✏️ Notes: OFF",
                    color = if (isNotesMode) Color.White else Color(0xFF94A3B8),
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
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bgColor: Color = SudokuColors.PadButtonBg,
    textColor: Color = SudokuColors.PadButtonText
) {
    val finalBg = if (enabled) bgColor else SudokuColors.PadButtonDisabledBg
    val finalText = if (enabled) textColor else SudokuColors.PadButtonDisabledText

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(finalBg)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = finalText
        )
    }
}
