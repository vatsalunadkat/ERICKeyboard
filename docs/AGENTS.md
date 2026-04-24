# ERICK Docs Agent Guide

Use this file when editing anything under `docs/`. It supplements the repo-root `AGENTS.md` with documentation, website, and diagram-specific rules.

## Scope

- Live website pages and assets live directly under `docs/`.
- `docs/documentation/User_Guide.md` is the main end-user guide.
- `APP_CONTEXT.md` is the canonical architecture document.
- `docs/documentation/APP_CONTEXT.md` is a mirrored copy for docs navigation.
- Ticket docs live under `docs/documentation/Jira/`.
- Diagram sources live in `ERICK_architecture.drawio` and `docs/documentation/joystick_wireframe.drawio`.
- `docs/v1/` is legacy website content. Do not edit it unless the task explicitly mentions `v1`.
- `docs/documentation/Research/` is research material. Do not update it unless the task is research-specific.

## Website Layout And Responsiveness

- Treat pages under `docs/` as public website pages, not throwaway documentation scraps.
- Keep pages responsive on narrow mobile widths and wider desktop widths.
- Avoid fixed-width layouts, overflow-prone text blocks, or interactions that depend on hover only.
- When changing layout or CSS, check the affected page at a narrow mobile width and at least one wider desktop width.
- Preserve readable spacing, heading hierarchy, and tappable targets on mobile.
- Update HTML and shared CSS together when one change would otherwise make the other misleading.

## Documentation Workflow

- Read the source-of-truth code or canonical doc before editing prose.
- If code and docs disagree, confirm shipped behavior in code and tests, then update docs to match the shipped behavior.
- Update `APP_CONTEXT.md` first, then sync `docs/documentation/APP_CONTEXT.md` intentionally.
- Update diagrams when prose would otherwise describe a structure, control layout, or interaction flow that no longer matches the source diagram.
- If a `.drawio` source changes and a checked-in export exists beside it, refresh the export when practical or explicitly note the export gap.

## Update Matrix

- Architecture, ownership, or module-flow changes: update `APP_CONTEXT.md`, `docs/documentation/APP_CONTEXT.md`, and `ERICK_architecture.drawio`.
- Dial geometry, gesture mappings, preview ordering, or labeled wheel behavior: update `docs/documentation/User_Guide.md`, the relevant Jira ticket, and `docs/documentation/joystick_wireframe.drawio`.
- Public feature descriptions, install instructions, or store-availability messaging: update `README.md` and the affected pages in `docs/`.
- Release-note-worthy user-visible changes: update `CHANGELOG.md`.
- Ticket-scoped work: update the matching `docs/documentation/Jira/ERICK-###.md` file when the scope, status, or follow-up guidance materially changes.

## Writing Rules

- Prefer short factual updates over marketing language.
- Do not invent unsupported features, release dates, or store availability.
- Keep Android and iOS availability language aligned across `README.md`, website pages, and ticket notes.
- Use consistent terminology: 6-section, 8-section, utility wheel, quickstart, practice hub, controller diagnostics.

## Validation

- Reread every changed markdown or HTML file for consistency with code and neighboring docs.
- Keep mirrored docs synchronized when the canonical root doc changes.
- Check changed paths, labels, and link targets for obvious drift.
- If multiple docs mention the same mapping, workflow, or store status, update all of them in the same pass.
- For website work, also verify the page still behaves reasonably on mobile and desktop viewport widths.
