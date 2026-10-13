package com.sudoku.core.game

import com.sudoku.core.candidates.CandidateCalculator
import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.difficulty.SolvingTechnique
import com.sudoku.core.difficulty.TechniqueStep
import com.sudoku.core.generator.GeneratedPuzzle
import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates
import com.sudoku.core.model.SudokuBoard
import com.sudoku.core.validator.SudokuValidator

class SudokuGame(
    val puzzle: GeneratedPuzzle,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val maxMistakes: Int = 3
) {
    var board: SudokuBoard = puzzle.initialBoard.copy()
        private set

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

    val completedDigits: Set<Int>
        get() = buildSet {
            for (digit in 1..9) {
                var count = 0
                for (i in 0 until CellPosition.TOTAL_CELLS) {
                    val pos = CellPosition.fromIndex(i)
                    if (board[pos] == digit && puzzle.solution[pos] == digit) {
                        count++
                    }
                }
                if (count == 9) add(digit)
            }
        }

    fun checkNewlyCompletedUnits(pos: CellPosition): Set<CellPosition> {
        val animatingCells = mutableSetOf<CellPosition>()

        var rowComplete = true
        for (c in 0..8) {
            val p = CellPosition.of(pos.row, c)
            if (board[p] != puzzle.solution[p]) {
                rowComplete = false
                break
            }
        }
        if (rowComplete) {
            for (c in 0..8) animatingCells.add(CellPosition.of(pos.row, c))
        }

        var colComplete = true
        for (r in 0..8) {
            val p = CellPosition.of(r, pos.column)
            if (board[p] != puzzle.solution[p]) {
                colComplete = false
                break
            }
        }
        if (colComplete) {
            for (r in 0..8) animatingCells.add(CellPosition.of(r, pos.column))
        }

        var boxComplete = true
        val startR = (pos.row / 3) * 3
        val startC = (pos.column / 3) * 3
        for (r in 0..2) {
            for (c in 0..2) {
                val p = CellPosition.of(startR + r, startC + c)
                if (board[p] != puzzle.solution[p]) {
                    boxComplete = false
                    break
                }
            }
        }
        if (boxComplete) {
            for (r in 0..2) {
                for (c in 0..2) animatingCells.add(CellPosition.of(startR + r, startC + c))
            }
        }

        return animatingCells
    }

    fun enterDigit(digit: Int): Boolean {
        require(digit in 1..9) { "Digit must be 1..9, got $digit" }
        val pos = selectedCell ?: return false
        if (status != GameStatus.PLAYING) return false
        if (puzzle.givenPositions.contains(pos)) return false
        if (completedDigits.contains(digit)) return false

        return if (isNotesModeActive) {
            toggleNote(pos, digit)
        } else {
            placeDigit(pos, digit)
        }
    }

    private fun toggleNote(pos: CellPosition, digit: Int): Boolean {
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
        if (prevVal == digit) return false

        val isMistake = puzzle.solution[pos] != digit
        if (isMistake) {
            mistakes++
            if (maxMistakes in 1..mistakes) {
                status = GameStatus.GAME_OVER
            }
        }

        val oldNotes = notesGrid[pos.index]
        val secondaryEliminations = mutableMapOf<CellPosition, Candidates>()

        board = board.withCell(pos, digit)
        notesGrid[pos.index] = Candidates.NONE

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

        board = board.withCell(move.position, move.previousValue)
        notesGrid[move.position.index] = move.previousCellNotes

        for ((peerPos, restored) in move.secondaryNotesEliminated) {
            val cur = notesGrid[peerPos.index]
            notesGrid[peerPos.index] = cur union restored
        }

        if (move.wasMistake && mistakes > 0) mistakes--

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

        if (move.wasMistake) mistakes++

        selectedCell = move.position
        checkVictory()
        return true
    }

    fun findNextHint(): TechniqueStep? {
        val currentCandidates = CandidateCalculator.calculateAllCandidates(board)
        val techniques = listOf(
            SolvingTechnique.NakedSingle,
            SolvingTechnique.HiddenSingle,
            SolvingTechnique.NakedPair,
            SolvingTechnique.PointingPair,
            SolvingTechnique.XWing
        )

        for (tech in techniques) {
            val step = tech.findStep(board, currentCandidates)
            if (step != null) return step
        }
        return null
    }

    fun applyHint(step: TechniqueStep): Boolean {
        hintsUsed++
        if (step.placedDigit != null) {
            val (pos, digit) = step.placedDigit
            selectCell(pos)
            isNotesModeActive = false
            return placeDigit(pos, digit)
        } else if (step.eliminatedCandidates.isNotEmpty()) {
            for ((pos, elim) in step.eliminatedCandidates) {
                val cur = notesGrid[pos.index]
                notesGrid[pos.index] = Candidates.fromMask(cur.mask and elim.mask.inv())
            }
            return true
        }
        return false
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
