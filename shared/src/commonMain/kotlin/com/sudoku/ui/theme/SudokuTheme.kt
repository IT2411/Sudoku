package com.sudoku.ui.theme

import androidx.compose.ui.graphics.Color

object SudokuColors {
    // Dark Canvas & Surfaces
    val Background = Color(0xFF0F1117)
    val Surface = Color(0xFF181B24)
    val CardBackground = Color(0xFF1F2330)

    // Grid Dividers
    val ThinBorder = Color(0xFF282C3D)
    val ThickBorder = Color(0xFF5E6582)

    // Cell Highlighting States
    val CellSelected = Color(0xFF253366)       // Focused cell
    val CellPeer = Color(0xFF1C2130)           // Row/col/box crosshair
    val CellSameDigit = Color(0xFF1E2E4A)      // Matching digits glow
    val CellConflict = Color(0xFF4A181E)       // Dark crimson collision
    val CellHintPrimary = Color(0xFF453A17)    // Gold amber hint focal
    val CellHintSecondary = Color(0xFF1B3D2B)  // Forest green hint scope

    // Subtle Wave Highlight (Gentle luminance lift)
    val UnitCompletionWave = Color(0xFF2E364E)

    // Typography Colors
    val TextGiven = Color(0xFFF1F5F9)          // Clues (high-contrast ice white)
    val TextPlayer = Color(0xFF60A5FA)         // Player digits (sapphire)
    val TextMistake = Color(0xFFF87171)        // Error red
    val TextNote = Color(0xFF64748B)           // Muted slate notes
    val NoteHighlighted = Color(0xFF38BDF8)    // Matching pencil marks

    // Number Pad
    val PadButtonBg = Color(0xFF222634)
    val PadButtonText = Color(0xFFE2E8F0)
    val PadButtonDisabledBg = Color(0xFF14161F)
    val PadButtonDisabledText = Color(0xFF3B4254)
}
