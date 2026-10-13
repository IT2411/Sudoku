package com.sudoku.core.haptics

interface HapticDriver {
    fun performClick()
    fun performError()
    fun performSuccess()
}

object Haptics {
    var driver: HapticDriver? = null
    var isEnabled: Boolean = true

    fun click() {
        if (isEnabled) driver?.performClick()
    }

    fun error() {
        if (isEnabled) driver?.performError()
    }

    fun success() {
        if (isEnabled) driver?.performSuccess()
    }
}
