package dev.hyunelab.markneat.editor

import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.DumbAware
import com.intellij.testFramework.LightVirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class MarkNeatFileEditorProviderTest : BasePlatformTestCase() {
    fun testProviderContract() {
        val provider = MarkNeatFileEditorProvider()

        assertTrue(provider.accept(project, LightVirtualFile("README.md", PlainTextLanguage.INSTANCE, "# MarkNeat")))
        assertTrue(provider.accept(project, LightVirtualFile("guide.markdown", PlainTextLanguage.INSTANCE, "# Guide")))
        assertFalse(provider.accept(project, LightVirtualFile("notes.txt", PlainTextLanguage.INSTANCE, "notes")))
        assertEquals(FileEditorPolicy.HIDE_OTHER_EDITORS, provider.policy)
        assertTrue((provider as Any) is DumbAware)
    }
}
