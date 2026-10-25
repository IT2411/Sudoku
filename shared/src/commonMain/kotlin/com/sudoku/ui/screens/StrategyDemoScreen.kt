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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.sudoku.core.difficulty.SolvingTechnique
import com.sudoku.core.haptics.Haptics
import com.sudoku.core.tutorial.DemoBoards
import com.sudoku.ui.board.SudokuBoardView
import com.sudoku.ui.theme.SudokuColors

@Composable
fun StrategyDemoScreen(
    technique: SolvingTechnique,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val demos = remember(technique) { DemoBoards.getDemoList(technique) }
    var currentDemoIndex by remember { mutableIntStateOf(0) }
    val activeDemo = demos[currentDemoIndex]

    var boardVersion by remember { mutableIntStateOf(0) }
    var isStepApplied by remember { mutableStateOf(false) }

    fun switchDemo(newIndex: Int) {
        currentDemoIndex = newIndex
        isStepApplied = false
        boardVersion++
        Haptics.click()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SudokuColors.Background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        Haptics.click()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SudokuColors.CardBackground)
                ) {
                    Text("← Back to Tips", color = Color(0xFFCBD5E1))
                }

                // Example Switcher Pills
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    demos.forEachIndexed { idx, _ ->
                        val isSelected = idx == currentDemoIndex
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF2563EB) else SudokuColors.CardBackground)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Ex ${idx + 1}",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }

            // Strategy Title & Subtitle
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = activeDemo.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = activeDemo.subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFA5B4FC)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = activeDemo.explanation,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            // Interactive Board View
            androidx.compose.runtime.key(boardVersion, currentDemoIndex) {
                SudokuBoardView(
                    game = activeDemo.game,
                    activeHint = activeDemo.hintStep,
                    onCellSelected = { pos ->
                        Haptics.click()
                        activeDemo.game.selectCell(pos)
                        boardVersion++
                    }
                )
            }

            // Live Deduction Card + Navigation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SudokuColors.CardBackground)
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🔍 Live Deduction on this Board:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFFFBBF24)
                    )

                    Text(
                        text = activeDemo.hintStep.explanation,
                        fontSize = 12.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 17.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous / Next Example Switcher
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (currentDemoIndex > 0) {
                                OutlinedButton(
                                    onClick = { switchDemo(currentDemoIndex - 1) },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("◀ Prev", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                                }
                            }
                            if (currentDemoIndex < demos.lastIndex) {
                                OutlinedButton(
                                    onClick = { switchDemo(currentDemoIndex + 1) },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Next ▶", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                                }
                            }
                        }

                        if (!isStepApplied) {
                            Button(
                                onClick = {
                                    Haptics.success()
                                    activeDemo.game.applyHint(activeDemo.hintStep)
                                    isStepApplied = true
                                    boardVersion++
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Apply ✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else {
                            Text(
                                text = "✓ Applied!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}
