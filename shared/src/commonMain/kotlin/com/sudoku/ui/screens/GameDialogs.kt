package com.sudoku.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.window.Dialog
import com.sudoku.core.achievements.Achievement
import com.sudoku.core.difficulty.Difficulty
import com.sudoku.ui.theme.SudokuColors

@Composable
fun CompletionDialog(
    elapsedSeconds: Long,
    mistakes: Int,
    hintsUsed: Int,
    difficulty: Difficulty,
    newAchievements: List<Achievement> = emptyList(),
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SudokuColors.CardBackground)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "🎉", fontSize = 48.sp)
            Text(
                text = "Puzzle Solved!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SudokuColors.TextGiven
            )
            Text(
                text = "${difficulty.displayName} • ${formatSeconds(elapsedSeconds)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF34D399)
            )

            // Newly Unlocked Achievements Badge Banner
            if (newAchievements.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2A2438))
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🏆 Unlocked Badges!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                    newAchievements.forEach { ach ->
                        Text(
                            text = "${ach.icon} ${ach.title}",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Mistakes: $mistakes", fontSize = 14.sp, color = SudokuColors.TextNote)
            Text(text = "Hints Used: $hintsUsed", fontSize = 14.sp, color = SudokuColors.TextNote)

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Play Again (${difficulty.displayName})", color = Color.White)
                }

                OutlinedButton(
                    onClick = onMainMenu,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🏠 Main Menu", color = Color(0xFFCBD5E1))
                }
            }
        }
    }
}

@Composable
fun GameOverDialog(
    difficulty: Difficulty,
    onRetry: () -> Unit,
    onMainMenu: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SudokuColors.CardBackground)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "❌", fontSize = 48.sp)
            Text(
                text = "Game Over",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SudokuColors.TextMistake
            )
            Text(
                text = "You reached the limit of 3 mistakes.",
                fontSize = 14.sp,
                color = SudokuColors.TextNote
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Try Again (${difficulty.displayName})", color = Color.White)
                }

                OutlinedButton(
                    onClick = onMainMenu,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🏠 Main Menu", color = Color(0xFFCBD5E1))
                }
            }
        }
    }
}

fun formatSeconds(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}
