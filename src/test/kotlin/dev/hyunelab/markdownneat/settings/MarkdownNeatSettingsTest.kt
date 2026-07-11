package dev.hyunelab.markdownneat.settings

import com.intellij.util.xmlb.XmlSerializer
import org.jdom.Element
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MarkdownNeatSettingsTest {
    @Test
    fun `loads display names written by earlier appearance builds`() {
        val serializedState = Element("state").apply {
            addContent(Element("option").setAttribute("name", "theme").setAttribute("value", "Dark"))
            addContent(Element("option").setAttribute("name", "profile").setAttribute("value", "Spacious"))
        }
        val state = XmlSerializer.deserialize(
            serializedState,
            MarkdownNeatSettings.SettingsState::class.java,
        )
        val settings = MarkdownNeatSettings()

        settings.loadState(state)

        assertEquals(MarkdownNeatTheme.DARK, settings.theme)
        assertEquals(MarkdownNeatProfile.SPACIOUS, settings.profile)
    }

    @Test
    fun `uses light by default and reports actual theme changes`() {
        val settings = MarkdownNeatSettings()

        assertEquals(MarkdownNeatTheme.LIGHT, settings.theme)
        assertEquals("GitHub Light", MarkdownNeatTheme.LIGHT.toString())
        assertEquals("GitHub Dark", MarkdownNeatTheme.DARK.toString())
        assertEquals(MarkdownNeatProfile.COMPACT, settings.profile)
        assertEquals("", settings.bodyFontFamily)
        assertEquals("", settings.codeFontFamily)
        assertEquals(100, settings.fontScale)
        assertEquals(1152, settings.maxContentWidth)
        assertTrue(settings.useFullWidth)
        assertTrue(
            settings.updateAppearance(
                theme = MarkdownNeatTheme.DARK,
                profile = MarkdownNeatProfile.SPACIOUS,
                bodyFontFamily = "Atkinson Hyperlegible",
                codeFontFamily = "JetBrains Mono",
                fontScale = 130,
                maxContentWidth = 1280,
                useFullWidth = true,
            ),
        )
        assertEquals(MarkdownNeatTheme.DARK, settings.theme)
        assertEquals(MarkdownNeatProfile.SPACIOUS, settings.profile)
        assertEquals("Atkinson Hyperlegible", settings.bodyFontFamily)
        assertEquals("JetBrains Mono", settings.codeFontFamily)
        assertEquals(130, settings.fontScale)
        assertEquals(1280, settings.maxContentWidth)
        assertTrue(settings.useFullWidth)
        assertFalse(
            settings.updateAppearance(
                theme = MarkdownNeatTheme.DARK,
                profile = MarkdownNeatProfile.SPACIOUS,
                bodyFontFamily = "Atkinson Hyperlegible",
                codeFontFamily = "JetBrains Mono",
                fontScale = 130,
                maxContentWidth = 1280,
                useFullWidth = true,
            ),
        )
    }

    @Test
    fun `loads the persisted theme`() {
        val settings = MarkdownNeatSettings()

        settings.loadState(
            MarkdownNeatSettings.SettingsState(
                theme = MarkdownNeatTheme.DARK,
                profile = MarkdownNeatProfile.SPACIOUS,
                bodyFontFamily = "  Inter  ",
                codeFontFamily = "  JetBrains Mono  ",
                fontScale = 500,
                maxContentWidth = 5000,
                useFullWidth = false,
            ),
        )

        assertEquals(MarkdownNeatTheme.DARK, settings.theme)
        assertEquals(MarkdownNeatProfile.SPACIOUS, settings.profile)
        assertEquals("Inter", settings.bodyFontFamily)
        assertEquals("JetBrains Mono", settings.codeFontFamily)
        assertEquals(180, settings.fontScale)
        assertEquals(1536, settings.maxContentWidth)
        assertFalse(settings.useFullWidth)
    }
}
