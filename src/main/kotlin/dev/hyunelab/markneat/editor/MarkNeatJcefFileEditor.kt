package dev.hyunelab.markneat.editor

import com.intellij.ide.BrowserUtil
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.application.ApplicationManager
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
import com.intellij.ui.JBColor
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefBrowserBase
import com.intellij.ui.jcef.JBCefJSQuery
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import java.beans.PropertyChangeListener
import java.net.URI
import javax.swing.JComponent

internal class MarkNeatJcefFileEditor(
    private val project: Project,
    private val file: VirtualFile,
) : UserDataHolderBase(), FileEditor {
    private val document = requireNotNull(FileDocumentManager.getInstance().getDocument(file))
    private val browser = JBCefBrowser()
    private val readyQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)
    private val openLinkQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)
    private val renderedQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)
    private val errorQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)
    private var rendererReady = false

    init {
        Disposer.register(this, browser)
        Disposer.register(this, readyQuery)
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
            LafManagerListener.TOPIC,
            LafManagerListener { render() },
        )

        browser.jbCefClient.addLoadHandler(object : CefLoadHandlerAdapter() {
            override fun onLoadEnd(browser: CefBrowser, frame: CefFrame, httpStatusCode: Int) {
                if (frame.isMain) {
                    connectRenderer()
                }
            }
        }, browser.cefBrowser)

        val viewerHtml = checkNotNull(javaClass.getResource("/markneat/viewer.html")) {
            "Missing bundled renderer"
        }.readText()
        browser.loadHTML(viewerHtml, file.url)
    }

    override fun getComponent(): JComponent = browser.component
    override fun getPreferredFocusedComponent(): JComponent = browser.component
    override fun getName(): String = "MarkNeat"
    override fun getFile(): VirtualFile = file
    override fun setState(state: FileEditorState) = Unit
    override fun getState(level: FileEditorStateLevel): FileEditorState = FileEditorState.INSTANCE
    override fun isModified(): Boolean = false
    override fun isValid(): Boolean = file.isValid && !project.isDisposed
    override fun addPropertyChangeListener(listener: PropertyChangeListener) = Unit
    override fun removePropertyChangeListener(listener: PropertyChangeListener) = Unit
    override fun dispose() = Unit

    private fun connectRenderer() {
        val script = """
            window.markneat.connect({
              ready: function() { ${readyQuery.inject("'ready'")} },
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
        val theme = if (JBColor.isBright()) "light" else "dark"
        val request = """
            {"version":1,"source":${document.text.toJsonString()},"baseUrl":${file.url.toJsonString()},"theme":"$theme"}
        """.trimIndent()
        browser.cefBrowser.executeJavaScript("window.markneat.render($request);", file.url, 0)
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
        val LOG = Logger.getInstance(MarkNeatJcefFileEditor::class.java)
    }
}
