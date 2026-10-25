package com.sudoku.core.tutorial

import com.sudoku.core.difficulty.SolvingTechnique
import com.sudoku.core.difficulty.TechniqueStep
import com.sudoku.core.game.SudokuGame
import com.sudoku.core.generator.GeneratedPuzzle
import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.SudokuBoard

object DemoBoards {

    data class DemoSetup(
        val technique: SolvingTechnique,
        val title: String,
        val subtitle: String,
        val explanation: String,
        val game: SudokuGame,
        val hintStep: TechniqueStep
    )

    fun getDemoList(technique: SolvingTechnique): List<DemoSetup> {
        return when (technique) {
            SolvingTechnique.NakedSingle -> listOf(
                createNakedSingleCenter(),
                createNakedSingleCorner()
            )
            SolvingTechnique.HiddenSingle -> listOf(
                createHiddenSingleRow(),
                createHiddenSingleColumn()
            )
            SolvingTechnique.NakedPair -> listOf(
                createNakedPairRow(),
                createNakedPairBox()
            )
            SolvingTechnique.PointingPair -> listOf(
                createPointingPairHorizontal(),
                createPointingPairVertical()
            )
            SolvingTechnique.XWing -> listOf(
                createXWingDemoA(),
                createXWingDemoB()
            )
        }
    }

    // 1. Naked Single Center
    private fun createNakedSingleCenter(): DemoSetup {
        val board = SudokuBoard()
            .withCell(CellPosition.of(4, 0), 1)
            .withCell(CellPosition.of(4, 1), 2)
            .withCell(CellPosition.of(4, 2), 3)
            .withCell(CellPosition.of(0, 4), 4)
            .withCell(CellPosition.of(1, 4), 5)
            .withCell(CellPosition.of(2, 4), 6)
            .withCell(CellPosition.of(3, 3), 7)
            .withCell(CellPosition.of(5, 5), 9)

        val pos = CellPosition.of(4, 4)
        val game = SudokuGame(
            puzzle = GeneratedPuzzle(board, board.withCell(pos, 8), 0L, 8)
        )
        val step = TechniqueStep(
            technique = SolvingTechnique.NakedSingle,
            primaryCells = setOf(pos),
            placedDigit = pos to 8,
            explanation = "Center Cell (R5, C5) is empty.\nRow 5 has {1, 2, 3}. Column 5 has {4, 5, 6}. Box 5 has {7, 9}.\nAll numbers 1–9 except 8 are present. 8 is the sole naked single!"
        )
        return DemoSetup(
            technique = SolvingTechnique.NakedSingle,
            title = "Naked Single",
            subtitle = "Example 1: Center Cell Focus",
            explanation = "Look at the highlighted cell. All other digits 1–9 are already visible in its row, column, and box.",
            game = game,
            hintStep = step
        )
    }

    // 2. Naked Single Corner
    private fun createNakedSingleCorner(): DemoSetup {
        val board = SudokuBoard()
            .withCell(CellPosition.of(0, 1), 1)
            .withCell(CellPosition.of(0, 2), 2)
            .withCell(CellPosition.of(0, 5), 3)
            .withCell(CellPosition.of(0, 7), 4)
            .withCell(CellPosition.of(2, 0), 5)
            .withCell(CellPosition.of(4, 0), 6)
            .withCell(CellPosition.of(6, 0), 7)
            .withCell(CellPosition.of(1, 1), 8)

        val pos = CellPosition.of(0, 0)
        val game = SudokuGame(
            puzzle = GeneratedPuzzle(board, board.withCell(pos, 9), 0L, 8)
        )
        val step = TechniqueStep(
            technique = SolvingTechnique.NakedSingle,
            primaryCells = setOf(pos),
            placedDigit = pos to 9,
            explanation = "Top-Left Corner (R1, C1) is empty.\nRow 1 has {1, 2, 3, 4}. Column 1 has {5, 6, 7}. Box 1 has {8}.\nTogether they block 1, 2, 3, 4, 5, 6, 7, 8. Cell must be 9!"
        )
        return DemoSetup(
            technique = SolvingTechnique.NakedSingle,
            title = "Naked Single",
            subtitle = "Example 2: Corner Intersection",
            explanation = "Notice how the intersection of Row 1, Column 1, and Box 1 rules out 8 distinct numbers.",
            game = game,
            hintStep = step
        )
    }

    // 3. Hidden Single in Row
    private fun createHiddenSingleRow(): DemoSetup {
        val board = SudokuBoard()
            .withCell(CellPosition.of(0, 1), 7)
            .withCell(CellPosition.of(8, 7), 7)
            .withCell(CellPosition.of(3, 0), 1)
            .withCell(CellPosition.of(3, 2), 2)
            .withCell(CellPosition.of(3, 3), 3)
            .withCell(CellPosition.of(3, 4), 4)
            .withCell(CellPosition.of(3, 6), 5)
            .withCell(CellPosition.of(3, 8), 6)

        val target = CellPosition.of(3, 5)
        val game = SudokuGame(
            puzzle = GeneratedPuzzle(board, board.withCell(target, 7), 0L, 8)
        )
        val step = TechniqueStep(
            technique = SolvingTechnique.HiddenSingle,
            primaryCells = setOf(target),
            placedDigit = target to 7,
            explanation = "Row 4 needs a 7.\n• (R4, C2) is blocked by 7 in Col 2.\n• (R4, C8) is blocked by 7 in Col 8.\nTherefore, (R4, C6) is the only place left for 7!"
        )
        return DemoSetup(
            technique = SolvingTechnique.HiddenSingle,
            title = "Hidden Single",
            subtitle = "Example 1: Row Isolation",
            explanation = "7 can only be placed in one remaining cell along Row 4 because the others are blocked by column numbers.",
            game = game,
            hintStep = step
        )
    }

    // 4. Hidden Single in Column
    private fun createHiddenSingleColumn(): DemoSetup {
        val board = SudokuBoard()
            .withCell(CellPosition.of(1, 0), 4)
            .withCell(CellPosition.of(7, 8), 4)
            .withCell(CellPosition.of(0, 2), 1)
            .withCell(CellPosition.of(2, 2), 2)
            .withCell(CellPosition.of(3, 2), 3)
            .withCell(CellPosition.of(5, 2), 5)
            .withCell(CellPosition.of(6, 2), 6)
            .withCell(CellPosition.of(8, 2), 8)

        val target = CellPosition.of(4, 2)
        val game = SudokuGame(
            puzzle = GeneratedPuzzle(board, board.withCell(target, 4), 0L, 8)
        )
        val step = TechniqueStep(
            technique = SolvingTechnique.HiddenSingle,
            primaryCells = setOf(target),
            placedDigit = target to 4,
            explanation = "Column 3 needs digit 4.\n• (R2, C3) is blocked by 4 in Row 2.\n• (R8, C3) is blocked by 4 in Row 8.\nTherefore, (R5, C3) is the only place left for 4 in Column 3!"
        )
        return DemoSetup(
            technique = SolvingTechnique.HiddenSingle,
            title = "Hidden Single",
            subtitle = "Example 2: Column Isolation",
            explanation = "Scanning Column 3 reveals that 4 cannot fit in any other empty cell.",
            game = game,
            hintStep = step
        )
    }

    // 5. Naked Pair Row
    private fun createNakedPairRow(): DemoSetup {
        val board = SudokuBoard()
            .withCell(CellPosition.of(0, 2), 1)
            .withCell(CellPosition.of(0, 3), 2)
            .withCell(CellPosition.of(0, 4), 4)
            .withCell(CellPosition.of(0, 5), 5)
            .withCell(CellPosition.of(0, 6), 6)
            .withCell(CellPosition.of(0, 7), 8)
            .withCell(CellPosition.of(0, 8), 9)

        val c1 = CellPosition.of(0, 0)
        val c2 = CellPosition.of(0, 1)

        val game = SudokuGame(puzzle = GeneratedPuzzle(board, board.withCell(c1, 3).withCell(c2, 7), 0L, 7))
        game.selectCell(c1); game.isNotesModeActive = true; game.enterDigit(3); game.enterDigit(7)
        game.selectCell(c2); game.enterDigit(3); game.enterDigit(7)
        game.isNotesModeActive = false; game.selectCell(null)

        val step = TechniqueStep(
            technique = SolvingTechnique.NakedPair,
            primaryCells = setOf(c1, c2),
            explanation = "Cells (R1, C1) and (R1, C2) both only contain candidates {3, 7}.\nSince they must hold 3 and 7 between them, 3 and 7 are eliminated from any other cell in Row 1."
        )
        return DemoSetup(
            technique = SolvingTechnique.NakedPair,
            title = "Naked Pair",
            subtitle = "Example 1: Horizontal Lock",
            explanation = "Two cells in Row 1 only have candidates 3 and 7. Those two numbers belong strictly to those two cells.",
            game = game,
            hintStep = step
        )
    }

    // 6. Naked Pair Box
    private fun createNakedPairBox(): DemoSetup {
        val board = SudokuBoard()
            .withCell(CellPosition.of(0, 0), 1)
            .withCell(CellPosition.of(0, 1), 2)
            .withCell(CellPosition.of(0, 2), 3)
            .withCell(CellPosition.of(1, 0), 4)
            .withCell(CellPosition.of(1, 1), 5)
            .withCell(CellPosition.of(1, 2), 6)
            .withCell(CellPosition.of(2, 0), 9)

        val c1 = CellPosition.of(2, 1)
        val c2 = CellPosition.of(2, 2)

        val game = SudokuGame(puzzle = GeneratedPuzzle(board, board.withCell(c1, 7).withCell(c2, 8), 0L, 7))
        game.selectCell(c1); game.isNotesModeActive = true; game.enterDigit(7); game.enterDigit(8)
        game.selectCell(c2); game.enterDigit(7); game.enterDigit(8)
        game.isNotesModeActive = false; game.selectCell(null)

        val step = TechniqueStep(
            technique = SolvingTechnique.NakedPair,
            primaryCells = setOf(c1, c2),
            explanation = "In Box 1, remaining cells (R3, C2) and (R3, C3) only contain {7, 8}.\nThey form a Naked Pair inside Box 1 and Row 3, locking 7 and 8 from other cells in Row 3."
        )
        return DemoSetup(
            technique = SolvingTechnique.NakedPair,
            title = "Naked Pair",
            subtitle = "Example 2: 3x3 Box Lock",
            explanation = "Two cells in the same 3x3 box share candidates {7, 8}.",
            game = game,
            hintStep = step
        )
    }

    // 7. Pointing Pair Horizontal
    private fun createPointingPairHorizontal(): DemoSetup {
        val board = SudokuBoard()
            .withCell(CellPosition.of(1, 0), 1)
            .withCell(CellPosition.of(1, 1), 2)
            .withCell(CellPosition.of(2, 0), 3)
            .withCell(CellPosition.of(2, 1), 4)

        val c1 = CellPosition.of(0, 0)
        val c2 = CellPosition.of(0, 1)
        val elim = CellPosition.of(0, 7)

        val game = SudokuGame(puzzle = GeneratedPuzzle(board, board.withCell(c1, 5), 0L, 4))
        val step = TechniqueStep(
            technique = SolvingTechnique.PointingPair,
            primaryCells = setOf(c1, c2),
            secondaryCells = setOf(elim),
            explanation = "In Box 1, candidate 5 is locked into Row 1.\nBecause 5 must be in Box 1 along Row 1, digit 5 cannot appear anywhere else in Row 1 outside this box!"
        )
        return DemoSetup(
            technique = SolvingTechnique.PointingPair,
            title = "Pointing Pair",
            subtitle = "Example 1: Row Alignment",
            explanation = "Candidates in Box 1 form a horizontal line along Row 1, eliminating that number from the rest of the row.",
            game = game,
            hintStep = step
        )
    }

    // 8. Pointing Pair Vertical
    private fun createPointingPairVertical(): DemoSetup {
        val board = SudokuBoard()
            .withCell(CellPosition.of(0, 1), 1)
            .withCell(CellPosition.of(1, 1), 2)
            .withCell(CellPosition.of(0, 2), 3)
            .withCell(CellPosition.of(1, 2), 4)

        val c1 = CellPosition.of(0, 0)
        val c2 = CellPosition.of(1, 0)
        val elim = CellPosition.of(7, 0)

        val game = SudokuGame(puzzle = GeneratedPuzzle(board, board.withCell(c1, 6), 0L, 4))
        val step = TechniqueStep(
            technique = SolvingTechnique.PointingPair,
            primaryCells = setOf(c1, c2),
            secondaryCells = setOf(elim),
            explanation = "In Box 1, candidate 6 is locked into Column 1.\nBecause 6 must be in Box 1 along Column 1, 6 cannot appear anywhere else in Column 1 outside Box 1!"
        )
        return DemoSetup(
            technique = SolvingTechnique.PointingPair,
            title = "Pointing Pair",
            subtitle = "Example 2: Column Alignment",
            explanation = "Candidates in Box 1 form a vertical line along Column 1, eliminating that number from the rest of the column.",
            game = game,
            hintStep = step
        )
    }

    // 9. X-Wing Demo A
    private fun createXWingDemoA(): DemoSetup {
        val board = SudokuBoard()
            .withCell(CellPosition.of(1, 0), 9)
            .withCell(CellPosition.of(7, 0), 9)

        val r1c1 = CellPosition.of(1, 2)
        val r1c2 = CellPosition.of(1, 6)
        val r2c1 = CellPosition.of(7, 2)
        val r2c2 = CellPosition.of(7, 6)

        val game = SudokuGame(puzzle = GeneratedPuzzle(board, board.withCell(r1c1, 5), 0L, 2))
        val step = TechniqueStep(
            technique = SolvingTechnique.XWing,
            primaryCells = setOf(r1c1, r1c2, r2c1, r2c2),
            secondaryCells = setOf(CellPosition.of(4, 2), CellPosition.of(4, 6)),
            explanation = "Notice the 4 corner cells forming a rectangle across Rows 2 and 8 in Columns 3 and 7.\nBecause candidate 5 is locked to these 4 corners, 5 is eliminated from all other cells in Column 3 and Column 7!"
        )
        return DemoSetup(
            technique = SolvingTechnique.XWing,
            title = "X-Wing",
            subtitle = "Example 1: Rows 2 and 8 Rectangle",
            explanation = "A 2x2 rectangle across two rows and columns locks the candidate to those corners, eliminating it from the rest of the columns.",
            game = game,
            hintStep = step
        )
    }

    // 10. X-Wing Demo B
    private fun createXWingDemoB(): DemoSetup {
        val board = SudokuBoard()
            .withCell(CellPosition.of(2, 8), 1)
            .withCell(CellPosition.of(5, 8), 1)

        val r1c1 = CellPosition.of(2, 1)
        val r1c2 = CellPosition.of(2, 4)
        val r2c1 = CellPosition.of(5, 1)
        val r2c2 = CellPosition.of(5, 4)

        val game = SudokuGame(puzzle = GeneratedPuzzle(board, board.withCell(r1c1, 8), 0L, 2))
        val step = TechniqueStep(
            technique = SolvingTechnique.XWing,
            primaryCells = setOf(r1c1, r1c2, r2c1, r2c2),
            secondaryCells = setOf(CellPosition.of(0, 1), CellPosition.of(0, 4)),
            explanation = "Rows 3 and 6 form an X-Wing on Columns 2 and 5 for digit 8.\nThis proves digit 8 cannot be anywhere else in Columns 2 and 5."
        )
        return DemoSetup(
            technique = SolvingTechnique.XWing,
            title = "X-Wing",
            subtitle = "Example 2: Rows 3 and 6 Rectangle",
            explanation = "Another standard tournament X-Wing configuration.",
            game = game,
            hintStep = step
        )
    }
}
