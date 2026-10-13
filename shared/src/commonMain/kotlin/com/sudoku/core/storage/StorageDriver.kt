package com.sudoku.core.storage

/**
 * Platform-agnostic interface for persistent disk storage.
 */
interface StorageDriver {
    fun save(key: String, value: String)
    fun load(key: String): String?
    fun clear(key: String)
}
