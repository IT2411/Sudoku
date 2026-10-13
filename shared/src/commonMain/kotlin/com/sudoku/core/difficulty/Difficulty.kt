package com.sudoku.core.difficulty

enum class Difficulty(val displayName: String, val baseClueTarget: Int) {
    EASY("Easy", baseClueTarget = 38),
    MEDIUM("Medium", baseClueTarget = 32),
    HARD("Hard", baseClueTarget = 28),
    EXPERT("Expert", baseClueTarget = 24);

    companion object {
        fun fromScore(score: Int, maxTechniqueWeight: Int): Difficulty = when {
            maxTechniqueWeight <= 2 && score <= 60 -> EASY
            maxTechniqueWeight <= 5 && score <= 140 -> MEDIUM
            maxTechniqueWeight <= 10 && score <= 260 -> HARD
            else -> EXPERT
        }
    }
}