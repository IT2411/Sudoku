package com.sudoku.core.storage

import com.sudoku.core.difficulty.Difficulty
import com.sudoku.core.game.SudokuGame
import com.sudoku.core.generator.GeneratedPuzzle
import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.SudokuBoard

data class GameStateSnapshot(
    val initialBoardRaw: String,
    val solutionRaw: String,
    val currentBoardRaw: String,
    val notesMasks: List<Int>,
    val difficulty: String,
    val elapsedSeconds: Long,
    val mistakes: Int,
    val hintsUsed: Int,
    val status: String,
    val seed: Long = 0L
) {
    /**
     * Serializes to a compact single-line disk format:
     * initial;solution;current;notes;difficulty;seconds;mistakes;hints;status;seed
     */
    fun serialize(): String {
        val notesStr = notesMasks.joinToString(",")
        return "$initialBoardRaw;$solutionRaw;$currentBoardRaw;$notesStr;$difficulty;$elapsedSeconds;$mistakes;$hintsUsed;$status;$seed"
    }

    fun toGame(): SudokuGame {
        val initialBoard = SudokuBoard.fromString(initialBoardRaw)
        val solution = SudokuBoard.fromString(solutionRaw)
        val currentBoard = SudokuBoard.fromString(currentBoardRaw)
        val diff = Difficulty.valueOf(difficulty)

        var clues = 0
        for (i in 0 until CellPosition.TOTAL_CELLS) {
            if (initialBoard[i] != SudokuBoard.EMPTY_VALUE) clues++
        }

        val puzzle = GeneratedPuzzle(
            initialBoard = initialBoard,
            solution = solution,
            seed = seed,
            clueCount = clues
        )

        val game = SudokuGame(puzzle = puzzle, difficulty = diff)

        // Restore numbers placed by player
        for (i in 0 until CellPosition.TOTAL_CELLS) {
            val pos = CellPosition.fromIndex(i)
            val v = currentBoard[pos]
            if (v != initialBoard[pos] && v != SudokuBoard.EMPTY_VALUE) {
                game.selectCell(pos)
                game.enterDigit(v)
            }
        }

        // Restore pencil notes
        if (notesMasks.size == CellPosition.TOTAL_CELLS) {
            for (i in 0 until CellPosition.TOTAL_CELLS) {
                val mask = notesMasks[i]
                if (mask > 0) {
                    val pos = CellPosition.fromIndex(i)
                    game.selectCell(pos)
                    game.isNotesModeActive = true
                    for (d in 1..9) {
                        if ((mask and (1 shl (d - 1))) != 0) {
                            game.enterDigit(d)
                        }
                    }
                    game.isNotesModeActive = false
                }
            }
        }

        game.elapsedTimeSeconds = elapsedSeconds
        game.selectCell(null)
        return game
    }

    companion object {
        fun deserialize(serialized: String): GameStateSnapshot? {
            return try {
                val parts = serialized.split(";")
                if (parts.size < 10) return null
                val notes = parts[3].split(",").mapNotNull { it.toIntOrNull() }
                GameStateSnapshot(
                    initialBoardRaw = parts[0],
                    solutionRaw = parts[1],
                    currentBoardRaw = parts[2],
                    notesMasks = notes,
                    difficulty = parts[4],
                    elapsedSeconds = parts[5].toLongOrNull() ?: 0L,
                    mistakes = parts[6].toIntOrNull() ?: 0,
                    hintsUsed = parts[7].toIntOrNull() ?: 0,
                    status = parts[8],
                    seed = parts[9].toLongOrNull() ?: 0L
                )
            } catch (e: Exception) {
                null
            }
        }

        fun fromGame(game: SudokuGame): GameStateSnapshot {
            val initialRaw = buildString {
                for (i in 0 until CellPosition.TOTAL_CELLS) append(game.puzzle.initialBoard[i])
            }
            val solutionRaw = buildString {
                for (i in 0 until CellPosition.TOTAL_CELLS) append(game.puzzle.solution[i])
            }
            val currentRaw = buildString {
                for (i in 0 until CellPosition.TOTAL_CELLS) append(game.board[i])
            }
            val masks = List(CellPosition.TOTAL_CELLS) { idx ->
                game.getNotes(CellPosition.fromIndex(idx)).mask
            }

            return GameStateSnapshot(
                initialBoardRaw = initialRaw,
                solutionRaw = solutionRaw,
                currentBoardRaw = currentRaw,
                notesMasks = masks,
                difficulty = game.difficulty.name,
                elapsedSeconds = game.elapsedTimeSeconds,
                mistakes = game.mistakes,
                hintsUsed = game.hintsUsed,
                status = game.status.name,
                seed = game.puzzle.seed
            )
        }
    }
}
