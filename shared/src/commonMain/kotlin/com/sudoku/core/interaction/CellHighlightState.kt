package com.sudoku.core.interaction

data class CellHighlightState(
    val isSelected: Boolean = false,
    val isPeer: Boolean = false,
    val isSameDigit: Boolean = false,
    val hasMatchingNote: Boolean = false,
    val hasConflict: Boolean = false,
    val isHintPrimary: Boolean = false,    // Core cell targeted by the hint
    val isHintSecondary: Boolean = false   // Affected peer/elimination cells
) {
    companion object {
        val DEFAULT = CellHighlightState()
    }
}