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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sudoku.core.daily.DailyPuzzleEngine
import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.haptics.Haptics
import com.sudoku.core.storage.GameStateSnapshot
import com.sudoku.ui.theme.SudokuColors

@Composable
fun HomeScreen(
    savedSnapshot: GameStateSnapshot?,
    onContinueGame: () -> Unit,
    onStartDaily: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenBadges: () -> Unit,
    onOpenTips: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartNewGame: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDailyDone = remember { DailyPuzzleEngine.isTodayCompleted() }
    var pendingDifficulty by remember { mutableStateOf<Difficulty?>(null) }
    var pendingDailyStart by remember { mutableStateOf(false) }

    fun requestStartGame(diff: Difficulty) {
        if (savedSnapshot != null) {
            pendingDifficulty = diff
        } else {
            onStartNewGame(diff)
        }
    }

    fun requestStartDaily() {
        if (isDailyDone) return
        if (savedSnapshot != null) {
            pendingDailyStart = true
        } else {
            onStartDaily()
        }
    }

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
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "🔢", fontSize = 28.sp)
                    Text(
                        text = "Sudoku",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = SudokuColors.TextGiven
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "📖",
                        fontSize = 15.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2235))
                            .clickable {
                                Haptics.click()
                                onOpenTips()
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )

                    Text(
                        text = "🏆",
                        fontSize = 15.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2235))
                            .clickable {
                                Haptics.click()
                                onOpenBadges()
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )

                    Text(
                        text = "📊",
                        fontSize = 15.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2235))
                            .clickable {
                                Haptics.click()
                                onOpenStats()
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )

                    Text(
                        text = "⚙️",
                        fontSize = 15.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2235))
                            .clickable {
                                Haptics.click()
                                onOpenSettings()
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Continue Game Card
            if (savedSnapshot != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1E2438))
                        .clickable {
                            Haptics.click()
                            onContinueGame()
                        }
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "▶ Continue Game",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF60A5FA)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${savedSnapshot.difficulty} • ${formatSeconds(savedSnapshot.elapsedSeconds)}",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Button(
                            onClick = {
                                Haptics.click()
                                onContinueGame()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Text("Resume", color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Daily Challenge Card
            val dailyBg = if (isDailyDone) Color(0xFF1A1A24) else Color(0xFF24223A)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(dailyBg)
                    .clickable(enabled = !isDailyDone) {
                        Haptics.click()
                        requestStartDaily()
                    }
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isDailyDone) "📅 Daily Challenge ✓" else "📅 Daily Challenge",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDailyDone) Color(0xFF34D399) else Color(0xFFC084FC)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isDailyDone) "Completed for today! Come back tomorrow" else "Worldwide seed for today",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Button(
                        onClick = {
                            Haptics.click()
                            requestStartDaily()
                        },
                        enabled = !isDailyDone,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9333EA),
                            disabledContainerColor = Color(0xFF2E2B3D),
                            disabledContentColor = Color(0xFF6B7280)
                        )
                    ) {
                        Text(if (isDailyDone) "Done ✓" else "Play", color = if (isDailyDone) Color(0xFF9CA3AF) else Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Standard New Game
            Text(
                text = "START NEW GAME",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.2.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            .clickable {
                                Haptics.click()
                                requestStartGame(diff)
                            }
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = diff.displayName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SudokuColors.TextGiven
                            )
                            Text("●", fontSize = 16.sp, color = color)
                        }
                    }
                }
            }
        }

        // Confirmation Dialog
        if (pendingDifficulty != null || pendingDailyStart) {
            Dialog(onDismissRequest = {
                pendingDifficulty = null
                pendingDailyStart = false
            }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SudokuColors.CardBackground)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "⚠️", fontSize = 38.sp)
                    Text(
                        text = "Start New Game?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "You have an ongoing ${savedSnapshot?.difficulty ?: ""} game in progress. Starting a new game will discard your current progress.",
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val diff = pendingDifficulty
                                val isDaily = pendingDailyStart
                                pendingDifficulty = null
                                pendingDailyStart = false
                                if (isDaily) onStartDaily() else if (diff != null) onStartNewGame(diff)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                        ) {
                            Text("Discard & Start New", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                pendingDifficulty = null
                                pendingDailyStart = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancel", color = Color(0xFFCBD5E1))
                        }
                    }
                }
            }
        }
    }
}
