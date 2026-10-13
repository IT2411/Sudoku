package com.sudoku.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.sudoku.core.storage.GameStateSnapshot
import com.sudoku.ui.theme.SudokuColors

@Composable
fun HomeScreen(
    savedSnapshot: GameStateSnapshot?,
    onContinueGame: () -> Unit,
    onStartNewGame: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SudokuColors.Background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Title & Logo Icon
            Text(text = "🔢", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Sudoku",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = SudokuColors.TextGiven
            )
            Text(
                text = "Clean • Logic • Offline",
                fontSize = 14.sp,
                color = SudokuColors.TextNote
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Continue Game Card (if in-progress game exists)
            if (savedSnapshot != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E2438))
                        .clickable(onClick = onContinueGame)
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "▶ Continue Game",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF60A5FA)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${savedSnapshot.difficulty} • ${formatSeconds(savedSnapshot.elapsedSeconds)}",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Button(
                            onClick = onContinueGame,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Text("Resume", color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }

            // New Game Section
            Text(
                text = "START NEW GAME",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.5.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Difficulty Selection Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Difficulty.entries.forEach { diff ->
                    val color = when (diff) {
                        Difficulty.EASY -> Color(0xFF10B981)
                        Difficulty.MEDIUM -> Color(0xFF3B82F6)
                        Difficulty.HARD -> Color(0xFFF59E0B)
                        Difficulty.EXPERT -> Color(0xFFEF4444)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SudokuColors.CardBackground)
                            .clickable { onStartNewGame(diff) }
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = diff.displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SudokuColors.TextGiven
                            )
                            Text(
                                text = "●",
                                fontSize = 16.sp,
                                color = color
                            )
                        }
                    }
                }
            }
        }
    }
}
