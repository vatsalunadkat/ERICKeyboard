# CLAUDE.md

@AGENTS.md

Project instructions for Claude Code in ERICK. Claude reads `CLAUDE.md`, not `AGENTS.md` directly, so keep the import above in place and update `AGENTS.md` first when shared workflow rules change.

## Claude Code Notes

- Claude Code can use either `./CLAUDE.md` or `./.claude/CLAUDE.md`; this repository uses the root file.
- When working under `android/`, `ios/`, or `docs/documentation/Research/`, rely on the nested subtree `CLAUDE.md` file for scoped workflow rules.
- When working under `docs/`, rely on the nested `docs/CLAUDE.md` file for docs-specific workflow rules.
- Keep this file short and Claude-specific; shared repo guidance belongs in `AGENTS.md`.
- If the workflow explicitly allows git writes, create small validated commits on the current branch rather than waiting for one large final commit.
