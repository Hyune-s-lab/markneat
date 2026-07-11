package dev.hyunelab.markdownneat.settings

import javax.swing.JComponent

internal interface MarkdownNeatSettingsPreview {
    val component: JComponent

    fun render(appearance: MarkdownNeatAppearance)

    fun dispose()
}
