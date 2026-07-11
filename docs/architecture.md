# Architecture

This document MUST describe the current MarkdownNeat architecture. Provisional choices are marked as candidates.

```text
Markdown file
    -> thin Kotlin host
    -> JCEF
    -> deep TypeScript renderer
        -> Markdown
        -> GitHub themes
```

## Ownership

Kotlin MUST own only JetBrains extension registration, file events, JCEF lifecycle, bridge transport, IDE navigation, Settings UI and persistence, and platform fallback behavior.

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
| IDE integration | Exclusive, dumb-aware `FileEditorProvider` with a read-only JCEF editor and plain-text fallback |
| Compatibility baseline | IntelliJ Platform 2025.2 (`since-build` 252) |
| Renderer | TypeScript 7.0.2 bundled by Vite 8.1.4 |
| Markdown | Marked 18.0.6 |
| Sanitization | DOMPurify 3.4.11 plus a restrictive Content Security Policy |
| Initial styling | github-markdown-css 5.9.0 with fixed GitHub Light and GitHub Dark output |
| Theme setting | Application-level Light or Dark selection under Settings > Tools > MarkdownNeat |
| Renderer delivery | One self-contained HTML resource bundled inside the plugin |
| Plugin ID | `dev.hyunelab.markdownneat` |

## Lightweight Baseline

Measurements MUST be repeated for releases that materially change the renderer or host lifecycle.

Measured on an Apple Silicon Mac with Java 21 and Node.js 22.21.1:

- Plugin distribution: 64.6 KB
- Self-contained renderer: 109.6 KB raw, 28.9 KB gzip
- Renderer module load: 45.6 ms and 4.1 MiB heap
- 100 KiB Markdown fixture: 185.9 ms median, 297.5 ms p95, and 42.1 MiB retained heap for one rendered result

Run `npm run measure:renderer` to reproduce renderer module load, render latency, and retained heap measurements.  
Times and memory are a local baseline, not release budgets.  
They isolate MarkdownNeat's TypeScript renderer in Node.js with JSDOM and do not claim to measure the IDE-owned JCEF runtime.

## Planned Candidates

| Area | Candidate |
| --- | --- |
| First diagram engine | Mermaid 11.16.0 |
| Mermaid icons | Curated, bundled Iconify packs |
| Rendering profiles | Standard and Enhanced |
| Typography | Configurable body and code font family/size |

## Constraints

- Core rendering MUST work offline.
- MarkdownNeat MUST own the normal editor for supported Markdown files while enabled.
- Content MUST be sanitized and MUST NOT write to the project.
- Renderer resources MUST be lazy-loaded and disposed with the viewer.
- Diagram failures MUST remain local to their block.
- Size, startup, render time, and memory MUST be measured.

## Open Decisions

- Syntax highlighter
- Profile, theme, and typography contract
- Font discovery and fallback behavior
- D2 runtime and syntax convention
- Excalidraw read-only workflow
- Initial Iconify packs
- Render diagnostic interface and AI usage guide format
