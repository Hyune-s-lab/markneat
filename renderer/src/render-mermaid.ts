import DOMPurify from "dompurify";

import type { RenderTheme } from "./render-request";

interface MermaidConfig {
  htmlLabels: false;
  securityLevel: "strict";
  startOnLoad: false;
  suppressErrorRendering: true;
  theme: "default" | "dark";
}

interface MermaidRenderResult {
  svg: string;
}

export interface MermaidApi {
  initialize(config: MermaidConfig): void;
  render(id: string, source: string, container?: Element): Promise<MermaidRenderResult>;
}

export type MermaidLoader = () => Promise<MermaidApi>;
export type MermaidErrorReporter = (message: string) => void;

let diagramSequence = 0;

export async function renderMermaidDiagrams(
  root: HTMLElement,
  theme: RenderTheme,
  loadMermaid: MermaidLoader,
  reportError: MermaidErrorReporter,
): Promise<void> {
  const diagrams = Array.from(root.querySelectorAll<HTMLElement>("pre > code.language-mermaid")).map(
    (code) => {
      const container = document.createElement("div");
      container.className = "markdown-neat-diagram";
      container.style.fontSize = "16px";
      container.style.lineHeight = "1.5";
      container.setAttribute("role", "img");
      container.setAttribute("aria-label", "Mermaid diagram");
      const source = code.textContent ?? "";
      code.parentElement?.replaceWith(container);
      return { container, source };
    },
  );

  if (diagrams.length === 0) {
    return;
  }

  let mermaid: MermaidApi;
  try {
    mermaid = await loadMermaid();
    mermaid.initialize({
      htmlLabels: false,
      securityLevel: "strict",
      startOnLoad: false,
      suppressErrorRendering: true,
      theme: theme === "dark" ? "dark" : "default",
    });
  } catch (error) {
    const message = errorMessage(error);
    for (const diagram of diagrams) {
      showDiagramError(diagram.container, diagram.source, message);
    }
    reportError(`Unable to load Mermaid: ${message}`);
    return;
  }

  for (const diagram of diagrams) {
    try {
      const result = await renderDiagram(mermaid, diagram.source, diagram.container);
      diagram.container.innerHTML = String(
        DOMPurify.sanitize(result.svg, {
          FORBID_TAGS: ["foreignObject", "script"],
          USE_PROFILES: { svg: true, svgFilters: true },
        }),
      );
    } catch (error) {
      const message = errorMessage(error);
      showDiagramError(diagram.container, diagram.source, message);
      reportError(`Mermaid diagram failed: ${message}`);
    }
  }
}

async function renderDiagram(
  mermaid: MermaidApi,
  source: string,
  container: Element,
): Promise<MermaidRenderResult> {
  try {
    return await mermaid.render(`markdown-neat-mermaid-${diagramSequence++}`, source, container);
  } catch (error) {
    if (!isImageDecodeError(error)) {
      throw error;
    }
    // Remote diagram images can fail to decode on a cold load; a retry hits the browser cache.
    return await mermaid.render(`markdown-neat-mermaid-${diagramSequence++}`, source, container);
  }
}

function isImageDecodeError(error: unknown): boolean {
  return error instanceof Error && error.message.includes("image cannot be decoded");
}

function showDiagramError(container: HTMLElement, source: string, message: string): void {
  container.classList.add("markdown-neat-diagram-error");
  container.removeAttribute("role");
  container.removeAttribute("aria-label");

  const title = document.createElement("strong");
  title.textContent = "Mermaid diagram could not be rendered";
  const detail = document.createElement("span");
  detail.textContent = message;
  const code = document.createElement("code");
  code.textContent = source;
  const pre = document.createElement("pre");
  pre.append(code);
  container.replaceChildren(title, detail, pre);
}

function errorMessage(error: unknown): string {
  const message = error instanceof Error ? error.message : String(error);
  return message.split("\n", 1)[0] || "Unknown Mermaid error";
}
