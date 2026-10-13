package com.sudoku.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.sudoku.core.storage.GameRepository
import com.sudoku.ui.SudokuApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set status bar and navigation bar to deep charcoal (#0F1117)
        val darkBgColor = android.graphics.Color.parseColor("#0F1117")
        window.statusBarColor = darkBgColor
        window.navigationBarColor = darkBgColor

        // Ensure status bar icons (clock, battery, Wi-Fi) are light/white
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = false

        // Attach persistent storage driver
        GameRepository.driver = AndroidStorageDriver(applicationContext)

        setContent {
            SudokuApp()
        }
    }
}
