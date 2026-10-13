package com.sudoku.core.interaction

/**
 * Visual styling flags for a single cell on the Sudoku board.
 */
data class CellHighlightState(
    val isSelected: Boolean = false,
    val isPeer: Boolean = false,             // Shares row, column, or box with selected cell
    val isSameDigit: Boolean = false,        // Has identical placed digit to selected
    val hasMatchingNote: Boolean = false,    // Is empty, but has note matching selected digit
    val hasConflict: Boolean = false         // Involved in a duplicate rule violation
) {
    companion object {
        val DEFAULT = CellHighlightState()
    }
}