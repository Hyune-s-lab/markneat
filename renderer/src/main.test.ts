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
      openLink: vi.fn(),
      ready: vi.fn(),
      rendered: vi.fn(),
    });
    window.markdownNeat.render({
      version: 1,
      source: "# Dark",
      baseUrl: "file:///README.md",
      theme: "dark",
    });

    vi.advanceTimersByTime(75);

    expect(document.documentElement.dataset.theme).toBe("dark");
    expect(document.head.querySelector("style[data-markdown-neat-theme]")?.textContent).toContain(
      "background-color: #0d1117",
    );
    expect(document.getElementById("viewer")?.innerHTML).toContain("<h1");
  });
});
