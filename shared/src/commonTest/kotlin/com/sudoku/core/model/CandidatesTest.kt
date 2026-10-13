package com.sudoku.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CandidatesTest {

    @Test
    fun testEmptyAndAllConstants() {
        assertEquals(0, Candidates.NONE.size)
        assertTrue(Candidates.NONE.isEmpty)

        assertEquals(9, Candidates.ALL.size)
        assertTrue(Candidates.ALL.isNotEmpty)
        for (d in 1..9) {
            assertTrue(d in Candidates.ALL)
        }
    }

    @Test
    fun testOfFactoryAndContains() {
        val candidates = Candidates.of(1, 4, 7, 9)
        assertEquals(4, candidates.size)
        assertTrue(candidates.contains(1))
        assertTrue(candidates.contains(4))
        assertTrue(candidates.contains(7))
        assertTrue(candidates.contains(9))

        assertFalse(candidates.contains(2))
        assertFalse(candidates.contains(5))
        assertFalse(candidates.contains(8))
    }

    @Test
    fun testSingleOrNull() {
        val single = Candidates.of(6)
        assertEquals(1, single.size)
        assertEquals(6, single.singleOrNull())

        val multiple = Candidates.of(2, 8)
        assertNull(multiple.singleOrNull())

        assertNull(Candidates.NONE.singleOrNull())
    }

    @Test
    fun testWithAndWithout() {
        val base = Candidates.of(2, 5)
        val added = base.with(8)
        assertTrue(8 in added)
        assertEquals(3, added.size)

        val removed = added.without(2)
        assertFalse(2 in removed)
        assertEquals(2, removed.size)
    }

    @Test
    fun testSetOperations() {
        val setA = Candidates.of(1, 2, 3, 4)
        val setB = Candidates.of(3, 4, 5, 6)

        val intersection = setA intersect setB
        assertEquals(listOf(3, 4), intersection.toList())

        val union = setA union setB
        assertEquals(listOf(1, 2, 3, 4, 5, 6), union.toList())
    }
}