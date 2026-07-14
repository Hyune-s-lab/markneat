package dev.hyunelab.markdownneat.editor

import dev.hyunelab.markdownneat.settings.MarkdownNeatProfile
import dev.hyunelab.markdownneat.settings.MarkdownNeatSettings
import dev.hyunelab.markdownneat.settings.MarkdownNeatTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class RendererRequestJsonTest {
    @Test
    fun `serializes the complete appearance contract safely`() {
        val settings = MarkdownNeatSettings().apply {
            updateAppearance(
                theme = MarkdownNeatTheme.DARK,
                profile = MarkdownNeatProfile.SPACIOUS,
                bodyFontFamily = "Font \"One\"",
                codeFontFamily = "Mono\\Code",
                fontScale = 130,
                maxContentWidth = 1280,
                useFullWidth = true,
                accentHeadings = true,
            )
        }

        val request = rendererRequestJson(
            source = "# Read\n",
            baseUrl = "file:///README.md",
            documentType = "markdown",
            settings = settings,
        )

        assertEquals(
            """{"version":4,"source":"# Read\n","baseUrl":"file:///README.md","documentType":"markdown","theme":"dark","profile":"spacious","bodyFontFamily":"Font \"One\"","codeFontFamily":"Mono\\Code","fontScale":130,"maxContentWidth":null,"accentHeadings":true,"accentBold":false,"accentInlineCode":false}""",
            request,
        )

        settings.updateAppearance(settings.appearance.copy(useFullWidth = false, accentInlineCode = true))
        assertEquals(
            """{"version":4,"source":"# Read\n","baseUrl":"file:///README.md","documentType":"markdown","theme":"dark","profile":"spacious","bodyFontFamily":"Font \"One\"","codeFontFamily":"Mono\\Code","fontScale":130,"maxContentWidth":1280,"accentHeadings":true,"accentBold":false,"accentInlineCode":true}""",
            rendererRequestJson(
                source = "# Read\n",
                baseUrl = "file:///README.md",
                documentType = "markdown",
                settings = settings,
            ),
        )
    }
}
