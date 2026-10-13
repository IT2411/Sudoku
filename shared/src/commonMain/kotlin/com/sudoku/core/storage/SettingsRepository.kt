package com.sudoku.core.storage

import com.sudoku.core.haptics.Haptics
import com.sudoku.core.settings.GameSettings

object SettingsRepository {
    private const val KEY_SETTINGS = "sudoku_game_settings"

    fun loadSettings(): GameSettings {
        val driver = GameRepository.driver ?: return GameSettings()
        val raw = driver.load(KEY_SETTINGS) ?: return GameSettings()
        val settings = GameSettings.deserialize(raw)
        Haptics.isEnabled = settings.hapticsEnabled
        return settings
    }

    fun saveSettings(settings: GameSettings) {
        val driver = GameRepository.driver ?: return
        driver.save(KEY_SETTINGS, settings.serialize())
        Haptics.isEnabled = settings.hapticsEnabled
    }
}
