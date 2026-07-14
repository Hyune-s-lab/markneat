# Roadmap

## 0.1.x — Markdown Viewer

- Ship a lightweight, read-only Markdown viewer for JetBrains IDEs.
- Render GitHub Flavored Markdown with GitHub Light and GitHub Dark themes, independent of the IDE theme.
- Support automatic refresh, relative images and links, offline rendering, sanitization, and a JCEF fallback.

## 0.2.x — Mermaid and Runtime Compatibility

- Render Mermaid 11.16.0 in fenced Markdown blocks and standalone `.mmd` and `.mermaid` files.
- Bundle the required Mermaid runtime and a curated Material Design Icons subset for offline use.
- Provide `mdi:account`, `mdi:api`, `mdi:cloud`, `mdi:cog`, `mdi:database`, `mdi:message-processing`, `mdi:server`, and `mdi:web` offline.
- Isolate diagram failures and provide local diagnostics.
- Load Mermaid only when a document needs it, through the shared optional-runtime boundary for future diagram engines.
- Load JCEF correctly on modular IDE runtimes and use IntelliJ Platform 2026.1 as the compatibility baseline, where the exclusive viewer policy is stable.

## 0.3.x — Appearance

- Add Compact and Spacious reading profiles on the GitHub Light and GitHub Dark base palettes, with a distinct Spacious heading accent.
- Choose body and code fonts independently from curated sets of installed system fonts.
- Scale document text from 90%–180% without scaling image or diagram geometry.
- Configure a profile-independent content width from 768–1536 px or use the full available width.
- Preview theme, profile, and typography changes before applying them.

## 0.4.x — Code Highlighting and Editing Handoff

- Add offline syntax highlighting with a deliberately scoped set of language grammars, loaded through the shared optional-runtime boundary.
- Add an Open as Text action that opens the underlying file in the IDE text editor without disabling MarkdownNeat.
- Preserve heading anchors when a link opens another Markdown file.
- Detect viewer bootstrap failures, log them, and fall back to the plain-text viewer.
- Verify non-ASCII path rendering on Windows.

## Later

- Add D2 and Excalidraw through the shared optional-runtime boundary.
- Add bundled themes beyond GitHub Light and GitHub Dark.
- Add Markdown extensions such as footnotes, alerts, and math rendering where they remain lightweight and safe.
- Add optional custom CSS overrides and a copyable AI usage guide.
- Bundle a curated subset of frequently used diagram icons for offline and deterministic rendering.
- Explore a browser extension that reuses the same renderer for Markdown files outside the IDE.

Published version history belongs in GitHub Releases.
