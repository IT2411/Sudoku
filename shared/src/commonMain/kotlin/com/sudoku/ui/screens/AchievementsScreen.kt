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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudoku.core.achievements.Achievement
import com.sudoku.ui.theme.SudokuColors

@Composable
fun AchievementsScreen(
    unlocked: Set<Achievement>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SudokuColors.Background)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 440.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🏆 Badges",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SudokuColors.TextGiven
                    )
                    Text(
                        text = "${unlocked.size} of ${Achievement.entries.size} Unlocked",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = SudokuColors.CardBackground)
                ) {
                    Text("Close", color = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Badges List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Achievement.entries.forEach { ach ->
                    val isUnlocked = unlocked.contains(ach)
                    val cardBg = if (isUnlocked) Color(0xFF1E2438) else Color(0xFF151822)
                    val titleColor = if (isUnlocked) Color.White else Color(0xFF64748B)
                    val descColor = if (isUnlocked) Color(0xFF94A3B8) else Color(0xFF475569)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardBg)
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = if (isUnlocked) ach.icon else "🔒",
                                fontSize = 28.sp
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ach.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = ach.description,
                                    fontSize = 12.sp,
                                    color = descColor,
                                    lineHeight = 16.sp
                                )
                            }

                            if (isUnlocked) {
                                Text(
                                    text = "✓",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
