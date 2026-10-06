package com.sudoku.core.model

/**
 * Core representation of a 9x9 Sudoku Board.
 *
 * Values:
 * - 0: Empty cell
 * - 1..9: Fixed or entered digit
 */
class SudokuBoard private constructor(
    private val grid: IntArray
) {
    constructor() : this(IntArray(CellPosition.TOTAL_CELLS))

    companion object {
        const val EMPTY_VALUE = 0
        const val MIN_VALUE = 1
        const val MAX_VALUE = 9

        /**
         * Creates a board initialized with an existing 81-element integer array.
         */
        fun fromArray(array: IntArray): SudokuBoard {
            require(array.size == CellPosition.TOTAL_CELLS) {
                "Array length must be ${CellPosition.TOTAL_CELLS}, got ${array.size}"
            }
            array.forEach { value ->
                require(value in EMPTY_VALUE..MAX_VALUE) {
                    "Values must be within $EMPTY_VALUE..$MAX_VALUE, got $value"
                }
            }
            return SudokuBoard(array.copyOf())
        }

        /**
         * Parses an 81-character string format (e.g., standard SDK string where '.' or '0' denotes empty).
         */
        fun fromString(representation: String): SudokuBoard {
            val sanitized = representation.filter { !it.isWhitespace() }
            require(sanitized.length == CellPosition.TOTAL_CELLS) {
                "String representation must contain exactly 81 non-whitespace characters, got ${sanitized.length}"
            }
            val array = IntArray(CellPosition.TOTAL_CELLS)
            for (i in sanitized.indices) {
                val ch = sanitized[i]
                array[i] = when (ch) {
                    '.', '0' -> EMPTY_VALUE
                    in '1'..'9' -> ch.digitToInt()
                    else -> throw IllegalArgumentException("Illegal character '$ch' at index $i")
                }
            }
            return SudokuBoard(array)
        }
    }

    operator fun get(position: CellPosition): Int = grid[position.index]
    operator fun get(row: Int, column: Int): Int = grid[row * CellPosition.GRID_SIZE + column]
    operator fun get(index: Int): Int = grid[index]

    fun isEmpty(position: CellPosition): Boolean = get(position) == EMPTY_VALUE
    fun isEmpty(row: Int, column: Int): Boolean = get(row, column) == EMPTY_VALUE

    /**
     * Returns a new mutated copy of this board with the given position updated.
     */
    fun withCell(position: CellPosition, value: Int): SudokuBoard {
        require(value in EMPTY_VALUE..MAX_VALUE) { "Value must be in $EMPTY_VALUE..$MAX_VALUE" }
        val newGrid = grid.copyOf()
        newGrid[position.index] = value
        return SudokuBoard(newGrid)
    }

    /**
     * Mutates the board in place. Used strictly internally for high-throughput solver/generator paths.
     */
    fun setRaw(index: Int, value: Int) {
        grid[index] = value
    }

    fun copy(): SudokuBoard = SudokuBoard(grid.copyOf())

    fun rawValues(): IntArray = grid.copyOf()

    fun getRowValues(row: Int): IntArray {
        require(row in 0..8) { "Invalid row: $row" }
        val result = IntArray(CellPosition.GRID_SIZE)
        val offset = row * CellPosition.GRID_SIZE
        grid.copyInto(result, destinationOffset = 0, startIndex = offset, endIndex = offset + CellPosition.GRID_SIZE)
        return result
    }

    fun getColumnValues(column: Int): IntArray {
        require(column in 0..8) { "Invalid column: $column" }
        val result = IntArray(CellPosition.GRID_SIZE)
        for (r in 0 until CellPosition.GRID_SIZE) {
            result[r] = grid[r * CellPosition.GRID_SIZE + column]
        }
        return result
    }

    fun getBoxValues(box: Int): IntArray {
        require(box in 0..8) { "Invalid box: $box" }
        val result = IntArray(CellPosition.GRID_SIZE)
        val startRow = (box / CellPosition.BOX_SIZE) * CellPosition.BOX_SIZE
        val startCol = (box % CellPosition.BOX_SIZE) * CellPosition.BOX_SIZE
        var ptr = 0
        for (r in 0 until CellPosition.BOX_SIZE) {
            for (c in 0 until CellPosition.BOX_SIZE) {
                result[ptr++] = grid[(startRow + r) * CellPosition.GRID_SIZE + (startCol + c)]
            }
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SudokuBoard) return false
        return grid.contentEquals(other.grid)
    }

    override fun hashCode(): Int = grid.contentHashCode()

    override fun toString(): String {
        return buildString {
            for (r in 0 until CellPosition.GRID_SIZE) {
                if (r > 0 && r % 3 == 0) append("------+-------+------\n")
                for (c in 0 until CellPosition.GRID_SIZE) {
                    if (c > 0 && c % 3 == 0) append("| ")
                    val value = grid[r * CellPosition.GRID_SIZE + c]
                    append(if (value == EMPTY_VALUE) ". " else "$value ")
                }
                append("\n")
            }
        }
    }
}