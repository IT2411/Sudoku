package com.sudoku.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class CellPositionTest {

    @Test
    fun testPositionCalculations() {
        val p = CellPosition.of(4, 7)
        assertEquals(4, p.row)
        assertEquals(7, p.column)
        assertEquals(43, p.index) // 4 * 9 + 7
        assertEquals(5, p.box)   // (4/3)*3 + (7/3) = 1*3 + 2 = 5
    }

    @Test
    fun testBoxMappingCorners() {
        assertEquals(0, CellPosition.of(0, 0).box)
        assertEquals(2, CellPosition.of(0, 8).box)
        assertEquals(6, CellPosition.of(8, 0).box)
        assertEquals(8, CellPosition.of(8, 8).box)
        assertEquals(4, CellPosition.of(4, 4).box) // Center
    }

    @Test
    fun testPoolReturnsCachedInstances() {
        val p1 = CellPosition.of(2, 3)
        val p2 = CellPosition.fromIndex(21) // 2 * 9 + 3
        assertSame(p1, p2)
    }

    @Test
    fun testInvalidCoordinatesThrowException() {
        assertFailsWith<IllegalArgumentException> { CellPosition(-1, 0) }
        assertFailsWith<IllegalArgumentException> { CellPosition(0, 9) }
        assertFailsWith<IllegalArgumentException> { CellPosition.fromIndex(81) }
    }
}