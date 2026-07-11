import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
}

dependencies {
    testImplementation(kotlin("test"))

    intellijPlatform {
        intellijIdea("2025.2.6.2")
        testFramework(TestFrameworkType.Platform)
    }
}

kotlin {
    jvmToolchain(21)
}

val npmInstall by tasks.registering(Exec::class) {
    inputs.files("package.json", "package-lock.json")
    outputs.dir("node_modules")
    commandLine("npm", "ci")
}

val typecheckRenderer by tasks.registering(Exec::class) {
    dependsOn(npmInstall)
    inputs.dir("renderer")
    inputs.file("tsconfig.json")
    commandLine("npm", "run", "typecheck:renderer")
}

val testRenderer by tasks.registering(Exec::class) {
    dependsOn(npmInstall)
    inputs.dir("renderer")
    inputs.file("vite.config.ts")
    commandLine("npm", "run", "test:renderer")
}

val buildRenderer by tasks.registering(Exec::class) {
    dependsOn(npmInstall)
    inputs.dir("renderer")
    inputs.files("tsconfig.json", "vite.config.ts")
    outputs.file(layout.buildDirectory.file("generated/renderer/index.html"))
    commandLine("npm", "run", "build:renderer")
}

val prepareRendererResources by tasks.registering(Copy::class) {
    dependsOn(buildRenderer, npmInstall)
    into(layout.buildDirectory.dir("generated/rendererResources"))
    from(layout.buildDirectory.file("generated/renderer/index.html")) {
        into("markdownneat")
        rename { "viewer.html" }
    }
    from("LICENSE") {
        into("META-INF")
        rename { "MARKDOWNNEAT-LICENSE.txt" }
    }
    from("THIRD_PARTY_NOTICES.md") {
        into("META-INF")
    }
    from("node_modules/marked/LICENSE") {
        into("META-INF/licenses")
        rename { "marked-MIT.txt" }
    }
    from("node_modules/dompurify/LICENSE") {
        into("META-INF/licenses")
        rename { "DOMPurify-Apache-2.0.txt" }
    }
    from("node_modules/github-markdown-css/license") {
        into("META-INF/licenses")
        rename { "github-markdown-css-MIT.txt" }
    }
}

sourceSets.main {
    resources.srcDir(layout.buildDirectory.dir("generated/rendererResources"))
}

tasks.processResources {
    dependsOn(prepareRendererResources)
}

tasks.check {
    dependsOn(testRenderer, typecheckRenderer)
}

intellijPlatform {
    pluginConfiguration {
        id = "dev.hyunelab.markdownneat"
        name = "MarkdownNeat"
        version = project.version.toString()

        description = """
            <p>A lightweight, read-only Markdown viewer for JetBrains IDEs.</p>
            <p>MarkdownNeat renders GitHub-style Markdown locally in a focused preview.</p>
        """.trimIndent()

        ideaVersion {
            sinceBuild = "252"
        }

        vendor {
            name = "Hyune-s-lab"
            url = "https://github.com/Hyune-s-lab"
        }
    }
}
