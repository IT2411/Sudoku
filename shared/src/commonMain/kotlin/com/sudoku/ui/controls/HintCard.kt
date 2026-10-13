package com.sudoku.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudoku.core.difficulty.TechniqueStep
import com.sudoku.ui.theme.SudokuColors

@Composable
fun HintCard(
    hint: TechniqueStep,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SudokuColors.CardBackground)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💡 ${hint.technique.name}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFFFBBF24)
            )
            OutlinedButton(
                onClick = onDismiss,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text("Dismiss", fontSize = 12.sp, color = Color(0xFF94A3B8))
            }
        }

        Text(
            text = hint.explanation,
            fontSize = 13.sp,
            color = Color(0xFFCBD5E1),
            lineHeight = 17.sp
        )

        Button(
            onClick = onApply,
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B), contentColor = Color.Black)
        ) {
            Text(
                text = if (hint.placedDigit != null) "Place Number" else "Apply Deduction",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
