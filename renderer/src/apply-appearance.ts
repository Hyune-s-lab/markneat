import type { RenderRequest } from "./render-request";

const BASE_FONT_SIZE = 16;
const MIN_FONT_SCALE = 90;
const MAX_FONT_SCALE = 180;
const DEFAULT_CONTENT_WIDTH = 1152;
const MIN_CONTENT_WIDTH = 768;
const MAX_CONTENT_WIDTH = 1536;

export function applyAppearance(
  root: HTMLElement,
  viewer: HTMLElement,
  request: RenderRequest,
): void {
  const profile = request.profile ?? "compact";
  root.dataset.profile = profile;
  root.style.setProperty(
    "--markdown-neat-accent",
    request.theme === "dark" ? "#f0883e" : "#bc4c00",
  );
  viewer.style.lineHeight = profile === "spacious" ? "1.75" : "1.5";
  viewer.style.fontSize = `${(BASE_FONT_SIZE * clampFontScale(request.fontScale)) / 100}px`;
  viewer.style.maxWidth = contentWidth(request.maxContentWidth);

  applyBodyFont(viewer, request.bodyFontFamily ?? "");
  applyCodeFont(viewer, request.codeFontFamily ?? "");
}

function applyBodyFont(viewer: HTMLElement, family: string): void {
  if (family.length === 0) {
    viewer.style.removeProperty("font-family");
    return;
  }
  viewer.style.fontFamily = fontStack(family, "system-ui, sans-serif");
}

function applyCodeFont(viewer: HTMLElement, family: string): void {
  if (family.length === 0) {
    delete viewer.dataset.customCodeFont;
    viewer.style.removeProperty("--markdown-neat-code-font");
    return;
  }
  viewer.dataset.customCodeFont = "true";
  viewer.style.setProperty(
    "--markdown-neat-code-font",
    fontStack(family, "ui-monospace, monospace"),
  );
}

function clampFontScale(scale: number): number {
  if (!Number.isFinite(scale)) {
    return 100;
  }
  return Math.min(MAX_FONT_SCALE, Math.max(MIN_FONT_SCALE, Math.round(scale)));
}

function contentWidth(width: number | null | undefined): string {
  if (width == null) {
    return "none";
  }
  const finiteWidth =
    typeof width === "number" && Number.isFinite(width)
      ? Math.round(width)
      : DEFAULT_CONTENT_WIDTH;
  return `${Math.min(MAX_CONTENT_WIDTH, Math.max(MIN_CONTENT_WIDTH, finiteWidth))}px`;
}

function fontStack(family: string, fallback: string): string {
  const escaped = family.replaceAll("\\", "\\\\").replaceAll('"', '\\"');
  return `"${escaped}", ${fallback}`;
}
