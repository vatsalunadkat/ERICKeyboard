# Website SEO and Analytics Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add Google Analytics, improve GitHub Pages SEO surfaces, add FAQ and 404 pages, localize badge assets, and keep app privacy messaging accurate.

**Architecture:** Update the static `docs/` website directly because those HTML files are the deployed source of truth. Reuse shared CSS and image assets across the public pages, then sync deploy workflow, sitemap, and legacy redirect pages so crawl signals and analytics remain consistent.

**Tech Stack:** Static HTML, CSS, JavaScript, GitHub Pages, GitHub Actions

---

### Task 1: Add shared website assets

**Files:**
- Create: `docs/images/google-play-badge.svg`
- Create: `docs/images/app-store-badge.svg`
- Modify: `docs/css/style.css`

- [ ] Add local badge SVG assets for Google Play and iOS release status.
- [ ] Update shared CSS so localized badges render cleanly in hero and footer contexts without remote crop hacks.

### Task 2: Update live public pages

**Files:**
- Modify: `docs/index.html`
- Modify: `docs/accessibility.html`
- Modify: `docs/who-benefits.html`
- Modify: `docs/privacy-policy.html`
- Modify: `docs/releases.html`

- [ ] Add the Google tag immediately after each `<head>` element.
- [ ] Add `apple-touch-icon`, richer site/entity metadata, local badge assets, FAQ navigation, and image dimension hints.
- [ ] Rewrite the privacy page so it is explicitly about app privacy and app tracking behavior.

### Task 3: Add new crawlable support pages

**Files:**
- Create: `docs/faq.html`
- Create: `docs/404.html`

- [ ] Add a search-intent FAQ page with visible Q&A content and matching FAQ structured data.
- [ ] Add a real 404 page with helpful navigation back into the site.

### Task 4: Sync deploy and crawl metadata

**Files:**
- Modify: `.github/workflows/deploy-website.yml`
- Modify: `docs/sitemap.xml`
- Modify: `docs/v1/index.html`
- Modify: `docs/v1/accessibility.html`
- Modify: `docs/v1/who-benefits.html`
- Modify: `docs/v1/privacy-policy.html`
- Modify: `docs/v1/releases.html`

- [ ] Ensure the deploy workflow includes the new FAQ page.
- [ ] Update the sitemap with the FAQ URL and fresh `lastmod` dates.
- [ ] Add the Google tag to the legacy redirect pages so traffic measurement stays complete during redirects.

### Task 5: Verify the static site pass

**Files:**
- Modify if needed after verification: `docs/SEO_AUDIT.md`

- [ ] Re-run grep-based static checks for Google tag coverage, missing 404/FAQ files, remote badge references, schema signals, and sitemap/workflow coverage.
- [ ] Open the updated site in the browser for a quick visual sanity check on key pages.
