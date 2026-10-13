package com.sudoku.android

import android.content.Context
import android.content.SharedPreferences
import com.sudoku.core.storage.StorageDriver

class AndroidStorageDriver(context: Context) : StorageDriver {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sudoku_storage", Context.MODE_PRIVATE)

    override fun save(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun load(key: String): String? {
        return prefs.getString(key, null)
    }

    override fun clear(key: String) {
        prefs.edit().remove(key).apply()
    }
}
