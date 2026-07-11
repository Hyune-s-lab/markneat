import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

vi.mock("github-markdown-css/github-markdown-dark.css?inline", () => ({
  default: ".markdown-body { background-color: #0d1117; }",
}));
vi.mock("github-markdown-css/github-markdown-light.css?inline", () => ({
  default: ".markdown-body { background-color: #ffffff; }",
}));

describe("viewer theme", () => {
  beforeEach(() => {
    vi.useFakeTimers();
    document.documentElement.removeAttribute("data-theme");
    delete window.markdownNeatRuntimes;
    document.head.innerHTML = "";
    document.body.innerHTML = '<main id="viewer" class="markdown-body"></main>';
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("applies a dark render request to the document", async () => {
    vi.resetModules();
    await import("./main");

    window.markdownNeat.connect({
      error: vi.fn(),
      loadRuntime: vi.fn(),
      openLink: vi.fn(),
      ready: vi.fn(),
      rendered: vi.fn(),
    });
    window.markdownNeat.render({
      version: 2,
      source: "# Dark",
      baseUrl: "file:///README.md",
      documentType: "markdown",
      theme: "dark",
    });

    await vi.advanceTimersByTimeAsync(75);

    expect(document.documentElement.dataset.theme).toBe("dark");
    expect(document.head.querySelector("style[data-markdown-neat-theme]")?.textContent).toContain(
      "background-color: #0d1117",
    );
    expect(document.getElementById("viewer")?.innerHTML).toContain("<h1");
  });

  it("loads Mermaid only when a diagram block is rendered", async () => {
    vi.resetModules();
    await import("./main");
    const loadRuntime = vi.fn();
    const rendered = vi.fn();

    window.markdownNeat.connect({
      error: vi.fn(),
      loadRuntime,
      openLink: vi.fn(),
      ready: vi.fn(),
      rendered,
    });
    window.markdownNeat.render({
      version: 2,
      source: "```mermaid\nflowchart LR\nA --> B\n```",
      baseUrl: "file:///README.md",
      documentType: "markdown",
      theme: "light",
    });

    await vi.advanceTimersByTimeAsync(75);
    expect(loadRuntime).toHaveBeenCalledOnce();
    expect(loadRuntime).toHaveBeenCalledWith("mermaid");

    window.markdownNeatRuntimes = {
      mermaid: {
        initialize: vi.fn(),
        render: vi.fn().mockResolvedValue({ svg: "<svg><text>Diagram</text></svg>" }),
      },
    };
    window.markdownNeat.runtimeReady("mermaid");
    await vi.runAllTimersAsync();

    expect(document.getElementById("viewer")?.textContent).toContain("Diagram");
    expect(rendered).toHaveBeenCalledOnce();
  });
});
