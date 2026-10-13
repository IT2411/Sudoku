package com.sudoku.core.difficulty

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates

/**
 * Details of a logical deduction found by a human technique.
 */
data class TechniqueStep(
    val technique: SolvingTechnique,
    val primaryCells: Set<CellPosition>,
    val secondaryCells: Set<CellPosition> = emptySet(),
    val placedDigit: Pair<CellPosition, Int>? = null,
    val eliminatedCandidates: Map<CellPosition, Candidates> = emptyMap(),
    val explanation: String
)