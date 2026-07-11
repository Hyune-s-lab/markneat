# ADR-0002: MarkdownNeat Owns Markdown Editor Selection

- **Status:** Accepted
- **Date:** 2026-07-11

## Context

JetBrains IDEs can load several `FileEditorProvider` implementations for the same Markdown file.  
When MarkdownNeat was placed after the default editor, the built-in Markdown preview remained selected while a separate MarkdownNeat tab appeared below it.  
MarkdownNeat settings then appeared to have no effect because the visible page belonged to a different renderer.

Project restoration adds another constraint: providers that are not available during indexing can be omitted while the saved editor is reconstructed.  
The built-in Markdown editor may then remain active even after MarkdownNeat becomes available.

## Decision

MarkdownNeat MUST be the exclusive normal editor for `.md` and `.markdown` files while the plugin is enabled.

- Its provider MUST use `HIDE_OTHER_EDITORS`.
- Its provider MUST be `DumbAware` so the same editor is selected during indexing and project restoration.
- Appearance settings MUST always address the renderer visible for a supported Markdown file.

## Consequences

- Users do not need to identify which Markdown preview is active.
- MarkdownNeat behavior is stable during startup and indexing.
- Other Markdown editors are not presented in the normal editor composite while MarkdownNeat is enabled.
- Installing MarkdownNeat deliberately favors read-only viewing over editing for supported Markdown files.
- If another plugin also requests exclusive editor ownership, IntelliJ may retain both providers. This case requires the user to keep only one exclusive viewer enabled.
- `HIDE_OTHER_EDITORS` is experimental in the 2025.2 and 2025.3 platform APIs. Every supported IDE release MUST remain covered by Plugin Verifier.

Keeping MarkdownNeat as a secondary editor was rejected because it made renderer ownership ambiguous.  
`HIDE_DEFAULT_EDITOR` was also insufficient because it removes only the platform default and does not establish MarkdownNeat as the sole provider among other Markdown plugins.
