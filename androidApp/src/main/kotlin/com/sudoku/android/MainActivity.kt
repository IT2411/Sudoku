package com.sudoku.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.sudoku.core.haptics.Haptics
import com.sudoku.core.storage.GameRepository
import com.sudoku.core.storage.SettingsRepository
import com.sudoku.ui.NavigationController
import com.sudoku.ui.SudokuApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val darkBgColor = android.graphics.Color.parseColor("#0F1117")
        window.statusBarColor = darkBgColor
        window.navigationBarColor = darkBgColor

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = false

        GameRepository.driver = AndroidStorageDriver(applicationContext)
        Haptics.driver = AndroidHapticDriver(applicationContext)
        SettingsRepository.loadSettings()

        val navController = NavigationController()

        // Handle Android system back button & edge swipe gesture
        onBackPressedDispatcher.addCallback(this) {
            if (navController.canGoBack) {
                navController.onGoBack?.invoke()
            } else {
                finish()
            }
        }

        setContent {
            SudokuApp(navController = navController)
        }
    }
}
