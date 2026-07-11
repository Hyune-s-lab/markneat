# Performance

Measurements MUST be repeated for releases that materially change the renderer or host lifecycle.

## 0.1.0 Baseline

Measured on an Apple Silicon Mac with Java 21 and Node.js 22.21.1:

- Plugin distribution: 53.5 KB
- Self-contained renderer: 109.6 KB raw, 28.9 KB gzip
- Renderer module load: 45.6 ms and 4.1 MiB heap
- 100 KiB Markdown fixture: 185.9 ms median, 297.5 ms p95, and 42.1 MiB retained heap for one rendered result

Run `npm run measure:renderer` to reproduce renderer module load, render latency, and retained heap measurements. Times and memory are a local baseline, not release budgets. They isolate MarkNeat's TypeScript renderer in Node.js with JSDOM and do not claim to measure the IDE-owned JCEF runtime.
