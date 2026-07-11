package dev.hyunelab.markdownneat.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.options.Configurable
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import java.awt.GraphicsEnvironment
import java.util.Hashtable
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider

class MarkdownNeatSettingsConfigurable internal constructor(
    private val settings: MarkdownNeatSettings,
    private val notifySettingsChanged: () -> Unit,
    private val availableFontFamilies: () -> List<String>,
    private val previewFactory: () -> MarkdownNeatSettingsPreview?,
) : Configurable {
    constructor() : this(
        MarkdownNeatSettings.getInstance(),
        ::publishSettingsChanged,
        ::systemFontFamilies,
        ::createMarkdownNeatSettingsPreview,
    )

    private var themeField: ComboBox<MarkdownNeatTheme>? = null
    private var profileField: ComboBox<MarkdownNeatProfile>? = null
    private var bodyFontField: ComboBox<String>? = null
    private var codeFontField: ComboBox<String>? = null
    private var fontScaleField: JSlider? = null
    private var fontScaleLabel: JBLabel? = null
    private var contentWidthField: JSlider? = null
    private var contentWidthLabel: JBLabel? = null
    private var preview: MarkdownNeatSettingsPreview? = null

    override fun getDisplayName(): String = "MarkdownNeat"

    override fun createComponent(): JComponent {
        themeField = ComboBox(MarkdownNeatTheme.entries.toTypedArray()).apply {
            name = "theme"
            addActionListener { refreshPreview() }
        }
        profileField = ComboBox(MarkdownNeatProfile.entries.toTypedArray()).apply {
            name = "profile"
            addActionListener { refreshPreview() }
        }

        val installedFonts = availableFontFamilies()
        bodyFontField = ComboBox(
            fontChoices(installedFonts, RECOMMENDED_BODY_FONTS, settings.bodyFontFamily),
        ).apply {
            name = "bodyFont"
            toolTipText = FONT_FALLBACK_TOOLTIP
            addActionListener { refreshPreview() }
        }
        codeFontField = ComboBox(
            fontChoices(installedFonts, RECOMMENDED_CODE_FONTS, settings.codeFontFamily),
        ).apply {
            name = "codeFont"
            toolTipText = FONT_FALLBACK_TOOLTIP
            addActionListener { refreshPreview() }
        }
        fontScaleLabel = JBLabel()
        fontScaleField = JSlider(
            MarkdownNeatSettings.MIN_FONT_SCALE,
            MarkdownNeatSettings.MAX_FONT_SCALE,
            MarkdownNeatSettings.DEFAULT_FONT_SCALE,
        ).apply {
            name = "fontScale"
            majorTickSpacing = 30
            minorTickSpacing = 10
            paintTicks = true
            paintLabels = true
            labelTable = Hashtable<Int, JLabel>().apply {
                put(90, JLabel("90%"))
                put(120, JLabel("120%"))
                put(150, JLabel("150%"))
                put(180, JLabel("180%"))
            }
            addChangeListener {
                updateFontScaleLabel()
                refreshPreview()
            }
        }
        contentWidthLabel = JBLabel().apply { name = "contentWidthLabel" }
        contentWidthField = JSlider(
            MarkdownNeatSettings.MIN_CONTENT_WIDTH,
            FULL_WIDTH_SLIDER_VALUE,
            FULL_WIDTH_SLIDER_VALUE,
        ).apply {
            name = "contentWidth"
            majorTickSpacing = 384
            minorTickSpacing = 64
            paintTicks = true
            paintLabels = true
            snapToTicks = true
            labelTable = Hashtable<Int, JLabel>().apply {
                put(768, JLabel("768 px"))
                put(1152, JLabel("1152 px"))
                put(FULL_WIDTH_SLIDER_VALUE, JLabel("Full width"))
            }
            addChangeListener {
                updateContentWidthState()
                refreshPreview()
            }
        }
        val form = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Theme:"), requireNotNull(themeField), 1, false)
            .addLabeledComponent(JBLabel("Profile:"), requireNotNull(profileField), 1, false)
            .addLabeledComponent(JBLabel("Body font:"), requireNotNull(bodyFontField), 1, false)
            .addLabeledComponent(JBLabel("Code font:"), requireNotNull(codeFontField), 1, false)
            .addLabeledComponent(requireNotNull(fontScaleLabel), requireNotNull(fontScaleField), 1, false)
            .addLabeledComponent(requireNotNull(contentWidthLabel), requireNotNull(contentWidthField), 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
            .also { reset() }

        preview = previewFactory()
        val currentPreview = preview ?: return form
        refreshPreview()
        return JBSplitter(true, 0.38f).apply {
            firstComponent = JBScrollPane(form).apply { border = null }
            secondComponent = currentPreview.component
        }
    }

    override fun isModified(): Boolean {
        val useFullWidth = selectedUseFullWidth()
        return themeField?.selectedItem != settings.theme ||
            profileField?.selectedItem != settings.profile ||
            selectedFont(bodyFontField) != settings.bodyFontFamily ||
            selectedFont(codeFontField) != settings.codeFontFamily ||
            fontScaleField?.value != settings.fontScale ||
            useFullWidth != settings.useFullWidth ||
            (!useFullWidth && contentWidthField?.value != settings.maxContentWidth)
    }

    override fun apply() {
        if (settings.updateAppearance(selectedAppearance())) {
            notifySettingsChanged()
        }
    }

    override fun reset() {
        themeField?.selectedItem = settings.theme
        profileField?.selectedItem = settings.profile
        bodyFontField?.selectedItem = displayedFont(settings.bodyFontFamily)
        codeFontField?.selectedItem = displayedFont(settings.codeFontFamily)
        fontScaleField?.value = settings.fontScale
        contentWidthField?.value = if (settings.useFullWidth) {
            FULL_WIDTH_SLIDER_VALUE
        } else {
            settings.maxContentWidth
        }
        updateFontScaleLabel()
        updateContentWidthState()
        refreshPreview()
    }

    override fun disposeUIResources() {
        preview?.dispose()
        preview = null
        themeField = null
        profileField = null
        bodyFontField = null
        codeFontField = null
        fontScaleField = null
        fontScaleLabel = null
        contentWidthField = null
        contentWidthLabel = null
    }

    private fun fontChoices(
        installedFonts: List<String>,
        recommendedFonts: List<String>,
        selectedFont: String,
    ): Array<String> = buildList {
        add(DEFAULT_FONT)
        if (selectedFont.isNotEmpty()) {
            add(selectedFont)
        }
        addAll(recommendedFonts.mapNotNull { recommendation ->
            installedFonts.firstOrNull { it.equals(recommendation, ignoreCase = true) }
        })
    }.distinctBy { it.lowercase() }.toTypedArray()

    private fun updateFontScaleLabel() {
        fontScaleLabel?.text = "Text scale: ${fontScaleField?.value ?: settings.fontScale}%"
    }

    private fun updateContentWidthState() {
        contentWidthLabel?.text = if (selectedUseFullWidth()) {
            "Maximum content width: Full width"
        } else {
            "Maximum content width: ${contentWidthField?.value ?: settings.maxContentWidth} px"
        }
    }

    private fun refreshPreview() {
        preview?.render(selectedAppearance())
    }

    private fun selectedAppearance(): MarkdownNeatAppearance = MarkdownNeatAppearance(
        theme = themeField?.selectedItem as? MarkdownNeatTheme ?: settings.theme,
        profile = profileField?.selectedItem as? MarkdownNeatProfile ?: settings.profile,
        bodyFontFamily = selectedFont(bodyFontField),
        codeFontFamily = selectedFont(codeFontField),
        fontScale = fontScaleField?.value ?: settings.fontScale,
        maxContentWidth = selectedMaxContentWidth(),
        useFullWidth = selectedUseFullWidth(),
    )

    private fun selectedUseFullWidth(): Boolean =
        contentWidthField?.value?.let { it == FULL_WIDTH_SLIDER_VALUE } ?: settings.useFullWidth

    private fun selectedMaxContentWidth(): Int =
        if (selectedUseFullWidth()) {
            settings.maxContentWidth
        } else {
            contentWidthField?.value ?: settings.maxContentWidth
        }

    private companion object {
        const val DEFAULT_FONT = "Default (system font)"
        const val CONTENT_WIDTH_STEP = 64
        const val FULL_WIDTH_SLIDER_VALUE = MarkdownNeatSettings.MAX_CONTENT_WIDTH + CONTENT_WIDTH_STEP
        const val FONT_FALLBACK_TOOLTIP =
            "If the selected font is unavailable, MarkdownNeat uses the default system font."
        val RECOMMENDED_BODY_FONTS = listOf(
            "Atkinson Hyperlegible",
            "Inter",
            "Pretendard",
            "Noto Sans",
            "Noto Sans CJK KR",
            "Apple SD Gothic Neo",
            "Segoe UI",
            "Arial",
            "Helvetica",
            "Georgia",
            "Noto Serif",
            "Times New Roman",
        )
        val RECOMMENDED_CODE_FONTS = listOf(
            "JetBrains Mono",
            "D2Coding",
            "Fira Code",
            "Cascadia Code",
            "SF Mono",
            "Menlo",
            "Monaco",
            "Consolas",
            "Source Code Pro",
            "Ubuntu Mono",
        )

        fun displayedFont(fontFamily: String): String = fontFamily.ifEmpty { DEFAULT_FONT }

        fun selectedFont(field: ComboBox<String>?): String =
            (field?.selectedItem as? String).orEmpty().takeUnless { it == DEFAULT_FONT }.orEmpty()

        fun systemFontFamilies(): List<String> =
            GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
                .sortedWith(String.CASE_INSENSITIVE_ORDER)

        fun publishSettingsChanged() {
            ApplicationManager.getApplication().messageBus
                .syncPublisher(MarkdownNeatSettingsListener.TOPIC)
                .settingsChanged()
        }
    }
}
