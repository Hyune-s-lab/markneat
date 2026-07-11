# Architecture

This document MUST describe the current MarkNeat architecture. Provisional choices are marked as candidates.

```text
Markdown file
    -> thin Kotlin host
    -> JCEF
    -> deep TypeScript renderer
        -> Markdown
        -> GitHub themes
```

## Ownership

Kotlin MUST own only JetBrains extension registration, file events, JCEF lifecycle, bridge transport, IDE navigation, and platform fallback behavior.

TypeScript MUST own Markdown parsing, sanitization, rendering profiles, themes, typography, diagram engines, DOM updates, render scheduling, error isolation, and renderer diagnostics.

The bridge MUST remain small:

```text
Kotlin -> TypeScript: render(request)
TypeScript -> Kotlin: ready | rendered | openLink | error
```

## Current Stack

| Area | Choice |
| --- | --- |
| Host | Kotlin 2.1.20 + IntelliJ Platform Gradle Plugin 2.18.1 |
| IDE integration | `FileEditorProvider` with a read-only JCEF editor and plain-text fallback |
| Compatibility baseline | IntelliJ Platform 2025.2 (`since-build` 252) |
| Renderer | TypeScript 7.0.2 bundled by Vite 8.1.4 |
| Markdown | Marked 18.0.6 |
| Sanitization | DOMPurify 3.4.11 plus a restrictive Content Security Policy |
| Initial styling | github-markdown-css 5.9.0 with fixed GitHub Light and GitHub Dark output |
| Renderer delivery | One self-contained HTML resource bundled inside the plugin |
| Plugin ID | `dev.hyunelab.markneat` |

## Planned Candidates

| Area | Candidate |
| --- | --- |
| First diagram engine | Mermaid 11.16.0 |
| Mermaid icons | Curated, bundled Iconify packs |
| Rendering profiles | Standard and Enhanced |
| Typography | Configurable body and code font family/size |

## Constraints

- Core rendering MUST work offline.
- Content MUST be sanitized and MUST NOT write to the project.
- Renderer resources MUST be lazy-loaded and disposed with the viewer.
- Diagram failures MUST remain local to their block.
- Size, startup, render time, and memory MUST be measured.

The current lightweight baseline and reproduction command are recorded in [Performance](performance.md).

## Open Decisions

- Syntax highlighter
- Profile, theme, and typography contract
- Font discovery and fallback behavior
- D2 runtime and syntax convention
- Excalidraw read-only workflow
- Initial Iconify packs
- Render diagnostic interface and AI usage guide format
- Marketplace identity and logo
