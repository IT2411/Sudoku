package com.sudoku.core.game

import com.sudoku.core.generator.GeneratedPuzzle
import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates
import com.sudoku.core.model.SudokuBoard
import com.sudoku.core.validator.SudokuValidator

class SudokuGame(
    val puzzle: GeneratedPuzzle,
    val maxMistakes: Int = 3 // Standard rules (set to -1 for unlimited)
) {
    var board: SudokuBoard = puzzle.initialBoard.copy()
        private set

    // 81 elements representing pencil marks per cell
    private val notesGrid = Array(CellPosition.TOTAL_CELLS) { Candidates.NONE }

    val undoManager = UndoManager()

    var status: GameStatus = GameStatus.PLAYING
        private set

    var selectedCell: CellPosition? = null
        private set

    var isNotesModeActive: Boolean = false
    var mistakes: Int = 0
        private set
    var hintsUsed: Int = 0
        private set
    var elapsedTimeSeconds: Long = 0L

    fun getNotes(position: CellPosition): Candidates = notesGrid[position.index]

    fun selectCell(position: CellPosition?) {
        selectedCell = position
    }

    /**
     * Enters a number into the selected cell.
     * If [isNotesModeActive] is true, toggles the pencil note.
     * If false, places the digit on the board.
     */
    fun enterDigit(digit: Int): Boolean {
        require(digit in 1..9) { "Digit must be 1..9, got $digit" }
        val pos = selectedCell ?: return false
        if (status != GameStatus.PLAYING) return false
        if (puzzle.givenPositions.contains(pos)) return false // Clues are immutable

        return if (isNotesModeActive) {
            toggleNote(pos, digit)
        } else {
            placeDigit(pos, digit)
        }
    }

    private fun toggleNote(pos: CellPosition, digit: Int): Boolean {
        // Can only add notes to empty cells
        if (board[pos] != SudokuBoard.EMPTY_VALUE) return false

        val oldNotes = notesGrid[pos.index]
        val newNotes = if (oldNotes.contains(digit)) oldNotes.without(digit) else oldNotes.with(digit)
        notesGrid[pos.index] = newNotes

        undoManager.recordMove(
            Move(
                position = pos,
                previousValue = SudokuBoard.EMPTY_VALUE,
                newValue = SudokuBoard.EMPTY_VALUE,
                previousCellNotes = oldNotes,
                newCellNotes = newNotes
            )
        )
        return true
    }

    private fun placeDigit(pos: CellPosition, digit: Int): Boolean {
        val prevVal = board[pos]
        if (prevVal == digit) return false // No-op

        val isMistake = puzzle.solution[pos] != digit
        if (isMistake) {
            mistakes++
            if (maxMistakes in 1..mistakes) {
                status = GameStatus.GAME_OVER
            }
        }

        val oldNotes = notesGrid[pos.index]
        val secondaryEliminations = mutableMapOf<CellPosition, Candidates>()

        // Update board
        board = board.withCell(pos, digit)
        notesGrid[pos.index] = Candidates.NONE // Clear own notes on digit placement

        // Auto-clear matching note in peer cells (row, col, box)
        for (idx in 0 until CellPosition.TOTAL_CELLS) {
            val peerPos = CellPosition.fromIndex(idx)
            if (peerPos != pos && (peerPos.row == pos.row || peerPos.column == pos.column || peerPos.box == pos.box)) {
                val peerNotes = notesGrid[idx]
                if (peerNotes.contains(digit)) {
                    secondaryEliminations[peerPos] = Candidates.of(digit)
                    notesGrid[idx] = peerNotes.without(digit)
                }
            }
        }

        undoManager.recordMove(
            Move(
                position = pos,
                previousValue = prevVal,
                newValue = digit,
                previousCellNotes = oldNotes,
                newCellNotes = Candidates.NONE,
                secondaryNotesEliminated = secondaryEliminations,
                wasMistake = isMistake
            )
        )

        checkVictory()
        return true
    }

    /**
     * Erases either placed value or notes in the selected cell.
     */
    fun erase(): Boolean {
        val pos = selectedCell ?: return false
        if (status != GameStatus.PLAYING) return false
        if (puzzle.givenPositions.contains(pos)) return false

        val prevVal = board[pos]
        val prevNotes = notesGrid[pos.index]

        if (prevVal == SudokuBoard.EMPTY_VALUE && prevNotes.isEmpty) return false

        board = board.withCell(pos, SudokuBoard.EMPTY_VALUE)
        notesGrid[pos.index] = Candidates.NONE

        undoManager.recordMove(
            Move(
                position = pos,
                previousValue = prevVal,
                newValue = SudokuBoard.EMPTY_VALUE,
                previousCellNotes = prevNotes,
                newCellNotes = Candidates.NONE
            )
        )
        return true
    }

    fun undo(): Boolean {
        if (status != GameStatus.PLAYING) return false
        val move = undoManager.undo() ?: return false

        // Revert cell value and notes
        board = board.withCell(move.position, move.previousValue)
        notesGrid[move.position.index] = move.previousCellNotes

        // Restore any eliminated notes in peer cells
        for ((peerPos, restored) in move.secondaryNotesEliminated) {
            val cur = notesGrid[peerPos.index]
            notesGrid[peerPos.index] = cur union restored
        }

        if (move.wasMistake && mistakes > 0) {
            mistakes--
        }

        selectedCell = move.position
        return true
    }

    fun redo(): Boolean {
        if (status != GameStatus.PLAYING) return false
        val move = undoManager.redo() ?: return false

        board = board.withCell(move.position, move.newValue)
        notesGrid[move.position.index] = move.newCellNotes

        for ((peerPos, eliminated) in move.secondaryNotesEliminated) {
            val cur = notesGrid[peerPos.index]
            notesGrid[peerPos.index] = Candidates.fromMask(cur.mask and eliminated.mask.inv())
        }

        if (move.wasMistake) {
            mistakes++
        }

        selectedCell = move.position
        checkVictory()
        return true
    }

    fun incrementHint() {
        hintsUsed++
    }

    fun pause() {
        if (status == GameStatus.PLAYING) status = GameStatus.PAUSED
    }

    fun resume() {
        if (status == GameStatus.PAUSED) status = GameStatus.PLAYING
    }

    private fun checkVictory() {
        if (SudokuValidator.isBoardSolved(board)) {
            status = GameStatus.COMPLETED
        }
    }
}