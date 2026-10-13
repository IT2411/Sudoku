package com.sudoku.core.interaction

import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates
import com.sudoku.core.model.SudokuBoard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoardSelectionEngineTest {

    /**
     * Requirement Test directly from SRS & SDLC Phase 8 Step 8.5:
     *
     * Given 7 placed at B2, E5, and H8.
     * User taps E5.
     *
     * Expected:
     * - E5: isSelected = true, isPeer = false (own cell), isSameDigit = true
     * - Row 5 & Column E cells: isPeer = true
     * - B2: isSameDigit = true, isPeer = false (Row B / Col 2 NOT highlighted!)
     * - H8: isSameDigit = true, isPeer = false (Row H / Col 8 NOT highlighted!)
     */
    @Test
    fun testExactInteractionRequirementStep8_5() {
        // Translating chess-style notation to 0-indexed coords:
        // B2 -> Row 1, Col 1
        // E5 -> Row 4, Col 4
        // H8 -> Row 7, Col 7
        val posB2 = CellPosition.of(1, 1)
        val posE5 = CellPosition.of(4, 4)
        val posH8 = CellPosition.of(7, 7)

        val board = SudokuBoard()
            .withCell(posB2, 7)
            .withCell(posE5, 7)
            .withCell(posH8, 7)

        val notesMap = mutableMapOf<CellPosition, Candidates>()
        // Empty cell at (4, 0) has a note '7'
        val notePos = CellPosition.of(4, 0)
        notesMap[notePos] = Candidates.of(7, 2)

        // User taps E5
        val highlights = BoardSelectionEngine.computeHighlights(
            board = board,
            notes = { notesMap[it] ?: Candidates.NONE },
            selectedCell = posE5
        )

        // 1. Check selected cell E5
        val e5State = highlights[posE5.index]
        assertTrue(e5State.isSelected)
        assertTrue(e5State.isSameDigit)
        assertFalse(e5State.isPeer) // The cell itself is selected, not its own peer

        // 2. Check peer cells in Row 4 (Row 5) and Col 4 (Col E)
        val peerInRow = CellPosition.of(4, 8)
        assertTrue(highlights[peerInRow.index].isPeer)

        val peerInCol = CellPosition.of(0, 4)
        assertTrue(highlights[peerInCol.index].isPeer)

        // 3. Check B2 (Row 1, Col 1)
        val b2State = highlights[posB2.index]
        assertTrue(b2State.isSameDigit)  // Digit '7' matches!
        assertFalse(b2State.isSelected)
        assertFalse(b2State.isPeer)      // Row 1 / Col 1 must NOT be highlighted as peer

        // Check cells in B2's row/col (e.g. Row 1, Col 8) -> MUST NOT BE PEER
        val b2RowCell = CellPosition.of(1, 8)
        assertFalse(highlights[b2RowCell.index].isPeer)

        // 4. Check H8 (Row 7, Col 7)
        val h8State = highlights[posH8.index]
        assertTrue(h8State.isSameDigit)  // Digit '7' matches!
        assertFalse(h8State.isSelected)
        assertFalse(h8State.isPeer)      // Row 7 / Col 7 must NOT be highlighted as peer

        // Check cells in H8's row/col (e.g. Row 7, Col 0) -> MUST NOT BE PEER
        val h8RowCell = CellPosition.of(7, 0)
        assertFalse(highlights[h8RowCell.index].isPeer)

        // 5. Check note highlighting
        val noteCellState = highlights[notePos.index]
        assertTrue(noteCellState.hasMatchingNote) // Contains note '7'
        assertTrue(noteCellState.isPeer)          // Also happens to be in Row 4
    }

    @Test
    fun testConflictFlaggingInHighlights() {
        // Place two 5s in Row 0 (Position 0,0 and 0,7)
        val p1 = CellPosition.of(0, 0)
        val p2 = CellPosition.of(0, 7)

        val board = SudokuBoard()
            .withCell(p1, 5)
            .withCell(p2, 5)

        val highlights = BoardSelectionEngine.computeHighlights(
            board = board,
            notes = { Candidates.NONE },
            selectedCell = p1
        )

        assertTrue(highlights[p1.index].hasConflict)
        assertTrue(highlights[p2.index].hasConflict)
    }

    @Test
    fun testNoSelectionResultsInDefaultStates() {
        val board = SudokuBoard().withCell(CellPosition.of(0, 0), 1)

        val highlights = BoardSelectionEngine.computeHighlights(
            board = board,
            notes = { Candidates.NONE },
            selectedCell = null
        )

        for (state in highlights) {
            assertFalse(state.isSelected)
            assertFalse(state.isPeer)
            assertFalse(state.isSameDigit)
        }
    }
}