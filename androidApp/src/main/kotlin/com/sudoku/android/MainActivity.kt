package com.sudoku.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sudoku.core.storage.GameRepository
import com.sudoku.ui.SudokuApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Attach persistent Android disk storage
        GameRepository.driver = AndroidStorageDriver(applicationContext)

        setContent {
            SudokuApp()
        }
    }
}
