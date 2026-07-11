# Architecture

This document MUST describe the current MarkNeat architecture. Provisional choices are marked as candidates.

```text
Markdown file
    -> thin Kotlin host
    -> JCEF
    -> deep TypeScript renderer
        -> Markdown
        -> themes
        -> diagram engines
```

## Ownership

Kotlin MUST own only JetBrains extension registration, file events, JCEF lifecycle, bridge transport, IDE navigation, and platform fallback behavior.

TypeScript MUST own Markdown parsing, sanitization, rendering profiles, themes, typography, diagram engines, DOM updates, render scheduling, error isolation, and renderer diagnostics.

The bridge MUST remain small:

```text
Kotlin -> TypeScript: render(request)
TypeScript -> Kotlin: ready | rendered | openLink | error
```

## Current Candidates

| Area | Candidate |
| --- | --- |
| Host | Kotlin + IntelliJ Platform Gradle Plugin 2.x |
| Viewer | JCEF |
| Renderer | TypeScript |
| Markdown | `markdown-it` or `remark-gfm` |
| First diagram engine | Mermaid 11.16.0 |
| Mermaid icons | Curated, bundled Iconify packs |
| Rendering profiles | Standard and Enhanced |
| Initial themes | GitHub Light and GitHub Dark |
| Typography | Configurable body and code font family/size |
| Plugin ID | `dev.hyunelab.markneat` |

## Constraints

- Core rendering MUST work offline.
- Content MUST be sanitized and MUST NOT write to the project.
- Renderer resources MUST be lazy-loaded and disposed with the viewer.
- Diagram failures MUST remain local to their block.
- Size, startup, render time, and memory MUST be measured.

## Open Decisions

- `FileEditorProvider` or a dedicated preview action
- `markdown-it` or `remark-gfm`
- First supported IntelliJ version
- Syntax highlighter
- Profile, theme, and typography contract
- Font discovery and fallback behavior
- D2 runtime and syntax convention
- Excalidraw read-only workflow
- Initial Iconify packs and third-party license notices
- Render diagnostic interface and AI usage guide format
- Marketplace identity and logo
