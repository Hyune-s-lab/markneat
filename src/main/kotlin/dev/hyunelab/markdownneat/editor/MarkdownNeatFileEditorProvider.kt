package dev.hyunelab.markdownneat.editor

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileEditor.TextEditorWithPreview
import com.intellij.openapi.fileEditor.TextEditorWithPreviewProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JBCefApp

class MarkdownNeatFileEditorProvider :
    TextEditorWithPreviewProvider(MarkdownNeatPreviewFileEditorProvider()), DumbAware {

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_OTHER_EDITORS

    override fun createSplitEditor(firstEditor: TextEditor, secondEditor: FileEditor): FileEditor =
        TextEditorWithPreview(
            firstEditor,
            secondEditor,
            "MarkdownNeat",
            TextEditorWithPreview.Layout.SHOW_PREVIEW,
        )
}

internal class MarkdownNeatPreviewFileEditorProvider : FileEditorProvider, DumbAware {
    override fun accept(project: Project, file: VirtualFile): Boolean =
        !file.isDirectory && file.extension?.lowercase() in MARKDOWN_EXTENSIONS

    override fun createEditor(project: Project, file: VirtualFile): FileEditor =
        if (JBCefApp.isSupported()) {
            MarkdownNeatJcefFileEditor(project, file)
        } else {
            MarkdownNeatFallbackFileEditor(file)
        }

    override fun getEditorTypeId(): String = "markdownneat-preview"

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_DEFAULT_EDITOR

    private companion object {
        val MARKDOWN_EXTENSIONS = setOf("md", "markdown", "mermaid", "mmd")
    }
}
