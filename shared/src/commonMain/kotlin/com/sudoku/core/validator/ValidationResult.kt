package com.sudoku.core.validator

import com.sudoku.core.model.CellPosition

sealed interface ValidationResult {
    data object Valid : ValidationResult
    
    data class Invalid(
        val conflictingPositions: Set<CellPosition>,
        val reason: String
    ) : ValidationResult

    val isValid: Boolean get() = this is Valid
}