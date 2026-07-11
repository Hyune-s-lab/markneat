import { describe, expect, it, vi } from "vitest";

import { renderMermaidDiagrams, type MermaidApi } from "./render-mermaid";

describe("renderMermaidDiagrams", () => {
  it("does not load Mermaid when the document has no Mermaid blocks", async () => {
    const root = document.createElement("main");
    root.innerHTML = "<p>Plain Markdown</p>";
    const load = vi.fn<() => Promise<MermaidApi>>();

    await renderMermaidDiagrams(root, "light", load, vi.fn());

    expect(load).not.toHaveBeenCalled();
  });

  it("renders valid blocks and isolates an invalid block", async () => {
    const root = document.createElement("main");
    root.innerHTML = [
      "<p>Before</p>",
      '<pre><code class="language-mermaid">flowchart LR\nA --&gt; B</code></pre>',
      '<pre><code class="language-mermaid">invalid diagram</code></pre>',
      "<p>After</p>",
    ].join("");
    const initialize = vi.fn();
    const render = vi
      .fn<MermaidApi["render"]>()
      .mockResolvedValueOnce({ svg: '<svg onload="alert(1)"><text>Rendered</text></svg>' })
      .mockRejectedValueOnce(new Error("Parse error on line 1\nDetails"));
    const reportError = vi.fn();

    await renderMermaidDiagrams(root, "dark", async () => ({ initialize, render }), reportError);

    expect(initialize).toHaveBeenCalledWith(
      expect.objectContaining({
        htmlLabels: false,
        securityLevel: "strict",
        startOnLoad: false,
        suppressErrorRendering: true,
        theme: "dark",
      }),
    );
    expect(root.textContent).toContain("Before");
    expect(root.textContent).toContain("Rendered");
    expect(root.innerHTML).not.toContain("onload");
    expect(root.textContent).toContain("Mermaid diagram could not be rendered");
    expect(root.textContent).toContain("Parse error on line 1");
    expect(root.textContent).toContain("After");
    expect(reportError).toHaveBeenCalledOnce();
  });
});
