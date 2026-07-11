package dev.hyunelab.markneat.editor

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JBCefApp

class MarkNeatFileEditorProvider : FileEditorProvider, DumbAware {
    override fun accept(project: Project, file: VirtualFile): Boolean =
        !file.isDirectory && file.extension?.lowercase() in MARKDOWN_EXTENSIONS

    override fun createEditor(project: Project, file: VirtualFile): FileEditor =
        if (JBCefApp.isSupported()) {
            MarkNeatJcefFileEditor(project, file)
        } else {
            MarkNeatFallbackFileEditor(file)
        }

    override fun getEditorTypeId(): String = "markneat-preview"

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_OTHER_EDITORS

    private companion object {
        val MARKDOWN_EXTENSIONS = setOf("md", "markdown")
    }
}
