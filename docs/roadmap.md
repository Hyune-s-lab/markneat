# Roadmap

## 0.1.0 — Markdown Viewer

- Ship an installable, lightweight, read-only Markdown preview for JetBrains IDEs.
- Render GitHub Flavored Markdown with fixed GitHub Light/Dark styling.
- Support relative images and links, automatic refresh, offline rendering, sanitization, and JCEF fallback.

## 0.2.0 — Mermaid

- Render Mermaid 11.16.0.
- Render Mermaid blocks in Markdown and preview standalone `.mmd` and `.mermaid` files.
- Bundle all required assets and work offline.
- Bundle a small set of licensed Iconify packs and load icon data only when used.
- Isolate diagram errors and expose useful diagnostics.
- Introduce the minimal renderer seam needed by future diagram engines.
- Provide self-contained Markdown and Mermaid preview without depending on other preview plugins.

## 0.3.0 — Appearance

- Add Standard (compact and restrained) and Enhanced (spacious and colorful) rendering profiles.
- Add selectable bundled themes beyond the GitHub defaults.
- Configure body and code font family and size.
- Preview appearance changes before applying them.
- Apply a shared profile, theme, and typography contract to Markdown and Mermaid output.

## Later

- Add D2 and Excalidraw renderers through the shared diagram renderer seam.
- Load each diagram runtime only when a document uses it.
- Define the read-only Excalidraw file and embedding workflow.
- Add a copyable AI usage guide when the workflow needs one.

Published version history belongs in GitHub Releases.
