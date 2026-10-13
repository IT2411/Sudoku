package com.sudoku.core.settings

data class GameSettings(
    val hapticsEnabled: Boolean = true,
    val mistakeLimitEnabled: Boolean = true, // true = 3 mistakes, false = unlimited
    val timerVisible: Boolean = true,
    val autoRemoveNotes: Boolean = true
) {
    fun serialize(): String = "$hapticsEnabled,$mistakeLimitEnabled,$timerVisible,$autoRemoveNotes"

    companion object {
        fun deserialize(raw: String): GameSettings {
            return try {
                val parts = raw.split(",")
                GameSettings(
                    hapticsEnabled = parts.getOrNull(0)?.toBooleanStrictOrNull() ?: true,
                    mistakeLimitEnabled = parts.getOrNull(1)?.toBooleanStrictOrNull() ?: true,
                    timerVisible = parts.getOrNull(2)?.toBooleanStrictOrNull() ?: true,
                    autoRemoveNotes = parts.getOrNull(3)?.toBooleanStrictOrNull() ?: true
                )
            } catch (e: Exception) {
                GameSettings()
            }
        }
    }
}
