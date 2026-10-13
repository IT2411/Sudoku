package com.sudoku.core.game

import com.sudoku.core.generator.SudokuGenerator
import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.SudokuBoard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SudokuGameTest {

    private fun createTestGame(): SudokuGame {
        // Generate deterministic puzzle
        val puzzle = SudokuGenerator.generate(targetClues = 34, seed = 42L)
        return SudokuGame(puzzle, maxMistakes = 3)
    }

    @Test
    fun testCannotModifyGivenClues() {
        val game = createTestGame()
        val givenPos = game.puzzle.givenPositions.first()

        game.selectCell(givenPos)
        val changed = game.enterDigit(1)

        assertFalse(changed)
        assertEquals(game.puzzle.initialBoard[givenPos], game.board[givenPos])
    }

    @Test
    fun testPlacingValidNumberClearsNotesAndTriggersUndo() {
        val game = createTestGame()
        // Find empty cell
        val emptyPos = CellPosition.allPositions.first { !game.puzzle.givenPositions.contains(it) }
        game.selectCell(emptyPos)

        // 1. Add notes
        game.isNotesModeActive = true
        game.enterDigit(3)
        game.enterDigit(7)
        assertEquals(listOf(3, 7), game.getNotes(emptyPos).toList())

        // 2. Place actual number
        game.isNotesModeActive = false
        val correctValue = game.puzzle.solution[emptyPos]
        game.enterDigit(correctValue)

        assertEquals(correctValue, game.board[emptyPos])
        assertTrue(game.getNotes(emptyPos).isEmpty) // Notes on this cell should be cleared

        // 3. Undo should restore notes and previous value
        assertTrue(game.undoManager.canUndo)
        game.undo()

        assertEquals(SudokuBoard.EMPTY_VALUE, game.board[emptyPos])
        assertEquals(listOf(3, 7), game.getNotes(emptyPos).toList())

        // 4. Redo should re-apply number and re-clear notes
        assertTrue(game.undoManager.canRedo)
        game.redo()

        assertEquals(correctValue, game.board[emptyPos])
        assertTrue(game.getNotes(emptyPos).isEmpty)
    }

    @Test
    fun testPeerNotesAutoCleanedOnNumberPlacement() {
        val game = createTestGame()
        val emptyPos1 = CellPosition.of(0, 0)
        val emptyPos2 = CellPosition.of(0, 5) // Same row

        // Setup notes if both are not givens
        if (!game.puzzle.givenPositions.contains(emptyPos1) && !game.puzzle.givenPositions.contains(emptyPos2)) {
            game.isNotesModeActive = true
            game.selectCell(emptyPos2)
            game.enterDigit(4)
            assertTrue(game.getNotes(emptyPos2).contains(4))

            // Now place 4 at emptyPos1
            game.isNotesModeActive = false
            game.selectCell(emptyPos1)
            game.enterDigit(4)

            // Note at emptyPos2 must be auto-removed because same row
            assertFalse(game.getNotes(emptyPos2).contains(4))

            // Undo must restore the note at emptyPos2!
            game.undo()
            assertTrue(game.getNotes(emptyPos2).contains(4))
        }
    }

    @Test
    fun testMistakesCountAndGameOver() {
        val game = createTestGame()
        val emptyPos = CellPosition.allPositions.first { !game.puzzle.givenPositions.contains(it) }
        val wrongDigit = if (game.puzzle.solution[emptyPos] == 1) 2 else 1

        game.selectCell(emptyPos)
        assertEquals(0, game.mistakes)

        // Commit 3 mistakes
        game.enterDigit(wrongDigit)
        assertEquals(1, game.mistakes)

        game.erase()
        game.enterDigit(wrongDigit)
        assertEquals(2, game.mistakes)

        game.erase()
        game.enterDigit(wrongDigit)
        assertEquals(3, game.mistakes)
        assertEquals(GameStatus.GAME_OVER, game.status)
    }

    @Test
    fun testCompletionStatusOnSolve() {
        val game = createTestGame()

        // Fill all empty cells with their known solutions
        for (pos in CellPosition.allPositions) {
            if (!game.puzzle.givenPositions.contains(pos)) {
                game.selectCell(pos)
                game.enterDigit(game.puzzle.solution[pos])
            }
        }

        assertEquals(GameStatus.COMPLETED, game.status)
    }
}