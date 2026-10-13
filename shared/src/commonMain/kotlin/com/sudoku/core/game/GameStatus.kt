package com.sudoku.core.game

enum class GameStatus {
    NOT_STARTED,
    PLAYING,
    PAUSED,
    COMPLETED,
    GAME_OVER // Reached mistake limit
}