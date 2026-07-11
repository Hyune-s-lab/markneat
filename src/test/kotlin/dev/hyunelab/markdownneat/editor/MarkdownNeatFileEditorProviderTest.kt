package dev.hyunelab.markdownneat.editor

import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.DumbAware
import com.intellij.testFramework.LightVirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class MarkdownNeatFileEditorProviderTest : BasePlatformTestCase() {
    fun testProviderContract() {
        val provider = MarkdownNeatFileEditorProvider()

        assertTrue(provider.accept(project, LightVirtualFile("README.md", PlainTextLanguage.INSTANCE, "# MarkdownNeat")))
        assertTrue(provider.accept(project, LightVirtualFile("guide.markdown", PlainTextLanguage.INSTANCE, "# Guide")))
        assertFalse(provider.accept(project, LightVirtualFile("notes.txt", PlainTextLanguage.INSTANCE, "notes")))
        assertEquals(FileEditorPolicy.HIDE_OTHER_EDITORS, provider.policy)
        assertTrue((provider as Any) is DumbAware)
    }
}
