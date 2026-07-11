# ADR-0003: Renderer Runtimes Are Loaded on Demand

- **Status:** Accepted
- **Date:** 2026-07-11

## Context

Diagram engines are much larger than the core Markdown renderer, but most viewed documents do not contain diagrams.

Bundling every engine into the initial JCEF page would increase startup work and memory use for ordinary Markdown files.

The Kotlin host must also remain stable as renderer capabilities evolve.

Engine-specific loading protocols would force JetBrains integration code to change whenever TypeScript adds another renderer.

## Decision

The core renderer MUST remain a self-contained page, and optional renderer runtimes MUST be separate bundled resources loaded only when a document needs them.

- TypeScript MUST decide which runtime is needed and own its lifecycle, diagnostics, and rendering behavior.
- Kotlin MUST expose only a generic, name-based loader for bundled `runtime-<name>.js` resources.
- Runtime names MUST be restricted before Kotlin resolves a classpath resource.
- Every runtime and its required assets MUST be bundled for offline use.
- The core and optional runtime sizes MUST be measured separately.

Mermaid 11.16.0 is the first runtime delivered through this boundary, and its curated Material Design Icons subset is registered by the Mermaid runtime itself.

## Consequences

- Plain Markdown does not parse or execute the Mermaid bundle.
- The plugin distribution is larger, while the initial viewer remains close to its pre-Mermaid size.
- Future diagram engines can use the same host bridge without adding engine-specific Kotlin code.
- A missing or invalid runtime can fail locally without replacing the rest of the rendered document.
- Runtime delivery adds an asynchronous boundary that requires stale-render protection and explicit automated tests.
