import darkTheme from "github-markdown-css/github-markdown-dark.css?inline";
import lightTheme from "github-markdown-css/github-markdown-light.css?inline";

import "./viewer.css";
import { renderMarkdown, type RenderRequest } from "./render-markdown";

interface MarkdownNeatHost {
  error(message: string): void;
  openLink(href: string): void;
  ready(): void;
  rendered(): void;
}

interface MarkdownNeatBridge {
  connect(host: MarkdownNeatHost): void;
  render(request: RenderRequest): void;
}

declare global {
  interface Window {
    markdownNeat: MarkdownNeatBridge;
  }
}

const viewer = requiredElement("viewer");
const themeStyle = document.createElement("style");
themeStyle.dataset.markdownNeatTheme = "true";
document.head.append(themeStyle);

let host: MarkdownNeatHost | undefined;
let pendingRequest: RenderRequest | undefined;
let renderTimer: number | undefined;

window.markdownNeat = {
  connect(nextHost) {
    host = nextHost;
    host.ready();
  },
  render(request) {
    pendingRequest = request;
    if (renderTimer !== undefined) {
      window.clearTimeout(renderTimer);
    }
    renderTimer = window.setTimeout(flushRender, 75);
  },
};

function flushRender(): void {
  const request = pendingRequest;
  pendingRequest = undefined;
  renderTimer = undefined;
  if (request === undefined) {
    return;
  }

  try {
    const scrollTop = document.documentElement.scrollTop;
    themeStyle.textContent = request.theme === "dark" ? darkTheme : lightTheme;
    document.documentElement.dataset.theme = request.theme;
    viewer.classList.remove("markdown-neat-error");
    viewer.innerHTML = renderMarkdown(request).html;
    document.documentElement.scrollTop = scrollTop;
    host?.rendered();
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error);
    viewer.textContent = `Unable to render this document: ${message}`;
    viewer.classList.add("markdown-neat-error");
    host?.error(message);
  }
}

document.addEventListener("click", (event) => {
  const target = event.target;
  const anchor = target instanceof Element ? target.closest("a") : null;
  if (!(anchor instanceof HTMLAnchorElement)) {
    return;
  }

  event.preventDefault();
  const url = new URL(anchor.href);
  if (url.pathname === window.location.pathname && url.hash.length > 1) {
    const id = decodeURIComponent(url.hash.slice(1));
    document.getElementById(id)?.scrollIntoView();
    return;
  }
  host?.openLink(anchor.href);
});

function requiredElement(id: string): HTMLElement {
  const element = document.getElementById(id);
  if (element === null) {
    throw new Error(`Missing #${id}`);
  }
  return element;
}
