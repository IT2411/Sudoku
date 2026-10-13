package com.sudoku.core.difficulty

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates
import com.sudoku.core.model.SudokuBoard

sealed interface SolvingTechnique {
    val name: String
    val weight: Int

    /**
     * Attempts to find the first applicable step using this technique.
     * Returns null if no deduction can be made.
     */
    fun findStep(board: SudokuBoard, candidates: Array<Candidates>): TechniqueStep?

    // -------------------------------------------------------------
    // 1. NAKED SINGLE
    // A cell has only one legal candidate left.
    // -------------------------------------------------------------
    data object NakedSingle : SolvingTechnique {
        override val name: String = "Naked Single"
        override val weight: Int = 1

        override fun findStep(board: SudokuBoard, candidates: Array<Candidates>): TechniqueStep? {
            for (index in 0 until CellPosition.TOTAL_CELLS) {
                if (board[index] == SudokuBoard.EMPTY_VALUE) {
                    val cand = candidates[index]
                    val single = cand.singleOrNull()
                    if (single != null) {
                        val pos = CellPosition.fromIndex(index)
                        return TechniqueStep(
                            technique = this,
                            primaryCells = setOf(pos),
                            placedDigit = pos to single,
                            explanation = "Cell (R${pos.row + 1}, C${pos.column + 1}) can only be $single as all other digits are blocked."
                        )
                    }
                }
            }
            return null
        }
    }

    // -------------------------------------------------------------
    // 2. HIDDEN SINGLE
    // A digit appears in only one empty cell within a row, col, or box.
    // -------------------------------------------------------------
    data object HiddenSingle : SolvingTechnique {
        override val name: String = "Hidden Single"
        override val weight: Int = 2

        override fun findStep(board: SudokuBoard, candidates: Array<Candidates>): TechniqueStep? {
            // Check Rows, Columns, Boxes
            for (unitType in 0..2) {
                for (unitIndex in 0..8) {
                    val step = checkUnit(board, candidates, unitType, unitIndex)
                    if (step != null) return step
                }
            }
            return null
        }

        private fun checkUnit(
            board: SudokuBoard,
            candidates: Array<Candidates>,
            unitType: Int,
            unitIndex: Int
        ): TechniqueStep? {
            val positions = getUnitPositions(unitType, unitIndex)

            for (digit in 1..9) {
                var count = 0
                var targetPos: CellPosition? = null

                for (pos in positions) {
                    if (board[pos] == SudokuBoard.EMPTY_VALUE && candidates[pos.index].contains(digit)) {
                        count++
                        targetPos = pos
                        if (count > 1) break
                    }
                }

                if (count == 1 && targetPos != null) {
                    val unitName = when (unitType) {
                        0 -> "row ${unitIndex + 1}"
                        1 -> "column ${unitIndex + 1}"
                        else -> "box ${unitIndex + 1}"
                    }
                    return TechniqueStep(
                        technique = this,
                        primaryCells = setOf(targetPos),
                        placedDigit = targetPos to digit,
                        explanation = "$digit can only be placed here in $unitName."
                    )
                }
            }
            return null
        }
    }

    // -------------------------------------------------------------
    // 3. NAKED PAIR
    // Two cells in the same unit share the exact same 2 candidates.
    // -------------------------------------------------------------
    data object NakedPair : SolvingTechnique {
        override val name: String = "Naked Pair"
        override val weight: Int = 4

        override fun findStep(board: SudokuBoard, candidates: Array<Candidates>): TechniqueStep? {
            for (unitType in 0..2) {
                for (unitIndex in 0..8) {
                    val positions = getUnitPositions(unitType, unitIndex).filter { board[it] == SudokuBoard.EMPTY_VALUE }
                    val pairCells = positions.filter { candidates[it.index].size == 2 }

                    for (i in pairCells.indices) {
                        for (j in (i + 1) until pairCells.size) {
                            val c1 = pairCells[i]
                            val c2 = pairCells[j]
                            val cand1 = candidates[c1.index]
                            val cand2 = candidates[c2.index]

                            if (cand1.mask == cand2.mask) {
                                // Found pair! Check if other cells in this unit contain these digits
                                val eliminations = mutableMapOf<CellPosition, Candidates>()
                                val pairDigits = cand1.toList()

                                for (other in positions) {
                                    if (other != c1 && other != c2) {
                                        val otherCand = candidates[other.index]
                                        var toRemove = 0
                                        for (d in pairDigits) {
                                            if (otherCand.contains(d)) {
                                                toRemove = toRemove or (1 shl (d - 1))
                                            }
                                        }
                                        if (toRemove != 0) {
                                            eliminations[other] = Candidates.fromMask(toRemove)
                                        }
                                    }
                                }

                                if (eliminations.isNotEmpty()) {
                                    return TechniqueStep(
                                        technique = this,
                                        primaryCells = setOf(c1, c2),
                                        secondaryCells = eliminations.keys,
                                        eliminatedCandidates = eliminations,
                                        explanation = "Cells form a Naked Pair with digits ${pairDigits.joinToString(",")}. These digits are eliminated from other cells in this unit."
                                    )
                                }
                            }
                        }
                    }
                }
            }
            return null
        }
    }

    // -------------------------------------------------------------
    // 4. POINTING PAIR / TRIPLE
    // All candidates for a digit within a box lie in the same row/col.
    // -------------------------------------------------------------
    data object PointingPair : SolvingTechnique {
        override val name: String = "Pointing Pair/Triple"
        override val weight: Int = 5

        override fun findStep(board: SudokuBoard, candidates: Array<Candidates>): TechniqueStep? {
            for (box in 0..8) {
                val boxPositions = getUnitPositions(2, box).filter { board[it] == SudokuBoard.EMPTY_VALUE }

                for (digit in 1..9) {
                    val cellsWithDigit = boxPositions.filter { candidates[it.index].contains(digit) }
                    if (cellsWithDigit.size in 2..3) {
                        val sameRow = cellsWithDigit.map { it.row }.distinct().size == 1
                        val sameCol = cellsWithDigit.map { it.column }.distinct().size == 1

                        if (sameRow) {
                            val row = cellsWithDigit.first().row
                            val rowPositions = getUnitPositions(0, row).filter { board[it] == SudokuBoard.EMPTY_VALUE }
                            val eliminations = mutableMapOf<CellPosition, Candidates>()

                            for (other in rowPositions) {
                                if (other.box != box && candidates[other.index].contains(digit)) {
                                    eliminations[other] = Candidates.of(digit)
                                }
                            }

                            if (eliminations.isNotEmpty()) {
                                return TechniqueStep(
                                    technique = this,
                                    primaryCells = cellsWithDigit.toSet(),
                                    secondaryCells = eliminations.keys,
                                    eliminatedCandidates = eliminations,
                                    explanation = "Digit $digit in Box ${box + 1} is locked into Row ${row + 1}, eliminating it from the rest of the row."
                                )
                            }
                        }

                        if (sameCol) {
                            val col = cellsWithDigit.first().column
                            val colPositions = getUnitPositions(1, col).filter { board[it] == SudokuBoard.EMPTY_VALUE }
                            val eliminations = mutableMapOf<CellPosition, Candidates>()

                            for (other in colPositions) {
                                if (other.box != box && candidates[other.index].contains(digit)) {
                                    eliminations[other] = Candidates.of(digit)
                                }
                            }

                            if (eliminations.isNotEmpty()) {
                                return TechniqueStep(
                                    technique = this,
                                    primaryCells = cellsWithDigit.toSet(),
                                    secondaryCells = eliminations.keys,
                                    eliminatedCandidates = eliminations,
                                    explanation = "Digit $digit in Box ${box + 1} is locked into Column ${col + 1}, eliminating it from the rest of the column."
                                )
                            }
                        }
                    }
                }
            }
            return null
        }
    }

    // -------------------------------------------------------------
    // 5. X-WING
    // A digit appears in exactly 2 cells across 2 rows/cols, forming a rectangle.
    // -------------------------------------------------------------
    data object XWing : SolvingTechnique {
        override val name: String = "X-Wing"
        override val weight: Int = 10

        override fun findStep(board: SudokuBoard, candidates: Array<Candidates>): TechniqueStep? {
            // Row-based X-Wing
            for (digit in 1..9) {
                val rowPairs = mutableListOf<Pair<Int, List<CellPosition>>>()

                for (r in 0..8) {
                    val matching = (0..8).map { CellPosition.of(r, it) }
                        .filter { board[it] == SudokuBoard.EMPTY_VALUE && candidates[it.index].contains(digit) }
                    if (matching.size == 2) {
                        rowPairs.add(r to matching)
                    }
                }

                if (rowPairs.size >= 2) {
                    for (i in rowPairs.indices) {
                        for (j in (i + 1) until rowPairs.size) {
                            val (r1, cells1) = rowPairs[i]
                            val (r2, cells2) = rowPairs[j]
                            val c1 = cells1[0].column
                            val c2 = cells1[1].column

                            if (cells2[0].column == c1 && cells2[1].column == c2) {
                                // Found row X-Wing on columns c1 and c2!
                                val eliminations = mutableMapOf<CellPosition, Candidates>()

                                for (r in 0..8) {
                                    if (r != r1 && r != r2) {
                                        val p1 = CellPosition.of(r, c1)
                                        val p2 = CellPosition.of(r, c2)
                                        if (board[p1] == SudokuBoard.EMPTY_VALUE && candidates[p1.index].contains(digit)) {
                                            eliminations[p1] = Candidates.of(digit)
                                        }
                                        if (board[p2] == SudokuBoard.EMPTY_VALUE && candidates[p2.index].contains(digit)) {
                                            eliminations[p2] = Candidates.of(digit)
                                        }
                                    }
                                }

                                if (eliminations.isNotEmpty()) {
                                    return TechniqueStep(
                                        technique = this,
                                        primaryCells = (cells1 + cells2).toSet(),
                                        secondaryCells = eliminations.keys,
                                        eliminatedCandidates = eliminations,
                                        explanation = "X-Wing found for digit $digit in rows ${r1 + 1} and ${r2 + 1}, eliminating $digit from other cells in columns ${c1 + 1} and ${c2 + 1}."
                                    )
                                }
                            }
                        }
                    }
                }
            }
            return null
        }
    }

    companion object {
        fun getUnitPositions(unitType: Int, unitIndex: Int): List<CellPosition> = when (unitType) {
            0 -> (0..8).map { CellPosition.of(unitIndex, it) } // Row
            1 -> (0..8).map { CellPosition.of(it, unitIndex) } // Column
            else -> {                                           // Box
                val startR = (unitIndex / 3) * 3
                val startC = (unitIndex % 3) * 3
                buildList {
                    for (r in 0..2) {
                        for (c in 0..2) {
                            add(CellPosition.of(startR + r, startC + c))
                        }
                    }
                }
            }
        }
    }
}