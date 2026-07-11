# ADR-0001: TypeScript Renderer with a Thin Kotlin Host

- **Status:** Accepted
- **Date:** 2026-07-11

## Context

JetBrains integration requires JVM code, while MarkNeat's changing product behavior belongs to the browser rendering ecosystem.
Splitting rendering logic across Kotlin and TypeScript would duplicate concepts and make changes cross two implementations.

## Decision

- Kotlin MUST contain only JetBrains Platform and JCEF adapter logic.
- TypeScript MUST contain Markdown, security, themes, diagrams, rendering, and diagnostics.
- New product behavior MUST default to TypeScript unless it requires a JetBrains JVM interface.
- The bridge MUST remain small and versioned.

```text
Kotlin -> TypeScript: render(request)
TypeScript -> Kotlin: ready | rendered | openLink | error
```

## Consequences

- Most feature work and tests stay in TypeScript.
- Kotlin changes mainly for platform compatibility and lifecycle concerns.
- The repository requires both Gradle/Kotlin and Node/TypeScript toolchains.
- Bridge compatibility and end-to-end JCEF behavior require explicit tests.
