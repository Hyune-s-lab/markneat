package dev.hyunelab.markdownneat.editor

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.FileEditorStateLevel
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefBrowserBase
import com.intellij.ui.jcef.JBCefJSQuery
import dev.hyunelab.markdownneat.settings.MarkdownNeatSettings
import dev.hyunelab.markdownneat.settings.MarkdownNeatSettingsListener
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import java.beans.PropertyChangeListener
import java.net.URI
import javax.swing.JComponent

internal class MarkdownNeatJcefFileEditor(
    private val project: Project,
    private val file: VirtualFile,
) : UserDataHolderBase(), FileEditor {
    private val document = requireNotNull(FileDocumentManager.getInstance().getDocument(file))
    private val browser = JBCefBrowser()
    private val readyQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)
    private val loadRuntimeQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)
    private val openLinkQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)
    private val renderedQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)
    private val errorQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)
    private var rendererReady = false
    @Volatile
    private var disposed = false

    init {
        Disposer.register(this, browser)
        Disposer.register(this, readyQuery)
        Disposer.register(this, loadRuntimeQuery)
        Disposer.register(this, openLinkQuery)
        Disposer.register(this, renderedQuery)
        Disposer.register(this, errorQuery)

        readyQuery.addHandler {
            ApplicationManager.getApplication().invokeLater {
                if (isValid) {
                    rendererReady = true
                    render()
                }
            }
            JBCefJSQuery.Response(null)
        }
        loadRuntimeQuery.addHandler { runtimeName ->
            loadRuntime(runtimeName)
            JBCefJSQuery.Response(null)
        }
        openLinkQuery.addHandler { href ->
            ApplicationManager.getApplication().invokeLater {
                if (isValid) {
                    openLink(href)
                }
            }
            JBCefJSQuery.Response(null)
        }
        renderedQuery.addHandler { JBCefJSQuery.Response(null) }
        errorQuery.addHandler { message ->
            LOG.warn("Renderer error for ${file.path}: $message")
            JBCefJSQuery.Response(null)
        }

        document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                render()
            }
        }, this)

        ApplicationManager.getApplication().messageBus.connect(this).subscribe(
            MarkdownNeatSettingsListener.TOPIC,
            MarkdownNeatSettingsListener { scheduleSettingsRender() },
        )

        browser.jbCefClient.addLoadHandler(object : CefLoadHandlerAdapter() {
            override fun onLoadEnd(browser: CefBrowser, frame: CefFrame, httpStatusCode: Int) {
                if (frame.isMain && isValid) {
                    connectRenderer()
                }
            }
        }, browser.cefBrowser)

        val viewerHtml = checkNotNull(javaClass.getResource("/markdownneat/viewer.html")) {
            "Missing bundled renderer"
        }.readText()
        browser.loadHTML(viewerHtml, file.url)
    }

    override fun getComponent(): JComponent = browser.component
    override fun getPreferredFocusedComponent(): JComponent = browser.component
    override fun getName(): String = "MarkdownNeat"
    override fun getFile(): VirtualFile = file
    override fun setState(state: FileEditorState) = Unit
    override fun getState(level: FileEditorStateLevel): FileEditorState = FileEditorState.INSTANCE
    override fun isModified(): Boolean = false
    override fun isValid(): Boolean = !disposed && file.isValid && !project.isDisposed
    override fun addPropertyChangeListener(listener: PropertyChangeListener) = Unit
    override fun removePropertyChangeListener(listener: PropertyChangeListener) = Unit
    override fun dispose() {
        disposed = true
        rendererReady = false
    }

    private fun connectRenderer() {
        val script = """
            window.markdownNeat.connect({
              ready: function() { ${readyQuery.inject("'ready'")} },
              loadRuntime: function(name) { ${loadRuntimeQuery.inject("name")} },
              openLink: function(href) { ${openLinkQuery.inject("href")} },
              rendered: function() { ${renderedQuery.inject("'rendered'")} },
              error: function(message) { ${errorQuery.inject("message")} }
            });
        """.trimIndent()
        browser.cefBrowser.executeJavaScript(script, file.url, 0)
    }

    private fun render() {
        if (!rendererReady || !isValid) {
            return
        }
        val settings = MarkdownNeatSettings.getInstance()
        val documentType = if (file.extension?.lowercase() in MERMAID_EXTENSIONS) "mermaid" else "markdown"
        val request = rendererRequestJson(document.text, file.url, documentType, settings)
        browser.cefBrowser.executeJavaScript("window.markdownNeat.render($request);", file.url, 0)
    }

    private fun loadRuntime(runtimeName: String) {
        ApplicationManager.getApplication().executeOnPooledThread {
            val script = markdownNeatRuntimeScript(runtimeName)
            ApplicationManager.getApplication().invokeLater({
                if (isValid) {
                    browser.cefBrowser.executeJavaScript(script, file.url, 0)
                }
            }, ModalityState.any())
        }
    }

    private fun scheduleSettingsRender() {
        ApplicationManager.getApplication().invokeLater({
            if (isValid) {
                render()
                browser.component.repaint()
            }
        }, ModalityState.any())
    }

    private fun openLink(href: String) {
        val uri = runCatching { URI(href) }.getOrNull() ?: return
        when (uri.scheme?.lowercase()) {
            "http", "https", "mailto" -> BrowserUtil.browse(uri)
            "file" -> {
                val fileUri = URI(uri.scheme, uri.authority, uri.path, uri.query, null)
                VirtualFileManager.getInstance().findFileByUrl(fileUri.toString())?.let { target ->
                    OpenFileDescriptor(project, target).navigate(true)
                }
            }
        }
    }

    private companion object {
        val LOG = Logger.getInstance(MarkdownNeatJcefFileEditor::class.java)
        val MERMAID_EXTENSIONS = setOf("mermaid", "mmd")
    }
}
