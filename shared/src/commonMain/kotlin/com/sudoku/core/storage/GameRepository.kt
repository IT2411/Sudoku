package com.sudoku.core.storage

object GameRepository {
    private const val KEY_SAVED_GAME = "active_sudoku_game"

    var driver: StorageDriver? = null

    fun saveGame(snapshot: GameStateSnapshot) {
        val d = driver ?: return
        if (snapshot.status == "PLAYING" || snapshot.status == "PAUSED") {
            d.save(KEY_SAVED_GAME, snapshot.serialize())
        } else {
            clearGame()
        }
    }

    fun loadGame(): GameStateSnapshot? {
        val d = driver ?: return null
        val raw = d.load(KEY_SAVED_GAME) ?: return null
        return GameStateSnapshot.deserialize(raw)
    }

    fun hasSavedGame(): Boolean = loadGame() != null

    fun clearGame() {
        driver?.clear(KEY_SAVED_GAME)
    }
}
