package com.sudoku.core.model

import kotlin.jvm.JvmInline

/**
 * High-performance, zero-allocation candidate set representation backed by a 9-bit bitmask.
 *
 * Bit mappings:
 * - Digit 1 -> bit 0 (1 shl 0 = 1)
 * - Digit 2 -> bit 1 (1 shl 1 = 2)
 * ...
 * - Digit 9 -> bit 8 (1 shl 8 = 256)
 */
@JvmInline
value class Candidates(val mask: Int) {

    init {
        require(mask in 0..ALL_MASK) { "Candidates mask out of bounds: $mask" }
    }

    /**
     * Number of active candidate digits.
     */
    val size: Int get() = mask.countOneBits()

    val isEmpty: Boolean get() = mask == 0
    val isNotEmpty: Boolean get() = mask != 0

    /**
     * Checks if a specific digit (1..9) is present in this candidate set.
     */
    operator fun contains(digit: Int): Boolean {
        if (digit !in 1..9) return false
        return (mask and (1 shl (digit - 1))) != 0
    }

    /**
     * Returns a new Candidates set with the specified digit included.
     */
    fun with(digit: Int): Candidates {
        require(digit in 1..9) { "Digit must be 1..9, got $digit" }
        return Candidates(mask or (1 shl (digit - 1)))
    }

    /**
     * Returns a new Candidates set with the specified digit removed.
     */
    fun without(digit: Int): Candidates {
        if (digit !in 1..9) return this
        return Candidates(mask and (1 shl (digit - 1)).inv())
    }

    /**
     * Intersection: returns candidates present in both sets.
     */
    infix fun intersect(other: Candidates): Candidates = Candidates(this.mask and other.mask)

    /**
     * Union: returns candidates present in either set.
     */
    infix fun union(other: Candidates): Candidates = Candidates(this.mask or other.mask)

    /**
     * Returns the single digit if size == 1, or null if size is 0 or > 1.
     * Crucial for detecting Naked Singles.
     */
    fun singleOrNull(): Int? {
        if (size != 1) return null
        return mask.countTrailingZeroBits() + 1
    }

    /**
     * Converts to an ordered list of digits (e.g., [1, 4, 9]).
     */
    fun toList(): List<Int> {
        if (isEmpty) return emptyList()
        val result = ArrayList<Int>(size)
        for (digit in 1..9) {
            if (contains(digit)) {
                result.add(digit)
            }
        }
        return result
    }

    override fun toString(): String = toList().joinToString(prefix = "{", postfix = "}")

    companion object {
        const val ALL_MASK: Int = 0x1FF // 511 (bits 0..8 set)
        
        val NONE = Candidates(0)
        val ALL = Candidates(ALL_MASK)

        fun of(vararg digits: Int): Candidates {
            var m = 0
            for (d in digits) {
                require(d in 1..9) { "Digit must be in 1..9, got $d" }
                m = m or (1 shl (d - 1))
            }
            return Candidates(m)
        }

        fun fromMask(mask: Int): Candidates = Candidates(mask)
    }
}