package com.sudoku.core.settings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameSettingsTest {

    @Test
    fun testSettingsSerializationRoundtrip() {
        val custom = GameSettings(
            hapticsEnabled = false,
            mistakeLimitEnabled = false,
            timerVisible = false,
            autoRemoveNotes = true
        )

        val serialized = custom.serialize()
        val restored = GameSettings.deserialize(serialized)

        assertFalse(restored.hapticsEnabled)
        assertFalse(restored.mistakeLimitEnabled)
        assertFalse(restored.timerVisible)
        assertTrue(restored.autoRemoveNotes)
        assertEquals(custom, restored)
    }
}
