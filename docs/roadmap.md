# Roadmap

## 0.1.0 — Markdown Viewer

- Ship a lightweight, read-only Markdown viewer for JetBrains IDEs.
- Render GitHub Flavored Markdown with GitHub Light and GitHub Dark themes, independent of the IDE theme.
- Support automatic refresh, relative images and links, offline rendering, sanitization, and a JCEF fallback.

## 0.2.0 — Mermaid

- Render Mermaid 11.16.0 in fenced Markdown blocks and standalone `.mmd` and `.mermaid` files.
- Bundle the required Mermaid runtime and a curated Material Design Icons subset for offline use.
- Provide `mdi:account`, `mdi:api`, `mdi:cloud`, `mdi:cog`, `mdi:database`, `mdi:message-processing`, `mdi:server`, and `mdi:web` offline.
- Isolate diagram failures and provide local diagnostics.
- Load Mermaid only when a document needs it, through the shared optional-runtime boundary for future diagram engines.

## 0.2.1 — JCEF Compatibility

- Declare the optional JCEF module required by newer IDE class loaders without excluding 2025.2-based IDEs.

## 0.3.0 — Appearance

- Add Standard (compact and restrained) and Enhanced (spacious and expressive) rendering profiles.
- Add bundled themes beyond GitHub Light and GitHub Dark.
- Configure body and code font family and 90%–180% scaling.
- Preview theme, profile, and typography changes before applying them.

## Later

- Add D2 and Excalidraw through the shared optional-runtime boundary.
- Add Markdown extensions such as footnotes, alerts, and math rendering where they remain lightweight and safe.
- Add optional custom CSS overrides and a copyable AI usage guide.

Published version history belongs in GitHub Releases.
