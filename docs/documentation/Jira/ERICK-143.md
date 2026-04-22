# ERICK-143 - Canonicalize APP_CONTEXT Source and Sync Rules

| Field | Value |
|---|---|
| **Type** | Tech Debt |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Completed on `ERICK-141` |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | ai-first, docs, maintainability |
| **Dependencies** | None |

---

## Objective

Choose one canonical `APP_CONTEXT.md` and make the second copy explicitly mirrored so agents and contributors do not drift the architecture docs.

Status: Completed on `ERICK-141` by making the repo-root `APP_CONTEXT.md` canonical, marking `docs/documentation/APP_CONTEXT.md` as mirrored, and aligning the main AI-facing references.

---

## Evidence

- The repo has both `APP_CONTEXT.md` and `docs/documentation/APP_CONTEXT.md`.
- `README.md`, `AGENTS.md`, `PROJECT_PROMPT.md`, and ticket docs all reference `APP_CONTEXT.md` in slightly different ways.
- ERICK-141 already records this duplication as an AI-first risk.

---

## Scope

1. Decide whether the repo-root or docs copy is canonical.
2. Add a mirrored-doc note to the secondary copy.
3. Update `README.md`, `AGENTS.md`, and `PROJECT_PROMPT.md` so they point to the canonical source consistently.
4. Add a lightweight sync checklist or script note for future architecture updates.

## Validation

- Confirm the two `APP_CONTEXT.md` files remain byte-identical after the note update.
- Confirm `README.md`, `AGENTS.md`, `PROJECT_PROMPT.md`, and `.github/copilot-instructions.md` all describe the repo-root `APP_CONTEXT.md` as canonical.

---

## Acceptance Criteria

1. One `APP_CONTEXT.md` is documented as canonical.
2. The mirrored copy clearly states where edits must originate.
3. All AI-facing docs point to the same source-of-truth architecture document.
