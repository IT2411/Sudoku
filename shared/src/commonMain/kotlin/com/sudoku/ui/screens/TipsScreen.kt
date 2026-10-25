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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.sudoku.core.difficulty.SolvingTechnique
import com.sudoku.core.haptics.Haptics
import com.sudoku.core.tutorial.DemoBoards
import com.sudoku.ui.board.MiniBoardPreview
import com.sudoku.ui.theme.SudokuColors

data class TipTechnique(
    val title: String,
    val level: String,
    val levelColor: Color,
    val summary: String,
    val explanation: String,
    val proTip: String,
    val technique: SolvingTechnique
)

@Composable
fun TipsScreen(
    onBack: () -> Unit,
    onOpenDemo: (SolvingTechnique) -> Unit,
    modifier: Modifier = Modifier
) {
    val techniques = remember {
        listOf(
            TipTechnique(
                title = "1. Naked Single",
                level = "Beginner",
                levelColor = Color(0xFF10B981),
                summary = "A cell where only one number is legally possible.",
                explanation = "If the other 8 numbers (1–9) are already present in its shared Row, Column, and 3×3 Box, those 8 digits are eliminated. That cell MUST be the 1 remaining digit.",
                proTip = "Spot these easily by turning on Notes mode and looking for cells with 1 note.",
                technique = SolvingTechnique.NakedSingle
            ),
            TipTechnique(
                title = "2. Hidden Single",
                level = "Beginner",
                levelColor = Color(0xFF10B981),
                summary = "A digit can only go in one spot within a row, column, or box.",
                explanation = "Even if a cell has multiple candidate notes, check the rest of its row or column. If a specific digit cannot fit in any other empty cell in that unit, it is 'hidden' in this cell.",
                proTip = "Scan units with 6 or 7 clues filled first — missing numbers are easiest to place.",
                technique = SolvingTechnique.HiddenSingle
            ),
            TipTechnique(
                title = "3. Naked Pair",
                level = "Intermediate",
                levelColor = Color(0xFF3B82F6),
                summary = "Two cells in the same unit contain only the exact same two candidates.",
                explanation = "If two cells in Row 1 only contain candidates {3, 7}, one must be 3 and the other must be 7. Therefore, 3 and 7 can be safely erased from all other cells in that row.",
                proTip = "This also extends to Naked Triples ({2, 5, 8} across 3 cells).",
                technique = SolvingTechnique.NakedPair
            ),
            TipTechnique(
                title = "4. Pointing Pair / Triple",
                level = "Intermediate",
                levelColor = Color(0xFF3B82F6),
                summary = "Candidates in a box are locked along a straight line.",
                explanation = "If all candidates for digit 4 in Box 1 lie in the same row, 4 is guaranteed to be in that row inside Box 1. Therefore, 4 cannot appear anywhere else along that entire row outside the box.",
                proTip = "Look for this when a box has only 2 or 3 instances of a candidate.",
                technique = SolvingTechnique.PointingPair
            ),
            TipTechnique(
                title = "5. X-Wing",
                level = "Advanced",
                levelColor = Color(0xFFF59E0B),
                summary = "A digit forms a 2x2 rectangle across two rows and two columns.",
                explanation = "Suppose digit 5 appears as a candidate exactly twice in Row 2 and twice in Row 7, and both pairs share Column 3 and Column 8. The 5s must occupy opposite corners of this rectangle, eliminating 5 from all other cells in those two columns!",
                proTip = "When you tap a number and see its notes forming a rectangle, look for the X-Wing.",
                technique = SolvingTechnique.XWing
            )
        )
    }

    var selectedFilter by remember { mutableStateOf("All") }
    val filtered = remember(selectedFilter) {
        if (selectedFilter == "All") techniques else techniques.filter { it.level == selectedFilter }
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
                Column {
                    Text(
                        text = "📖 Tips & Tricks",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SudokuColors.TextGiven
                    )
                    Text(
                        text = "Visual patterns and interactive demos",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Button(
                    onClick = {
                        Haptics.click()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SudokuColors.CardBackground)
                ) {
                    Text("Close", color = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "Beginner", "Intermediate", "Advanced").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFF2563EB) else SudokuColors.CardBackground)
                            .clickable {
                                Haptics.click()
                                selectedFilter = filter
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Cards with Mini Visual Board
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                filtered.forEach { tech ->
                    TipCardWithVisualBoard(
                        tech = tech,
                        onOpenDemo = {
                            Haptics.click()
                            onOpenDemo(tech.technique)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TipCardWithVisualBoard(
    tech: TipTechnique,
    onOpenDemo: () -> Unit
) {
    val sampleDemo = remember(tech.technique) { DemoBoards.getDemoList(tech.technique).first() }
    val primaryIndices = remember(sampleDemo) { sampleDemo.hintStep.primaryCells.map { it.index }.toSet() }
    val secondaryIndices = remember(sampleDemo) { sampleDemo.hintStep.secondaryCells.map { it.index }.toSet() }
    val rawGrid = remember(sampleDemo) { sampleDemo.game.board.rawValues() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SudokuColors.CardBackground)
            .clickable(onClick = onOpenDemo)
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header Row: Title & Level
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tech.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = tech.level,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tech.levelColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(tech.levelColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            // Summary
            Text(
                text = tech.summary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFA5B4FC)
            )

            // Explanation Text
            Text(
                text = tech.explanation,
                fontSize = 12.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 17.sp
            )

            // Visual Mini Board Preview (Static, Same Every Time)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF141722))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MiniBoardPreview(
                    grid = rawGrid,
                    primaryIndices = primaryIndices,
                    secondaryIndices = secondaryIndices
                )

                Column(
                    modifier = Modifier.padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFFEAB308), RoundedCornerShape(2.dp)))
                        Text("Pattern Focus", fontSize = 11.sp, color = Color(0xFFE2E8F0))
                    }
                    if (secondaryIndices.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(10.dp).background(Color(0xFF10B981), RoundedCornerShape(2.dp)))
                            Text("Eliminations", fontSize = 11.sp, color = Color(0xFFE2E8F0))
                        }
                    }
                    Text("Always identical setup", fontSize = 10.sp, color = Color(0xFF64748B))
                }
            }

            // Pro Tip & Interactive Demo Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💡 ${tech.proTip}",
                    fontSize = 11.sp,
                    color = Color(0xFFFBBF24),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )

                Text(
                    text = "🎮 Try Demos →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF60A5FA)
                )
            }
        }
    }
}
