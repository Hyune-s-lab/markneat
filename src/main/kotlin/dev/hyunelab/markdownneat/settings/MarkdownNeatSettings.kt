package dev.hyunelab.markdownneat.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.Converter
import com.intellij.util.xmlb.annotations.OptionTag

enum class MarkdownNeatTheme(
    val displayName: String,
    val wireValue: String,
) {
    LIGHT("GitHub Light", "light"),
    DARK("GitHub Dark", "dark"),
    ;

    override fun toString(): String = displayName
}

enum class MarkdownNeatProfile(
    val displayName: String,
    val wireValue: String,
) {
    COMPACT("Compact", "compact"),
    SPACIOUS("Spacious", "spacious"),
    ;

    override fun toString(): String = displayName
}

data class MarkdownNeatAppearance(
    val theme: MarkdownNeatTheme,
    val profile: MarkdownNeatProfile,
    val bodyFontFamily: String,
    val codeFontFamily: String,
    val fontScale: Int,
    val maxContentWidth: Int,
    val useFullWidth: Boolean,
    val accentHeadings: Boolean = false,
    val accentBold: Boolean = false,
    val accentInlineCode: Boolean = false,
)

@State(
    name = "dev.hyunelab.markdownneat.settings.MarkdownNeatSettings",
    storages = [Storage("markdownneat.xml")],
)
class MarkdownNeatSettings : PersistentStateComponent<MarkdownNeatSettings.SettingsState> {
    data class SettingsState(
        @OptionTag(converter = MarkdownNeatThemeConverter::class)
        var theme: MarkdownNeatTheme = MarkdownNeatTheme.LIGHT,
        @OptionTag(converter = MarkdownNeatProfileConverter::class)
        var profile: MarkdownNeatProfile = MarkdownNeatProfile.COMPACT,
        var bodyFontFamily: String = "",
        var codeFontFamily: String = "",
        var fontScale: Int = DEFAULT_FONT_SCALE,
        var maxContentWidth: Int = DEFAULT_CONTENT_WIDTH,
        var useFullWidth: Boolean = true,
        var accentHeadings: Boolean = false,
        var accentBold: Boolean = false,
        var accentInlineCode: Boolean = false,
        // Distinguishes pre-0.4 settings files, whose heading accent was implied by the profile.
        var accentsInitialized: Boolean = false,
    )

    private var settingsState = normalizedState(SettingsState())

    val theme: MarkdownNeatTheme
        get() = settingsState.theme

    val profile: MarkdownNeatProfile
        get() = settingsState.profile

    val bodyFontFamily: String
        get() = settingsState.bodyFontFamily

    val codeFontFamily: String
        get() = settingsState.codeFontFamily

    val fontScale: Int
        get() = settingsState.fontScale

    val maxContentWidth: Int
        get() = settingsState.maxContentWidth

    val useFullWidth: Boolean
        get() = settingsState.useFullWidth

    val accentHeadings: Boolean
        get() = settingsState.accentHeadings

    val accentBold: Boolean
        get() = settingsState.accentBold

    val accentInlineCode: Boolean
        get() = settingsState.accentInlineCode

    val appearance: MarkdownNeatAppearance
        get() = MarkdownNeatAppearance(
            theme,
            profile,
            bodyFontFamily,
            codeFontFamily,
            fontScale,
            maxContentWidth,
            useFullWidth,
            accentHeadings,
            accentBold,
            accentInlineCode,
        )

    fun updateAppearance(appearance: MarkdownNeatAppearance): Boolean = updateAppearance(
        theme = appearance.theme,
        profile = appearance.profile,
        bodyFontFamily = appearance.bodyFontFamily,
        codeFontFamily = appearance.codeFontFamily,
        fontScale = appearance.fontScale,
        maxContentWidth = appearance.maxContentWidth,
        useFullWidth = appearance.useFullWidth,
        accentHeadings = appearance.accentHeadings,
        accentBold = appearance.accentBold,
        accentInlineCode = appearance.accentInlineCode,
    )

    fun updateAppearance(
        theme: MarkdownNeatTheme,
        profile: MarkdownNeatProfile,
        bodyFontFamily: String,
        codeFontFamily: String,
        fontScale: Int,
        maxContentWidth: Int,
        useFullWidth: Boolean,
        accentHeadings: Boolean = false,
        accentBold: Boolean = false,
        accentInlineCode: Boolean = false,
    ): Boolean {
        val nextState = normalizedState(
            SettingsState(
                theme,
                profile,
                bodyFontFamily,
                codeFontFamily,
                fontScale,
                maxContentWidth,
                useFullWidth,
                accentHeadings,
                accentBold,
                accentInlineCode,
                accentsInitialized = true,
            ),
        )
        if (settingsState == nextState) {
            return false
        }
        settingsState = nextState
        return true
    }

    override fun getState(): SettingsState = settingsState

    override fun loadState(state: SettingsState) {
        settingsState = normalizedState(state)
    }

    companion object {
        const val MIN_FONT_SCALE = 90
        const val MAX_FONT_SCALE = 180
        const val DEFAULT_FONT_SCALE = 100
        const val MIN_CONTENT_WIDTH = 768
        const val MAX_CONTENT_WIDTH = 1536
        const val DEFAULT_CONTENT_WIDTH = 1152

        fun getInstance(): MarkdownNeatSettings =
            ApplicationManager.getApplication().getService(MarkdownNeatSettings::class.java)

        private fun normalizedState(state: SettingsState): SettingsState = state.copy(
            bodyFontFamily = state.bodyFontFamily.trim(),
            codeFontFamily = state.codeFontFamily.trim(),
            fontScale = state.fontScale.coerceIn(MIN_FONT_SCALE, MAX_FONT_SCALE),
            maxContentWidth = state.maxContentWidth.coerceIn(MIN_CONTENT_WIDTH, MAX_CONTENT_WIDTH),
            accentHeadings = if (state.accentsInitialized) {
                state.accentHeadings
            } else {
                state.profile == MarkdownNeatProfile.SPACIOUS
            },
            accentBold = state.accentsInitialized && state.accentBold,
            accentInlineCode = state.accentsInitialized && state.accentInlineCode,
            accentsInitialized = true,
        )
    }
}

internal class MarkdownNeatThemeConverter : Converter<MarkdownNeatTheme>() {
    override fun fromString(value: String): MarkdownNeatTheme = when (value.lowercase()) {
        "dark", "github dark" -> MarkdownNeatTheme.DARK
        else -> MarkdownNeatTheme.LIGHT
    }

    override fun toString(value: MarkdownNeatTheme): String = value.wireValue
}

internal class MarkdownNeatProfileConverter : Converter<MarkdownNeatProfile>() {
    override fun fromString(value: String): MarkdownNeatProfile = when (value.lowercase()) {
        "spacious" -> MarkdownNeatProfile.SPACIOUS
        else -> MarkdownNeatProfile.COMPACT
    }

    override fun toString(value: MarkdownNeatProfile): String = value.wireValue
}
