package com.sudoku.core.achievements

enum class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String
) {
    FIRST_WIN(
        id = "first_win",
        title = "First Victory",
        description = "Complete your first Sudoku puzzle.",
        icon = "🥉"
    ),
    PERFECT_GAME(
        id = "perfect_game",
        title = "Flawless Mind",
        description = "Complete a puzzle with 0 mistakes.",
        icon = "💎"
    ),
    SPEED_DEMON(
        id = "speed_demon",
        title = "Speed Demon",
        description = "Solve any puzzle in under 5 minutes.",
        icon = "⚡"
    ),
    NO_HINTS(
        id = "no_hints",
        title = "Pure Logic",
        description = "Solve without using a single hint.",
        icon = "🧠"
    ),
    EXPERT_MASTER(
        id = "expert_master",
        title = "Grandmaster",
        description = "Conquer an Expert difficulty puzzle.",
        icon = "👑"
    ),
    TEN_WINS(
        id = "ten_wins",
        title = "Dedicated Solver",
        description = "Complete 10 total Sudoku puzzles.",
        icon = "🥈"
    ),
    STREAK_7(
        id = "streak_7",
        title = "Week of Logic",
        description = "Achieve a 7-day daily streak.",
        icon = "🔥"
    )
}
