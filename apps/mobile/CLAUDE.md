# Project Rules — Context7 Auto-Docs

When a request involves code generation, setup/config steps, or library/API usage:
- Automatically use the Context7 MCP server:
  1) call `resolve-library-id` with each library mentioned
  2) call `get-library-docs` with the resolved ID(s)
- Ground examples in those docs; no guessing or outdated APIs.
- Only include relevant snippets (no large doc dumps).
- If Context7 isn’t available, say so and proceed conservatively.
