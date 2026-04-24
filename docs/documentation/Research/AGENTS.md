# ERICK Research Agent Guide

Use this file for work under `docs/documentation/Research/`. It supplements the repo-root `AGENTS.md` with Python, reproducibility, and research-asset rules.

## Scope

- Research notes, reports, and references live under `docs/documentation/Research/`.
- Python optimizer and analysis work primarily lives under `docs/documentation/Research/vatsal/`.
- The React visualizer and design assets under the research subtree are research tools, not shipping product code.

## Python And Research Workflow

- Preserve reproducibility. When you change a Python script, update the documented dependencies, inputs, outputs, or run steps if they changed.
- Prefer small, explicit scripts over hidden side effects or implicit working-directory assumptions.
- Do not overwrite result logs, optimizer outputs, or generated artifacts unless the task explicitly requires regeneration.
- Keep generated outputs and hand-written analysis separate.
- If a script change materially alters metrics, corpus assumptions, or layout conclusions, update the relevant README or report in the same pass.
- When possible, avoid mixing product behavior changes into research scripts or research-only experiments into shipping code paths.

## Routing Hints

- Research overview: `docs/documentation/Research/README.md`
- Main optimizer: `docs/documentation/Research/vatsal/erick_v5_vectorized.py`
- Alternate and runner scripts: `docs/documentation/Research/vatsal/scripts/`
- Research reports and logs: `docs/documentation/Research/vatsal/results_and_logs/`
- Research layout visualizer: `docs/documentation/Research/vatsal/layout_design/`

## Documentation Triggers

- Script workflow, dependency, or runtime changes: update `docs/documentation/Research/README.md`.
- New conclusions or changed reported metrics: update the affected report or results markdown.
- If research findings influence shipped layouts or user-facing behavior, also update the product docs that describe that behavior.

## Validation

- Run the narrowest relevant Python command for the touched script when the environment allows it.
- If full optimizer runs are too expensive, use a cheaper syntax or targeted execution check and say that full optimization was not rerun.
- If Python or required packages are unavailable, state the validation gap explicitly.