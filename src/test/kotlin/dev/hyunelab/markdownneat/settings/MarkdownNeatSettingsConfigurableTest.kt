package dev.hyunelab.markdownneat.settings

import com.intellij.openapi.ui.ComboBox
import java.awt.Component
import java.awt.Container
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MarkdownNeatSettingsConfigurableTest {
    @Test
    fun testAppliesThemeAndNotifiesOpenViewers() {
        val settings = MarkdownNeatSettings()
        var notifications = 0

        val configurable = MarkdownNeatSettingsConfigurable(settings) { notifications += 1 }
        val component = configurable.createComponent()
        val themeField = findComboBox(component)
        themeField.selectedItem = MarkdownNeatTheme.DARK

        assertTrue(configurable.isModified)
        configurable.apply()
        assertEquals(MarkdownNeatTheme.DARK, settings.theme)
        assertEquals(1, notifications)
        assertFalse(configurable.isModified)

        configurable.disposeUIResources()
    }

    @Suppress("UNCHECKED_CAST")
    private fun findComboBox(component: Component): ComboBox<MarkdownNeatTheme> {
        if (component is ComboBox<*>) {
            return component as ComboBox<MarkdownNeatTheme>
        }
        if (component is Container) {
            for (child in component.components) {
                runCatching { return findComboBox(child) }
            }
        }
        error("Theme selector not found")
    }
}
