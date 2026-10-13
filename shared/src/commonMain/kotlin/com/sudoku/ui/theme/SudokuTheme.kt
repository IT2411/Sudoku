package com.sudoku.ui.theme

import androidx.compose.ui.graphics.Color

object SudokuColors {
    // Canvas & Surface
    val Background = Color(0xFFF7F9FC)
    val Surface = Color(0xFFFFFFFF)

    // Grid Dividers
    val ThinBorder = Color(0xFFCFD8DC)
    val ThickBorder = Color(0xFF263238)

    // Cell Highlighting States
    val CellSelected = Color(0xFFBBDEFB)        // Active focus cell
    val CellPeer = Color(0xFFE8F0FE)            // Row, Col, and Box crosshair
    val CellSameDigit = Color(0xFFD1E3FF)       // All matching placed digits
    val CellConflict = Color(0xFFFFCDD2)        // Duplicate collision error
    val NoteHighlighted = Color(0xFF1976D2)     // Matching pencil notes

    // Typography Colors
    val TextGiven = Color(0xFF1A237E)           // Clues (dark navy, bold)
    val TextPlayer = Color(0xFF0D47A1)          // Player numbers (sapphire)
    val TextMistake = Color(0xFFC62828)         // Conflicting number
    val TextNote = Color(0xFF607D8B)            // Normal pencil mark
}