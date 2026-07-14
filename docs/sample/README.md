# Rendering Samples

Manual QA fixtures for the viewer. Open this project in a `runIde` sandbox and open each file to verify rendering.

These files intentionally break the English-only convention: their multilingual names and bodies are the test input.

| File | Verifies |
|---|---|
| `sample-order-service (english).md` | ASCII-path baseline |
| `샘플-주문 서비스 아키텍처 (한국어).md` | Korean path with spaces and parentheses |
| `サンプル-注文サービス設計 (日本語).md` | Japanese path |
| `示例-订单服务架构 (中文).md` | Chinese path |
| `📝 혼합-ミックス-混合 100%.md` | Emoji, `%`, and mixed-script path |

Every file exercises heading levels, tables, task lists, blockquotes, Kotlin/TypeScript/Python/YAML code fences, Mermaid flowchart with remote https icon nodes, sequence and ER diagrams, and a relative image.
