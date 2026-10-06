package com.sudoku.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SudokuBoardTest {

    private val sampleRaw = "" +
            "530070000" +
            "600195000" +
            "098000060" +
            "800060003" +
            "400803001" +
            "700020006" +
            "060000280" +
            "000419005" +
            "000080079"

    @Test
    fun testBoardInitializationAndParsing() {
        val board = SudokuBoard.fromString(sampleRaw)
        assertEquals(5, board[0, 0])
        assertEquals(3, board[0, 1])
        assertEquals(SudokuBoard.EMPTY_VALUE, board[0, 2])
        assertEquals(9, board[8, 8])
    }

    @Test
    fun testImmutableCopyOnWithCell() {
        val board = SudokuBoard()
        val pos = CellPosition.of(3, 4)
        val updated = board.withCell(pos, 7)

        assertEquals(0, board[pos])
        assertEquals(7, updated[pos])
    }

    @Test
    fun testGetBoxValues() {
        val board = SudokuBoard.fromString(sampleRaw)
        // Top-left box 0:
        // 5 3 0
        // 6 0 0
        // 0 9 8
        val box0 = board.getBoxValues(0)
        val expected = intArrayOf(5, 3, 0, 6, 0, 0, 0, 9, 8)
        assertTrue(box0.contentEquals(expected))
    }

    @Test
    fun testInvalidStringLengthThrowsException() {
        assertFailsWith<IllegalArgumentException> {
            SudokuBoard.fromString("12345")
        }
    }
}