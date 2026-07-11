package dev.hyunelab.markdownneat

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PluginDescriptorDependencyTest {
    @Test
    fun `declares the JCEF module required by the editor`() {
        val descriptor = requireNotNull(javaClass.getResource("/META-INF/plugin.xml")).readText()

        assertTrue(
            "<depends optional=\"true\" config-file=\"jcef.xml\">intellij.libraries.jcef</depends>" in descriptor,
            "JCEF API classes must be visible when the IDE provides the JCEF module",
        )
        assertNotNull(javaClass.getResource("/META-INF/jcef.xml"))
    }
}
