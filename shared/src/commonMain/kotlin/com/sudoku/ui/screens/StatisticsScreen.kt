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
import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.statistics.PlayerStatistics
import com.sudoku.ui.theme.SudokuColors

@Composable
fun StatisticsScreen(
    stats: PlayerStatistics,
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
                Text(
                    text = "📊 Statistics",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = SudokuColors.TextGiven
                )
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = SudokuColors.CardBackground)
                ) {
                    Text("Close", color = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Overview 4-Stat Grid Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(title = "Played", value = stats.totalGamesPlayed.toString(), modifier = Modifier.weight(1f))
                StatCard(title = "Win %", value = "${stats.winRatePercentage}%", modifier = Modifier.weight(1f))
                StatCard(title = "Streak", value = stats.currentStreak.toString(), modifier = Modifier.weight(1f))
                StatCard(title = "Best", value = stats.bestStreak.toString(), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "DIFFICULTY BREAKDOWN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.2.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Breakdown by Difficulty
            Difficulty.entries.forEach { diff ->
                val diffStats = stats.statsByDifficulty[diff]
                val dotColor = when (diff) {
                    Difficulty.EASY -> Color(0xFF10B981)
                    Difficulty.MEDIUM -> Color(0xFF3B82F6)
                    Difficulty.HARD -> Color(0xFFF59E0B)
                    Difficulty.EXPERT -> Color(0xFFEF4444)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SudokuColors.CardBackground)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("●", color = dotColor, fontSize = 14.sp)
                                Text(diff.displayName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                            }
                            Text("Won: ${diffStats?.gamesWon ?: 0}", color = Color(0xFF94A3B8), fontSize = 13.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Best: ${if ((diffStats?.bestTimeSeconds ?: 0) > 0) formatSeconds(diffStats!!.bestTimeSeconds) else "--:--"}",
                                color = Color(0xFFCBD5E1),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Avg: ${if ((diffStats?.averageTimeSeconds ?: 0) > 0) formatSeconds(diffStats!!.averageTimeSeconds) else "--:--"}",
                                color = Color(0xFFCBD5E1),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SudokuColors.CardBackground)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SudokuColors.TextPlayer)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, fontSize = 11.sp, color = Color(0xFF94A3B8))
        }
    }
}
