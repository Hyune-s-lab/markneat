package dev.hyunelab.markdownneat.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

enum class MarkdownNeatTheme(
    val displayName: String,
    val wireValue: String,
) {
    LIGHT("Light", "light"),
    DARK("Dark", "dark"),
    ;

    override fun toString(): String = displayName
}

@State(
    name = "dev.hyunelab.markdownneat.settings.MarkdownNeatSettings",
    storages = [Storage("markdownneat.xml")],
)
class MarkdownNeatSettings : PersistentStateComponent<MarkdownNeatSettings.SettingsState> {
    data class SettingsState(
        var theme: MarkdownNeatTheme = MarkdownNeatTheme.LIGHT,
    )

    private var settingsState = SettingsState()

    val theme: MarkdownNeatTheme
        get() = settingsState.theme

    fun updateTheme(theme: MarkdownNeatTheme): Boolean {
        if (settingsState.theme == theme) {
            return false
        }
        settingsState.theme = theme
        return true
    }

    override fun getState(): SettingsState = settingsState

    override fun loadState(state: SettingsState) {
        settingsState = state
    }

    companion object {
        fun getInstance(): MarkdownNeatSettings =
            ApplicationManager.getApplication().getService(MarkdownNeatSettings::class.java)
    }
}
