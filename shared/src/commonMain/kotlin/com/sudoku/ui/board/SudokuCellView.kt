package com.sudoku.ui.board

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sudoku.core.interaction.CellHighlightState
import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates
import com.sudoku.core.model.SudokuBoard
import com.sudoku.ui.theme.SudokuColors
import kotlinx.coroutines.delay

@Composable
fun SudokuCellView(
    position: CellPosition,
    value: Int,
    notes: Candidates,
    isGiven: Boolean,
    highlight: CellHighlightState,
    isAnimatingUnitCompletion: Boolean,
    activeHighlightDigit: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val waveIntensity = remember { Animatable(0f) }

    // Staggered wave animation sweeping from left to right based on cell column
    LaunchedEffect(isAnimatingUnitCompletion) {
        if (isAnimatingUnitCompletion) {
            val columnDelay = position.column * 55L
            delay(columnDelay)
            // Gentle ease-in lift
            waveIntensity.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
            )
            // Smooth ease-out descent
            waveIntensity.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
            )
        } else {
            waveIntensity.snapTo(0f)
        }
    }

    val baseBgColor = when {
        highlight.hasConflict -> SudokuColors.CellConflict
        highlight.isHintPrimary -> SudokuColors.CellHintPrimary
        highlight.isHintSecondary -> SudokuColors.CellHintSecondary
        highlight.isSelected -> SudokuColors.CellSelected
        highlight.isSameDigit -> SudokuColors.CellSameDigit
        highlight.isPeer -> SudokuColors.CellPeer
        else -> SudokuColors.Surface
    }

    // Blend base color with the wave highlight
    val renderedBgColor = if (waveIntensity.value > 0f) {
        lerp(baseBgColor, SudokuColors.UnitCompletionWave, waveIntensity.value)
    } else {
        baseBgColor
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(renderedBgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (value != SudokuBoard.EMPTY_VALUE) {
            val textColor = when {
                highlight.hasConflict -> SudokuColors.TextMistake
                isGiven -> SudokuColors.TextGiven
                else -> SudokuColors.TextPlayer
            }

            Text(
                text = value.toString(),
                fontSize = 24.sp,
                fontWeight = if (isGiven) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        } else if (notes.isNotEmpty) {
            NotesGrid(
                candidates = notes,
                highlightDigit = activeHighlightDigit
            )
        }
    }
}
