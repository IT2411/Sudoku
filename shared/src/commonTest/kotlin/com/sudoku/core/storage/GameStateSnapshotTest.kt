package com.sudoku.core.storage

import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.game.SudokuGame
import com.sudoku.core.generator.SudokuGenerator
import com.sudoku.core.model.CellPosition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GameStateSnapshotTest {

    @Test
    fun testSerializationRoundtripPreservesFullState() {
        val puzzle = SudokuGenerator.generate(targetClues = 32, seed = 12345L)
        val game = SudokuGame(puzzle, difficulty = Difficulty.HARD)

        // Place a number and add pencil notes
        val emptyPos1 = CellPosition.allPositions.first { !puzzle.givenPositions.contains(it) }
        game.selectCell(emptyPos1)
        game.enterDigit(puzzle.solution[emptyPos1])

        val emptyPos2 = CellPosition.allPositions.last { !puzzle.givenPositions.contains(it) && it != emptyPos1 }
        game.selectCell(emptyPos2)
        game.isNotesModeActive = true
        game.enterDigit(4)
        game.enterDigit(9)
        game.isNotesModeActive = false

        game.elapsedTimeSeconds = 345L

        // Serialize to snapshot
        val snapshot = GameStateSnapshot.fromGame(game)
        val serializedString = snapshot.serialize()

        // Deserialize back
        val restoredSnapshot = GameStateSnapshot.deserialize(serializedString)
        assertNotNull(restoredSnapshot)

        val restoredGame = restoredSnapshot.toGame()

        assertEquals(game.board, restoredGame.board)
        assertEquals(game.difficulty, restoredGame.difficulty)
        assertEquals(game.elapsedTimeSeconds, restoredGame.elapsedTimeSeconds)
        assertEquals(listOf(4, 9), restoredGame.getNotes(emptyPos2).toList())
    }
}
