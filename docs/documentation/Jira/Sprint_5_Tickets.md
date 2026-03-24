# ERICK -- Sprint 5 Tickets

**Sprint**: SCRUM Sprint 5  
**Sprint 5 Start Date**: March 23, 2026 (Monday)  
**Sprint 5 End Date**: March 27, 2026 (Friday)  
**Project**: ERICK  

**Ticket Numbering**: Continues from ERICK-130 (last Sprint 4 ticket).

---

## ERICK-131 -- ERICKeyboard Website Redesign

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 13 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Sprint 5 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | website, react, redesign, CI/CD |
| **Dependencies** | None |

### Description

Complete redesign of the ERICKeyboard project website from a static HTML/CSS/JS site to a modern React single-page application. The new site is built with Vite 8 + React 19 + Tailwind CSS v4 + Framer Motion 12 + React Router DOM 7 (HashRouter) and uses a Headspace-inspired pastel color palette with no dark mode.

**Pages:**
- **Landing** -- Hero with gradient background, "How It Works" section with placeholder GIF (replaces old interactive JoystickDemo), demo GIF grid from actual repo assets, 6+ feature cards, all 8 accessibility personas from the original site, actual color palette swatches for the 8 directional colors with hex values, controller support section, and a CTA.
- **Evolution** -- Stats section at the top (years in development, platforms, layouts, characters), vertical timeline with demo media for each version milestone (project start year is 2017), and a roadmap section.
- **Features** -- Chord input system explanation with placeholder GIF (no table), prediction engine, accessibility deep-dive (7 items: chorded efficiency, word prediction, high-contrast colors, large touch targets, haptic feedback, switch access, customizable layouts), custom layouts (Logical, Efficiency, user-defined), and a full feature grid.
- **Privacy** -- Fully static, no Framer Motion imports or animations. Plain divs with `bg-white/40 rounded-2xl` card styling. Covers data collection, permissions, third-party services, data security, children's privacy, and contact information.

**Shared Components:**
- **Navbar** -- Actual logo image via `import.meta.env.BASE_URL`, "ERICKeyboard" branding, responsive mobile hamburger menu, sticky with backdrop blur, GitHub external link with arrow indicator on desktop and mobile.
- **Footer** -- 4-column layout (Brand, Navigate, Resources, Developer). Navigate includes "Old Website (v1)" link to `/ERICKeyboard/docs/v1/index.html`. External links show arrow indicator. Actual logo image. Copyright notice.
- **FeatureCard** -- Pastel color map with no dark mode variants, hover elevation.
- **TimelineCard** -- Supports `media` prop for images/GIFs, alternating left/right layout.
- **SectionWrapper** -- Framer Motion viewport-triggered fade-in with configurable direction and delay.
- **ScrollToTop** -- Scrolls to top on React Router route change.

**Build & Deployment:**
- Source lives in `docs/website/`, builds to `docs/` parent directory with base path `/ERICKeyboard/`.
- GitHub Actions workflow (`.github/workflows/deploy-website.yml`) using `actions/deploy-pages@v4` (artifact-based, not branch-based). Triggers on push to main (`docs/website/**` paths), daily cron at 6 AM UTC, and manual `workflow_dispatch`. Copies static assets (v1, images, documentation) into the deploy artifact.
- `404.html` and `.nojekyll` in public directory for GitHub Pages compatibility.

**Preserved Original Website:**
- Original HTML/CSS/JS site from `website/` on `main` branch restored to `docs/v1/` (index.html, accessibility.html, privacy-policy.html, css/style.css, js/main.js, images/erick-logo.png, images/erick-logo-small.png). Fully functional at `/ERICKeyboard/docs/v1/index.html`.

**Other Changes:**
- All "ERICK" references renamed to "ERICKeyboard" throughout the site.
- No em dashes used anywhere (replaced with regular dashes).
- Source-available license update (`LICENSE` file and `docs/index.html` updated in commit `52b5845`).

### Acceptance Criteria

- [ ] React SPA builds successfully with `npm run build` from `docs/website/`
- [ ] All four pages render correctly: Landing, Evolution, Features, Privacy
- [ ] HashRouter navigation works without 404s on page refresh
- [ ] Tailwind CSS v4 pastel theme tokens resolve correctly, no dark mode anywhere
- [ ] Landing page shows all 8 accessibility personas, 8 color swatches with hex values, demo GIF grid, and placeholder GIF for "How It Works"
- [ ] Evolution page has stats at the top, timeline with media, project start year 2017
- [ ] Features page has chord system with placeholder GIF (no table), 7 accessibility deep-dive items
- [ ] Privacy page has zero Framer Motion imports, fully static
- [ ] Navbar shows actual logo, "ERICKeyboard" branding, GitHub link with arrow indicator on desktop and mobile
- [ ] Footer has "Old Website (v1)" link pointing to `/ERICKeyboard/docs/v1/index.html`
- [ ] Original website at `docs/v1/` is visually identical to `website/` on main, all internal links/styles/images work
- [ ] GitHub Actions workflow triggers on push, daily cron, and manual dispatch
- [ ] `actions/deploy-pages@v4` deploys successfully with v1, images, and documentation assets included
- [ ] All text uses "ERICKeyboard" (not "ERICK"), no em dashes
- [ ] `LICENSE` file contains source-available license text

---

## ERICK-132 -- Architecture Diagrams

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Low |
| **Story Points** | 2 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Sprint 5 |
| **Parent Epic** | -- |
| **Labels** | documentation, architecture |
| **Dependencies** | None |

### Description

Add visual architecture diagrams to the repository documenting the ERICKeyboard system design. These supplement the existing text-based documentation in `APP_CONTEXT.md` and `PROJECT_PROMPT.md`.

**Files added:**
- `Arch_Diagram.drawio` -- High-level architecture overview
- `ERICK_architecture.drawio` -- Detailed architecture with component relationships (Android app, iOS app, Shared KMP module, website deployment)
- `erick_arch_diagram.drawio` -- Alternative architecture view
- `erick_arch_diagram.jsx` -- React component for rendering the architecture diagram interactively

Diagrams are placed at the repository root for easy access and are viewable in VS Code with the Draw.io extension or on draw.io web.

### Acceptance Criteria

- [ ] At least one `.drawio` architecture diagram is present at the repo root
- [ ] Diagram accurately represents the current project structure (Android, iOS, Shared KMP, Website)
- [ ] Diagrams open correctly in the Draw.io VS Code extension or draw.io web

---

## Sprint 5 Summary

| Ticket | Title | Type | Points | Priority |
|---|---|---|---|---|
| ERICK-131 | ERICKeyboard Website Redesign | Story | 13 | High |
| ERICK-132 | Architecture Diagrams | Task | 2 | Low |

**Total Story Points**: 15  
**Ticket Count**: 2
