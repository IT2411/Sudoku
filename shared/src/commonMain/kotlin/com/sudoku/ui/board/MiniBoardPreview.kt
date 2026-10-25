package com.sudoku.ui.board

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudoku.core.model.CellPosition
import com.sudoku.ui.theme.SudokuColors

@Composable
fun MiniBoardPreview(
    grid: IntArray,
    primaryIndices: Set<Int>,
    secondaryIndices: Set<Int> = emptySet(),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(150.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF141722))
    ) {
        // Grid cells
        Column(modifier = Modifier.fillMaxSize()) {
            for (r in 0..8) {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    for (c in 0..8) {
                        val idx = r * 9 + c
                        val v = grid.getOrElse(idx) { 0 }
                        val isPrimary = primaryIndices.contains(idx)
                        val isSecondary = secondaryIndices.contains(idx)

                        val cellBg = when {
                            isPrimary -> Color(0xFFEAB308).copy(alpha = 0.4f)
                            isSecondary -> Color(0xFF10B981).copy(alpha = 0.3f)
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(cellBg),
                            contentAlignment = Alignment.Center
                        ) {
                            if (v != 0) {
                                Text(
                                    text = v.toString(),
                                    fontSize = 9.sp,
                                    fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isPrimary) Color(0xFFFDE047) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Overlay Divider Lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = size.width / 9f
            val cellH = size.height / 9f

            for (i in 1..8) {
                val isThick = i % 3 == 0
                val strokeW = if (isThick) 1.5.dp.toPx() else 0.5.dp.toPx()
                val color = if (isThick) Color(0xFF475569) else Color(0xFF1E2434)

                // Vertical
                drawLine(
                    color = color,
                    start = Offset(x = i * cellW, y = 0f),
                    end = Offset(x = i * cellW, y = size.height),
                    strokeWidth = strokeW
                )

                // Horizontal
                drawLine(
                    color = color,
                    start = Offset(x = 0f, y = i * cellH),
                    end = Offset(x = size.width, y = i * cellH),
                    strokeWidth = strokeW
                )
            }
        }
    }
}
