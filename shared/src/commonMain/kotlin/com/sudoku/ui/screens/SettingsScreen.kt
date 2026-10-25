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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.sudoku.core.haptics.Haptics
import com.sudoku.core.settings.GameSettings
import com.sudoku.core.statistics.PlayerStatistics
import com.sudoku.core.storage.GameRepository
import com.sudoku.core.storage.SettingsRepository
import com.sudoku.core.storage.StatisticsRepository
import com.sudoku.ui.theme.SudokuColors

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var settings by remember { mutableStateOf(SettingsRepository.loadSettings()) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    fun updateSettings(newSettings: GameSettings) {
        settings = newSettings
        SettingsRepository.saveSettings(newSettings)
        Haptics.click()
    }

    fun wipeAllUserData() {
        val driver = GameRepository.driver
        driver?.clear("sudoku_player_stats")
        driver?.clear("sudoku_achievements")
        driver?.clear("active_sudoku_game")
        driver?.clear("daily_completed_epoch_day")
        StatisticsRepository.saveStats(PlayerStatistics())
        GameRepository.clearGame()
        Haptics.error()
    }

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
                    text = "⚙️ Settings",
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

            Spacer(modifier = Modifier.height(24.dp))

            // Gameplay Settings Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SudokuColors.CardBackground)
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    SettingToggleRow(
                        title = "📳 Haptic Feedback",
                        subtitle = "Vibrate on button clicks and completions",
                        checked = settings.hapticsEnabled,
                        onCheckedChange = { updateSettings(settings.copy(hapticsEnabled = it)) }
                    )

                    SettingToggleRow(
                        title = "❌ 3-Mistakes Limit",
                        subtitle = "Standard 3 errors limit (turn off for unlimited practice)",
                        checked = settings.mistakeLimitEnabled,
                        onCheckedChange = { updateSettings(settings.copy(mistakeLimitEnabled = it)) }
                    )

                    SettingToggleRow(
                        title = "⏱ Show Timer",
                        subtitle = "Display elapsed solving time in games",
                        checked = settings.timerVisible,
                        onCheckedChange = { updateSettings(settings.copy(timerVisible = it)) }
                    )

                    SettingToggleRow(
                        title = "✏️ Auto-Clear Pencil Notes",
                        subtitle = "Remove matching notes in row, column, and box",
                        checked = settings.autoRemoveNotes,
                        onCheckedChange = { updateSettings(settings.copy(autoRemoveNotes = it)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Data & Progress Management Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SudokuColors.CardBackground)
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "DATA & PROGRESS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Reset all player statistics, win streaks, unlocked achievement badges, and current game progress.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            Haptics.click()
                            showDeleteConfirmDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF451A1A))
                    ) {
                        Text("🗑 Delete All User Data", color = Color(0xFFF87171), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Sudoku Engine • Version 1.0.0",
                fontSize = 12.sp,
                color = Color(0xFF475569)
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirmDialog) {
            Dialog(onDismissRequest = { showDeleteConfirmDialog = false }) {
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
                        text = "Delete All Data?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "This will permanently erase your win streaks, best times, statistics, unlocked badges, and active game save. This action cannot be undone.",
                        fontSize = 13.sp,
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
                                wipeAllUserData()
                                showDeleteConfirmDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                        ) {
                            Text("Yes, Delete Everything", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showDeleteConfirmDialog = false },
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

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF94A3B8), lineHeight = 16.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF2563EB),
                uncheckedThumbColor = Color(0xFF64748B),
                uncheckedTrackColor = Color(0xFF1E2235)
            )
        )
    }
}
