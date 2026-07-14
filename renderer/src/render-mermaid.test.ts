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

  it("retries a diagram once when a remote image fails to decode", async () => {
    const root = document.createElement("main");
    root.innerHTML = '<pre><code class="language-mermaid">flowchart LR\nA --&gt; B</code></pre>';
    const render = vi
      .fn<MermaidApi["render"]>()
      .mockRejectedValueOnce(new Error("The source image cannot be decoded."))
      .mockResolvedValueOnce({ svg: "<svg><text>Recovered</text></svg>" });
    const reportError = vi.fn();

    await renderMermaidDiagrams(root, "light", async () => ({ initialize: vi.fn(), render }), reportError);

    expect(render).toHaveBeenCalledTimes(2);
    expect(root.textContent).toContain("Recovered");
    expect(reportError).not.toHaveBeenCalled();
  });

  it("does not retry parse errors", async () => {
    const root = document.createElement("main");
    root.innerHTML = '<pre><code class="language-mermaid">invalid diagram</code></pre>';
    const render = vi.fn<MermaidApi["render"]>().mockRejectedValue(new Error("Parse error on line 1"));
    const reportError = vi.fn();

    await renderMermaidDiagrams(root, "light", async () => ({ initialize: vi.fn(), render }), reportError);

    expect(render).toHaveBeenCalledTimes(1);
    expect(reportError).toHaveBeenCalledOnce();
  });
});
