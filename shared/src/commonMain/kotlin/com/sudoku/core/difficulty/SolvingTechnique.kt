package com.sudoku.core.difficulty

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates
import com.sudoku.core.model.SudokuBoard

sealed interface SolvingTechnique {
    val name: String
    val weight: Int

    fun findStep(board: SudokuBoard, candidates: Array<Candidates>): TechniqueStep?

    // -------------------------------------------------------------
    // 1. NAKED SINGLE
    // Explains exactly which digits in the Row, Col, and Box block this cell.
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

                        // 1. Digits present in this Row
                        val rowDigits = (0..8)
                            .mapNotNull { c -> if (c != pos.column && board[pos.row, c] != 0) board[pos.row, c] else null }
                            .distinct().sorted()

                        // 2. Digits present in this Column
                        val colDigits = (0..8)
                            .mapNotNull { r -> if (r != pos.row && board[r, pos.column] != 0) board[r, pos.column] else null }
                            .distinct().sorted()

                        // 3. Digits present in this 3x3 Box
                        val boxDigits = getUnitPositions(2, pos.box)
                            .mapNotNull { p -> if (p != pos && board[p] != 0) board[p] else null }
                            .distinct().sorted()

                        val allBlocked = (rowDigits + colDigits + boxDigits).distinct().sorted()

                        val explanation = buildString {
                            append("Cell (Row ${pos.row + 1}, Col ${pos.column + 1}) must be $single.\n\n")
                            append("Why? All other digits 1–9 are ruled out:\n")
                            if (rowDigits.isNotEmpty()) {
                                append("• Row ${pos.row + 1} has: ${rowDigits.joinToString(", ")}\n")
                            }
                            if (colDigits.isNotEmpty()) {
                                append("• Column ${pos.column + 1} has: ${colDigits.joinToString(", ")}\n")
                            }
                            if (boxDigits.isNotEmpty()) {
                                append("• Box ${pos.box + 1} has: ${boxDigits.joinToString(", ")}\n")
                            }
                            append("\nTogether, these block: ${allBlocked.joinToString(", ")}.\n")
                            append("The only remaining number is $single.")
                        }

                        return TechniqueStep(
                            technique = this,
                            primaryCells = setOf(pos),
                            placedDigit = pos to single,
                            explanation = explanation
                        )
                    }
                }
            }
            return null
        }
    }

    // -------------------------------------------------------------
    // 2. HIDDEN SINGLE
    // Explains why every other empty cell in this unit cannot contain the digit.
    // -------------------------------------------------------------
    data object HiddenSingle : SolvingTechnique {
        override val name: String = "Hidden Single"
        override val weight: Int = 2

        override fun findStep(board: SudokuBoard, candidates: Array<Candidates>): TechniqueStep? {
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
                val blockedReasons = mutableListOf<String>()

                for (pos in positions) {
                    if (board[pos] == SudokuBoard.EMPTY_VALUE) {
                        if (candidates[pos.index].contains(digit)) {
                            count++
                            targetPos = pos
                        } else {
                            // Find why this other empty cell cannot contain 'digit'
                            val reason = when (unitType) {
                                0 -> { // Row unit: check col and box
                                    val inCol = (0..8).any { r -> board[r, pos.column] == digit }
                                    if (inCol) "Col ${pos.column + 1} already has $digit"
                                    else "Box ${pos.box + 1} already has $digit"
                                }
                                1 -> { // Column unit: check row and box
                                    val inRow = (0..8).any { c -> board[pos.row, c] == digit }
                                    if (inRow) "Row ${pos.row + 1} already has $digit"
                                    else "Box ${pos.box + 1} already has $digit"
                                }
                                else -> { // Box unit: check row and col
                                    val inRow = (0..8).any { c -> board[pos.row, c] == digit }
                                    if (inRow) "Row ${pos.row + 1} already has $digit"
                                    else "Col ${pos.column + 1} already has $digit"
                                }
                            }
                            blockedReasons.add("• (R${pos.row + 1}, C${pos.column + 1}): $reason")
                        }
                    }
                }

                if (count == 1 && targetPos != null) {
                    val unitName = when (unitType) {
                        0 -> "Row ${unitIndex + 1}"
                        1 -> "Column ${unitIndex + 1}"
                        else -> "Box ${unitIndex + 1}"
                    }

                    val explanation = buildString {
                        append("Digit $digit must go in (Row ${targetPos.row + 1}, Col ${targetPos.column + 1}).\n\n")
                        append("Why? In $unitName, every other cell is blocked from having $digit:\n")
                        blockedReasons.take(3).forEach { append("$it\n") }
                        if (blockedReasons.size > 3) append("• Other cells also blocked\n")
                        append("\nTherefore, (R${targetPos.row + 1}, C${targetPos.column + 1}) is the only legal spot left in $unitName.")
                    }

                    return TechniqueStep(
                        technique = this,
                        primaryCells = setOf(targetPos),
                        placedDigit = targetPos to digit,
                        explanation = explanation
                    )
                }
            }
            return null
        }
    }

    // -------------------------------------------------------------
    // 3. NAKED PAIR
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
                                    val unitName = when (unitType) {
                                        0 -> "Row ${unitIndex + 1}"
                                        1 -> "Column ${unitIndex + 1}"
                                        else -> "Box ${unitIndex + 1}"
                                    }

                                    val explanation = buildString {
                                        append("Naked Pair: Digits {${pairDigits.joinToString(", ")}}.\n\n")
                                        append("Cells (R${c1.row + 1}, C${c1.column + 1}) and (R${c2.row + 1}, C${c2.column + 1}) only contain these two digits.\n")
                                        append("Because they must share {${pairDigits.joinToString(", ")}}, no other cell in $unitName can have them.\n")
                                        append("We eliminate {${pairDigits.joinToString(", ")}} from ${eliminations.size} other cell(s).")
                                    }

                                    return TechniqueStep(
                                        technique = this,
                                        primaryCells = setOf(c1, c2),
                                        secondaryCells = eliminations.keys,
                                        eliminatedCandidates = eliminations,
                                        explanation = explanation
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
                                val explanation = buildString {
                                    append("Pointing Pair: Digit $digit in Box ${box + 1}.\n\n")
                                    append("In Box ${box + 1}, candidate $digit only appears inside Row ${row + 1}.\n")
                                    append("Since $digit must be in this box along Row ${row + 1}, it cannot exist anywhere else in Row ${row + 1}.\n")
                                    append("Eliminating $digit from ${eliminations.size} other cell(s) in Row ${row + 1}.")
                                }

                                return TechniqueStep(
                                    technique = this,
                                    primaryCells = cellsWithDigit.toSet(),
                                    secondaryCells = eliminations.keys,
                                    eliminatedCandidates = eliminations,
                                    explanation = explanation
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
                                val explanation = buildString {
                                    append("Pointing Pair: Digit $digit in Box ${box + 1}.\n\n")
                                    append("In Box ${box + 1}, candidate $digit only appears inside Column ${col + 1}.\n")
                                    append("Since $digit must be in this box along Column ${col + 1}, it cannot exist anywhere else in Column ${col + 1}.\n")
                                    append("Eliminating $digit from ${eliminations.size} other cell(s) in Column ${col + 1}.")
                                }

                                return TechniqueStep(
                                    technique = this,
                                    primaryCells = cellsWithDigit.toSet(),
                                    secondaryCells = eliminations.keys,
                                    eliminatedCandidates = eliminations,
                                    explanation = explanation
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
    // -------------------------------------------------------------
    data object XWing : SolvingTechnique {
        override val name: String = "X-Wing"
        override val weight: Int = 10

        override fun findStep(board: SudokuBoard, candidates: Array<Candidates>): TechniqueStep? {
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
                                    val explanation = buildString {
                                        append("X-Wing: Digit $digit forms a rectangle across Rows ${r1 + 1} and ${r2 + 1}.\n\n")
                                        append("Because $digit is locked into Columns ${c1 + 1} and ${c2 + 1} for both rows, no other cells in these columns can contain $digit.\n")
                                        append("Eliminating $digit from ${eliminations.size} other cell(s).")
                                    }

                                    return TechniqueStep(
                                        technique = this,
                                        primaryCells = (cells1 + cells2).toSet(),
                                        secondaryCells = eliminations.keys,
                                        eliminatedCandidates = eliminations,
                                        explanation = explanation
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
            0 -> (0..8).map { CellPosition.of(unitIndex, it) }
            1 -> (0..8).map { CellPosition.of(it, unitIndex) }
            else -> {
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
