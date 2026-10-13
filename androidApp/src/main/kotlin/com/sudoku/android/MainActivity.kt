package com.sudoku.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.sudoku.core.haptics.Haptics
import com.sudoku.core.storage.GameRepository
import com.sudoku.core.storage.SettingsRepository
import com.sudoku.ui.SudokuApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dark theme status bar and navigation bar
        val darkBgColor = android.graphics.Color.parseColor("#0F1117")
        window.statusBarColor = darkBgColor
        window.navigationBarColor = darkBgColor

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = false

        // Attach persistent storage & native haptics
        GameRepository.driver = AndroidStorageDriver(applicationContext)
        Haptics.driver = AndroidHapticDriver(applicationContext)

        // Preload settings
        SettingsRepository.loadSettings()

        setContent {
            SudokuApp()
        }
    }
}
