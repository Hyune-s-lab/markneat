package dev.hyunelab.markdownneat.editor

import dev.hyunelab.markdownneat.settings.MarkdownNeatSettings

internal fun rendererRequestJson(
    source: String,
    baseUrl: String,
    documentType: String,
    settings: MarkdownNeatSettings,
): String =
    """{"version":3,"source":${source.toJsonString()},"baseUrl":${baseUrl.toJsonString()},"documentType":"$documentType","theme":"${settings.theme.wireValue}","profile":"${settings.profile.wireValue}","bodyFontFamily":${settings.bodyFontFamily.toJsonString()},"codeFontFamily":${settings.codeFontFamily.toJsonString()},"fontScale":${settings.fontScale},"maxContentWidth":${if (settings.useFullWidth) "null" else settings.maxContentWidth}}"""
