# Context7 Auto-Docs (Cursor Rule)

Always use Context7 when I need code generation, setup or configuration steps, or library/API documentation.
Automatically call the Context7 MCP tools without me asking:

1) First call `resolve-library-id` with the library name mentioned in my task.
2) Then call `get-library-docs` with the resolved ID.
3) Use the returned docs to ground examples and ensure they match the latest API.
4) If resolution fails, say so and proceed cautiously (but avoid guessing outdated APIs).

Rules of engagement:
- Never dump large docs verbatim; include only relevant snippets.
- Prefer version-specific examples when available.
- For multiple libraries, repeat the resolve → docs flow for each.
