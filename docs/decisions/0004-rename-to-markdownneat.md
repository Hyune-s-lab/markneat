# ADR-0004: Rename the Plugin to MarkdownNeat

- **Status:** Accepted
- **Date:** 2026-07-11

## Context

`MarkNeat` did not make the Markdown scope immediately clear and its word boundary was difficult to read.
The project has not published a Marketplace release, so its plugin identity and persisted settings do not require backward-compatible migration.

## Decision

The public product and plugin name MUST be `MarkdownNeat`.
The GitHub repository, Gradle project, and npm package MUST use `markdown-neat`.
The plugin ID and Kotlin package MUST use `dev.hyunelab.markdownneat`.
Internal identifiers MUST follow the naming convention of their environment, including `markdownNeat` for the JavaScript bridge and `markdownneat` for lowercase-only resource and persistence identifiers.

The project MUST NOT migrate the unpublished `MarkNeat` plugin ID, editor identity, or settings file.

## Consequences

- Development installations using the old plugin ID are treated as a separate plugin and should be removed before testing MarkdownNeat.
- The first Marketplace release establishes `dev.hyunelab.markdownneat` as the stable plugin identity.
- Any identity change after publication will require an explicit compatibility and Marketplace migration decision.
