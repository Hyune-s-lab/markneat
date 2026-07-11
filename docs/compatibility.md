# Compatibility

MarkNeat owns the normal editor for `.md` and `.markdown` files while the plugin is enabled. The built-in Markdown editor and non-exclusive third-party providers are hidden so the visible document always uses MarkNeat settings.

## Competing Exclusive Viewers

Another Markdown plugin may also request exclusive editor ownership. IntelliJ keeps every provider that makes that request, so multiple editor tabs can still appear in this exceptional case. MarkNeat does not disable or target arbitrary third-party plugins.

Keep only one exclusive Markdown viewer enabled when this happens. The active editor name appears in the selector below the document.

## Reporting a Conflict

Include the following information in a compatibility report:

- JetBrains IDE name and version
- MarkNeat version
- Enabled Markdown or diagram preview plugins
- A screenshot that includes the editor selector below the document
