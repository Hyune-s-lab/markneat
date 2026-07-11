package dev.hyunelab.markdownneat.settings

import com.intellij.util.messages.Topic

fun interface MarkdownNeatSettingsListener {
    fun settingsChanged()

    companion object {
        @field:Topic.AppLevel
        val TOPIC: Topic<MarkdownNeatSettingsListener> = Topic.create(
            "MarkdownNeat settings changed",
            MarkdownNeatSettingsListener::class.java,
        )
    }
}
