package com.sudoku.core.game

class UndoManager {
    private val undoStack = mutableListOf<Move>()
    private val redoStack = mutableListOf<Move>()

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    fun recordMove(move: Move) {
        undoStack.add(move)
        redoStack.clear() // Clear redo history on new action
    }

    fun undo(): Move? {
        if (!canUndo) return null
        val move = undoStack.removeAt(undoStack.lastIndex)
        redoStack.add(move)
        return move
    }

    fun redo(): Move? {
        if (!canRedo) return null
        val move = redoStack.removeAt(redoStack.lastIndex)
        undoStack.add(move)
        return move
    }

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}