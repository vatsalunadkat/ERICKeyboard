# Using ERICK with Cursor

This repository commits `.cursor/rules/erick-ai-first.mdc` with `alwaysApply: true`, so Cursor Agent should load the ERICK-specific behavioral and routing guidance automatically.

Cursor officially supports both project rules in `.cursor/rules/` and plain `AGENTS.md` files. In ERICK, `AGENTS.md` is the canonical shared instructions file, while `.cursor/rules/erick-ai-first.mdc` is the always-on Cursor overlay.

When working in the documentation subtree, `docs/AGENTS.md` adds docs-specific rules. Treat `docs/v1/` as legacy website content unless the task explicitly mentions it.

When the shared AI workflow rules change, keep these files aligned:

- `AGENTS.md`
- `CLAUDE.md`
- `docs/AGENTS.md`
- `docs/CLAUDE.md`
- `.cursor/rules/erick-ai-first.mdc`
- `.github/copilot-instructions.md`
- `docs/documentation/Jira/ERICK-141.md`
