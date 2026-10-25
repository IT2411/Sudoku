package com.sudoku.core.candidates

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.SudokuBoard
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CandidateBlockedNotesTest {

    @Test
    fun testBlockedCandidatesDetectsRowColumnAndBoxNumbers() {
        // Place 3 in Row 0, 7 in Col 0, and 9 in Box 0
        val board = SudokuBoard()
            .withCell(CellPosition.of(0, 5), 3)
            .withCell(CellPosition.of(8, 0), 7)
            .withCell(CellPosition.of(1, 1), 9)

        // Query empty target cell (0, 0)
        val target = CellPosition.of(0, 0)
        val blocked = CandidateCalculator.getBlockedCandidates(board, target)

        // 3, 7, and 9 must be flagged as blocked
        assertTrue(blocked.contains(3), "Row number 3 should be blocked")
        assertTrue(blocked.contains(7), "Column number 7 should be blocked")
        assertTrue(blocked.contains(9), "Box number 9 should be blocked")

        // Unplaced numbers (e.g. 1, 2, 4) must NOT be blocked
        assertFalse(blocked.contains(1))
        assertFalse(blocked.contains(2))
        assertFalse(blocked.contains(4))
    }

    @Test
    fun testEmptyBoardHasZeroBlockedCandidates() {
        val board = SudokuBoard()
        val pos = CellPosition.of(4, 4)
        val blocked = CandidateCalculator.getBlockedCandidates(board, pos)

        assertTrue(blocked.isEmpty)
    }
}
