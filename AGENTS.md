# MarkdownNeat Agent Guidelines

## Product

- MarkdownNeat MUST remain a lightweight, read-only Markdown viewer for JetBrains IDEs.
- Direct human use SHOULD remain intuitive.
- MarkdownNeat MUST NOT add editing, autocomplete, inspections, refactoring, WYSIWYG, or background indexing.
- Core rendering MUST work offline and MUST NOT depend on a runtime CDN.

## Architecture

- Kotlin MUST contain only stable JetBrains Platform and JCEF integration.
- Product behavior and volatile rendering logic MUST live in TypeScript.
- The Kotlin/TypeScript bridge MUST remain small, serializable, and versioned.
- Renderer and diagram resources MUST load only when needed and MUST be released with the viewer.
- Viewing a document MUST NOT write to the project.
- Markdown and diagram input MUST be treated as untrusted content and sanitized.

## Language

- Public content MUST be written in English, including documentation, UI text, Git history, issues, PRs, and release notes.
- Private discussion with the project owner MAY be written in Korean.
- Code identifiers and established technical terms SHOULD keep their original names.

## Documentation

- `README.md` MUST remain a concise product entry point and describe only key product capabilities.
- `docs/roadmap.md` MUST own the feature matrix and future scope.
- `docs/architecture.md` MUST always describe the current design and open decisions.
- Accepted architecture decisions MUST be recorded under `docs/decisions/` without turning `docs/architecture.md` into a decision log.
- GitHub Releases MUST own published release notes and version history.
- GitHub Release titles MUST use the `v<version>` form, such as `v0.1.0`.
- Release notes MUST group changes by change type, using Spring Boot-style emoji headings such as `:star: New Features`, `:lady_beetle: Bug Fixes`, `:notebook_with_decorative_cover: Documentation`, and `:hammer: Dependency Upgrades`.
- All feature changes in one release MUST share a single `:star: New Features` section; split sections only when the change type differs.
- Detailed status or decisions MUST NOT be duplicated across documents.

## Verification

- Changes SHOULD be small, runnable, and tested at the closest useful level.
- Renderer tests SHOULD cover valid input, invalid diagrams, and unsafe content.
- Changes affecting lightweight behavior MUST measure the relevant size, startup, or memory impact.
- Releases MUST pass `./gradlew verifyRelease` locally and in CI.
- Manual smoke testing MUST cover only behavior that automation cannot verify.
- Reports MUST list only checks that were actually run and MUST disclose remaining risk.

## Git

- Work MUST happen on a focused branch and reach `main` through a pull request.
- Pull requests MUST be opened ready for review, MUST NOT be drafts, and MUST assign `Hyune-c`.
- AI agents MUST NOT merge pull requests. Only the project owner may merge.
- The first JetBrains Marketplace listing MUST be created through the Marketplace UI.
- Unrelated changes MUST NOT be mixed in one commit.
- Generated output, IDE caches, and local environment files MUST NOT be committed.
- Commit, push, PR creation, and publishing MUST NOT happen without the project owner's explicit request.
