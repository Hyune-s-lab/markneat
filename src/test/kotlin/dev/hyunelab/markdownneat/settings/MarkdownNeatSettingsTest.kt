package dev.hyunelab.markdownneat.settings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MarkdownNeatSettingsTest {
    @Test
    fun `uses light by default and reports actual theme changes`() {
        val settings = MarkdownNeatSettings()

        assertEquals(MarkdownNeatTheme.LIGHT, settings.theme)
        assertTrue(settings.updateTheme(MarkdownNeatTheme.DARK))
        assertEquals(MarkdownNeatTheme.DARK, settings.theme)
        assertFalse(settings.updateTheme(MarkdownNeatTheme.DARK))
    }

    @Test
    fun `loads the persisted theme`() {
        val settings = MarkdownNeatSettings()

        settings.loadState(MarkdownNeatSettings.SettingsState(theme = MarkdownNeatTheme.DARK))

        assertEquals(MarkdownNeatTheme.DARK, settings.theme)
    }
}
