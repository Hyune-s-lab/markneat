package dev.hyunelab.markneat.editor

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JBCefApp

class MarkNeatFileEditorProvider : FileEditorProvider {
    override fun accept(project: Project, file: VirtualFile): Boolean =
        !file.isDirectory && file.extension?.lowercase() in MARKDOWN_EXTENSIONS

    override fun createEditor(project: Project, file: VirtualFile): FileEditor =
        if (JBCefApp.isSupported()) {
            MarkNeatJcefFileEditor(project, file)
        } else {
            MarkNeatFallbackFileEditor(file)
        }

    override fun getEditorTypeId(): String = "markneat-preview"

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR

    private companion object {
        val MARKDOWN_EXTENSIONS = setOf("md", "markdown")
    }
}
