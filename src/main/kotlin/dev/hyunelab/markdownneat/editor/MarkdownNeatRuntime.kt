package dev.hyunelab.markdownneat.editor

internal fun markdownNeatRuntimeScript(runtimeName: String): String {
    if (!RUNTIME_NAME.matches(runtimeName)) {
        return runtimeFailureScript(runtimeName, "Unsupported runtime name")
    }
    val runtime = runCatching {
        checkNotNull(
            MarkdownNeatRuntimeAnchor::class.java.getResource("/markdownneat/runtime-$runtimeName.js"),
        ) { "Missing bundled runtime: $runtimeName" }.readText()
    }.getOrElse { error ->
        return runtimeFailureScript(runtimeName, error.message ?: "Missing bundled runtime")
    }
    return """
        try {
          $runtime
          window.markdownNeat.runtimeReady(${runtimeName.toJsonString()});
        } catch (error) {
          window.markdownNeat.runtimeFailed(
            ${runtimeName.toJsonString()},
            String(error && error.message ? error.message : error)
          );
        }
    """.trimIndent()
}

private fun runtimeFailureScript(runtimeName: String, message: String): String =
    "window.markdownNeat.runtimeFailed(${runtimeName.toJsonString()}, ${message.toJsonString()});"

private object MarkdownNeatRuntimeAnchor

private val RUNTIME_NAME = Regex("[a-z0-9-]+")
