export type DocumentType = "markdown" | "mermaid";
export type RenderTheme = "light" | "dark";

export interface RenderRequest {
  version: 2;
  source: string;
  baseUrl: string;
  documentType: DocumentType;
  theme: RenderTheme;
}
