package dev.hyunelab.markdownneat.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBLabel
import java.awt.Component
import java.awt.Container
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSlider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MarkdownNeatSettingsConfigurableTest {
    @Test
    fun testPreviewsAndAppliesAppearanceBeforeNotifyingOpenViewers() {
        val settings = MarkdownNeatSettings()
        var notifications = 0
        val preview = RecordingSettingsPreview()

        val configurable = MarkdownNeatSettingsConfigurable(
            settings = settings,
            notifySettingsChanged = { notifications += 1 },
            availableFontFamilies = {
                listOf("Arial", "Atkinson Hyperlegible", "Comic Sans MS", "JetBrains Mono")
            },
            previewFactory = { preview },
        )
        val component = configurable.createComponent()
        val splitter = component as JBSplitter
        assertTrue(splitter.isVertical)
        assertEquals(0.38f, splitter.proportion, 0.001f)
        assertTrue(contains(splitter.secondComponent, preview.component))
        assertTrue(contains(component, preview.component))
        assertEquals(MarkdownNeatProfile.COMPACT, preview.appearances.last().profile)
        assertTrue(preview.appearances.last().useFullWidth)
        @Suppress("UNCHECKED_CAST")
        val themeField = findNamed(component, "theme") as ComboBox<MarkdownNeatTheme>
        @Suppress("UNCHECKED_CAST")
        val profileField = findNamed(component, "profile") as ComboBox<MarkdownNeatProfile>
        @Suppress("UNCHECKED_CAST")
        val bodyFontField = findNamed(component, "bodyFont") as ComboBox<String>
        @Suppress("UNCHECKED_CAST")
        val codeFontField = findNamed(component, "codeFont") as ComboBox<String>
        val fontScaleField = findNamed(component, "fontScale") as JSlider
        val contentWidthField = findNamed(component, "contentWidth") as JSlider
        val contentWidthLabel = findNamed(component, "contentWidthLabel") as JBLabel
        assertEquals("Default (system font)", bodyFontField.getItemAt(0))
        assertEquals("Default (system font)", codeFontField.getItemAt(0))
        assertEquals(
            listOf("Default (system font)", "Atkinson Hyperlegible", "Arial"),
            bodyFontField.items(),
        )
        assertEquals(
            listOf("Default (system font)", "JetBrains Mono"),
            codeFontField.items(),
        )
        assertEquals(
            "If the selected font is unavailable, MarkdownNeat uses the default system font.",
            bodyFontField.toolTipText,
        )
        assertEquals(bodyFontField.toolTipText, codeFontField.toolTipText)
        assertEquals(1600, contentWidthField.maximum)
        assertEquals(1600, contentWidthField.value)
        assertEquals("Maximum content width: Full width", contentWidthLabel.text)
        for (value in listOf(768, 1152, 1536)) {
            contentWidthField.value = value
            assertEquals("Maximum content width: $value px", contentWidthLabel.text)
            assertEquals(value, preview.appearances.last().maxContentWidth)
            assertFalse(preview.appearances.last().useFullWidth)
        }
        contentWidthField.value = contentWidthField.maximum
        assertTrue(preview.appearances.last().useFullWidth)
        themeField.selectedItem = MarkdownNeatTheme.DARK
        profileField.selectedItem = MarkdownNeatProfile.SPACIOUS
        bodyFontField.selectedItem = "Atkinson Hyperlegible"
        codeFontField.selectedItem = "JetBrains Mono"
        fontScaleField.value = 130
        contentWidthField.value = 1280
        assertEquals("Maximum content width: 1280 px", contentWidthLabel.text)
        assertEquals(1280, preview.appearances.last().maxContentWidth)
        assertFalse(preview.appearances.last().useFullWidth)
        contentWidthField.value = contentWidthField.maximum
        assertEquals("Maximum content width: Full width", contentWidthLabel.text)

        assertEquals(
            MarkdownNeatAppearance(
                theme = MarkdownNeatTheme.DARK,
                profile = MarkdownNeatProfile.SPACIOUS,
                bodyFontFamily = "Atkinson Hyperlegible",
                codeFontFamily = "JetBrains Mono",
                fontScale = 130,
                maxContentWidth = 1152,
                useFullWidth = true,
            ),
            preview.appearances.last(),
        )
        assertTrue(configurable.isModified)
        configurable.apply()
        assertEquals(MarkdownNeatTheme.DARK, settings.theme)
        assertEquals(MarkdownNeatProfile.SPACIOUS, settings.profile)
        assertEquals("Atkinson Hyperlegible", settings.bodyFontFamily)
        assertEquals("JetBrains Mono", settings.codeFontFamily)
        assertEquals(130, settings.fontScale)
        assertEquals(1152, settings.maxContentWidth)
        assertTrue(settings.useFullWidth)
        assertEquals(1, notifications)
        assertFalse(configurable.isModified)

        configurable.disposeUIResources()
        assertTrue(preview.disposed)
    }

    @Test
    fun testKeepsStoredFontsOutsideTheRecommendedInstalledChoices() {
        val settings = MarkdownNeatSettings().apply {
            updateAppearance(
                theme = MarkdownNeatTheme.LIGHT,
                profile = MarkdownNeatProfile.COMPACT,
                bodyFontFamily = "Legacy Reading Font",
                codeFontFamily = "Legacy Code Font",
                fontScale = 100,
                maxContentWidth = 1152,
                useFullWidth = false,
            )
        }
        val configurable = MarkdownNeatSettingsConfigurable(
            settings = settings,
            notifySettingsChanged = {},
            availableFontFamilies = { listOf("Arial", "JetBrains Mono") },
            previewFactory = { null },
        )
        val component = configurable.createComponent()
        @Suppress("UNCHECKED_CAST")
        val bodyFontField = findNamed(component, "bodyFont") as ComboBox<String>
        @Suppress("UNCHECKED_CAST")
        val codeFontField = findNamed(component, "codeFont") as ComboBox<String>
        val contentWidthField = findNamed(component, "contentWidth") as JSlider
        val contentWidthLabel = findNamed(component, "contentWidthLabel") as JBLabel

        assertEquals("Legacy Reading Font", bodyFontField.selectedItem)
        assertEquals("Legacy Code Font", codeFontField.selectedItem)
        assertEquals(1152, contentWidthField.value)
        assertEquals("Maximum content width: 1152 px", contentWidthLabel.text)
        assertEquals(
            listOf("Default (system font)", "Legacy Reading Font", "Arial"),
            bodyFontField.items(),
        )
        assertEquals(
            listOf("Default (system font)", "Legacy Code Font", "JetBrains Mono"),
            codeFontField.items(),
        )
        assertFalse(configurable.isModified)

        bodyFontField.selectedIndex = 0
        codeFontField.selectedIndex = 0
        assertTrue(configurable.isModified)
        configurable.reset()
        assertEquals("Legacy Reading Font", bodyFontField.selectedItem)
        assertEquals("Legacy Code Font", codeFontField.selectedItem)
        assertFalse(configurable.isModified)

        configurable.disposeUIResources()
    }

    private fun findNamed(component: Component, name: String): Component {
        if (component.name == name) {
            return component
        }
        if (component is Container) {
            for (child in component.components) {
                runCatching { return findNamed(child, name) }
            }
        }
        error("Component $name not found")
    }

    private fun contains(root: Component, target: Component): Boolean =
        root === target || (root is Container && root.components.any { contains(it, target) })

    private fun ComboBox<String>.items(): List<String> =
        (0 until itemCount).map(::getItemAt)

    private class RecordingSettingsPreview : MarkdownNeatSettingsPreview {
        override val component: JComponent = JPanel()
        val appearances = mutableListOf<MarkdownNeatAppearance>()
        var disposed = false

        override fun render(appearance: MarkdownNeatAppearance) {
            appearances += appearance
        }

        override fun dispose() {
            disposed = true
        }
    }
}
