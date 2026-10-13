package com.sudoku.core.difficulty

import com.sudoku.core.candidates.CandidateCalculator
import com.sudoku.core.model.CellPosition
import com.sudoku.core.model.Candidates
import com.sudoku.core.model.SudokuBoard

data class DifficultyAnalysis(
    val difficulty: Difficulty,
    val score: Int,
    val maxTechniqueWeight: Int,
    val techniqueCounts: Map<String, Int>,
    val steps: List<TechniqueStep>
)

object DifficultyAnalyzer {

    // Registered techniques in order of priority (simplest first)
    private val TECHNIQUES: List<SolvingTechnique> = listOf(
        SolvingTechnique.NakedSingle,
        SolvingTechnique.HiddenSingle,
        SolvingTechnique.NakedPair,
        SolvingTechnique.PointingPair,
        SolvingTechnique.XWing
    )

    /**
     * Simulates a human solving the puzzle using logic techniques.
     */
    fun analyze(initialBoard: SudokuBoard): DifficultyAnalysis {
        var currentBoard = initialBoard.copy()
        var currentCandidates = CandidateCalculator.calculateAllCandidates(currentBoard)

        val steps = mutableListOf<TechniqueStep>()
        val counts = mutableMapOf<String, Int>()
        var totalScore = 0
        var maxWeight = 1

        while (!isSolved(currentBoard)) {
            var stepFound = false

            for (technique in TECHNIQUES) {
                val step = technique.findStep(currentBoard, currentCandidates)
                if (step != null) {
                    stepFound = true
                    steps.add(step)
                    counts[step.technique.name] = (counts[step.technique.name] ?: 0) + 1
                    totalScore += step.technique.weight
                    if (step.technique.weight > maxWeight) {
                        maxWeight = step.technique.weight
                    }

                    // Apply step: either place a digit or eliminate candidates
                    if (step.placedDigit != null) {
                        val (pos, digit) = step.placedDigit
                        currentBoard = currentBoard.withCell(pos, digit)
                        currentCandidates = CandidateCalculator.calculateAllCandidates(currentBoard)
                    } else if (step.eliminatedCandidates.isNotEmpty()) {
                        for ((pos, elim) in step.eliminatedCandidates) {
                            val oldCand = currentCandidates[pos.index]
                            currentCandidates[pos.index] = Candidates.fromMask(oldCand.mask and elim.mask.inv())
                        }
                    }
                    break
                }
            }

            if (!stepFound) {
                // If human logic cannot proceed with basic/intermediate rules, classify as EXPERT
                maxWeight = 15
                totalScore += 200
                break
            }
        }

        val difficulty = Difficulty.fromScore(totalScore, maxWeight)
        return DifficultyAnalysis(
            difficulty = difficulty,
            score = totalScore,
            maxTechniqueWeight = maxWeight,
            techniqueCounts = counts,
            steps = steps
        )
    }

    private fun isSolved(board: SudokuBoard): Boolean {
        for (i in 0 until CellPosition.TOTAL_CELLS) {
            if (board[i] == SudokuBoard.EMPTY_VALUE) return false
        }
        return true
    }
}