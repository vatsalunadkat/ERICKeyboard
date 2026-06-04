# SEO Audit: ERICK GitHub Pages

## Follow-up update on 2026-06-04 (sitemap "Couldn't fetch" cache-buster)

Symptom: after the clean sitemap deployed, Google Search Console still reported "Couldn't fetch" / "Sitemap could not be read" for `sitemap.xml`.

Diagnosis (verified live):
- The deployed `sitemap.xml` is genuinely fetchable: HTTP 200, `content-type: application/xml`, no BOM, valid XML, clean headers.
- The widely-cited Next.js issue (vercel/next.js#75836) is almost entirely a Next.js + Vercel problem (middleware intercepting the request, dynamic routes adding `vary: rsc, next-router-*` headers). None of that applies to a static GitHub Pages file.
- The trailing-slash "fix" from that thread does not work on GitHub Pages: `https://.../sitemap.xml/` returns HTTP 404 on static hosting (it is a real path, not a route handler), so submitting it makes GSC cache a 404.
- The universal cause that does apply: GSC caches a failed-fetch state per exact URL and is not real-time; a cached failure for `sitemap.xml` can persist for days.

Fix applied (the one proven cross-host workaround from the thread): publish a second, never-before-cached sitemap URL so GSC fetches it with no cached failure state.
- Added `docs/sitemap-v2.xml` (identical clean URL set to `sitemap.xml`).
- Listed both sitemaps in `docs/robots.txt` (multiple `Sitemap:` lines are valid).
- Added `sitemap-v2.xml` to the Pages deploy workflow.
- `sitemap.xml` is kept as the canonical/conventional sitemap for all other crawlers.

Manual GSC steps after deploy:
1. Deploy (push to `main`, or run the deploy workflow).
2. In Search Console, remove the old `sitemap.xml` entry that shows "Couldn't fetch".
3. Submit `sitemap.xml` again WITHOUT a trailing slash, and also submit `sitemap-v2.xml`.
4. Use URL Inspection -> Test Live URL on the homepage and request indexing; the sitemap status panel is not real-time, so do not rely on it for confirmation.
5. Allow 1-14 days; "Couldn't fetch" commonly clears itself once Google re-crawls.

## Follow-up update on 2026-06-04 (analytics removal + sitemap simplification)

- Removed Google Analytics / `gtag.js` (`G-HVEL79YHNZ`) from every page: the seven live root pages, `404.html`, and the five legacy `/v1/` redirect pages. The site now makes zero third-party requests, which improves load performance (Core Web Vitals) and privacy.
- Simplified `sitemap.xml` to a clean, standards-only URL set. Removed the `video:` sitemap extension because animated-GIF `video:thumbnail_loc` values and large inline video entries are a common cause of Google Search Console "Couldn't fetch" / sitemap processing failures. Video discoverability is preserved by the on-page `VideoObject` JSON-LD already present on each `videos/*.html` watch page (Google's recommended method).
- Bumped all `sitemap.xml` `lastmod` values to `2026-06-04` to signal freshness for re-crawl.
- Updated privacy and FAQ copy that referenced "website analytics," since the website no longer runs analytics or tracking scripts. Only standard GitHub Pages hosting logs (outside our control) remain.
- Manual step still required: in Google Search Console, confirm the property is the URL-prefix `https://vatsalunadkat.github.io/ERICKeyboard/` (not the bare `github.io` root), then re-submit `sitemap.xml` and use "Validate fix" / re-fetch so the stale "Couldn't fetch" status refreshes.

## Follow-up update on 2026-06-01

- Added the Google tag (`G-HVEL79YHNZ`) to all live public pages plus the legacy `/v1/` redirect pages for measurement continuity.
- Added a real `docs/404.html` page for GitHub Pages and kept it out of the sitemap.
- Added a dedicated `docs/faq.html` page with visible FAQ content, FAQ structured data, and breadcrumb structured data.
- Added `apple-touch-icon` links and richer shared entity metadata (`Person`, `Organization`, `WebSite`, `WebPage`, `SoftwareApplication`) across the live public pages.
- Replaced remote Google Play and App Store badge image requests with local SVG assets under `docs/images/`.
- Updated the app privacy policy page so it clearly states that the ERICK app itself does not track users, while the public website may use separate website analytics.
- Updated the sitemap and Pages deploy workflow so the new FAQ page deploys and advertises correctly.

## Current site setup before this change

- GitHub Pages is powered by a GitHub Actions workflow, not by the Pages branch/folder setting alone.
- Workflow: `.github/workflows/deploy-website.yml`
- Trigger: push to `main` when files under `docs/**` change, plus manual `workflow_dispatch`
- Deployment model: artifact-based Pages deploy via `actions/configure-pages@v5`, `actions/upload-pages-artifact@v3`, and `actions/deploy-pages@v4`
- Effective published site URL: `https://vatsalunadkat.github.io/ERICKeyboard/`
- Effective content location before this change:
  - Root URL existed, but `docs/index.html` was only a thin meta-refresh page to `v1/index.html`
  - `docs/accessibility.html` and `docs/privacy-policy.html` were also thin refresh pages
  - Real website content lived under `docs/v1/`
- `docs/website/` is not deployed by the current workflow

## Files that were actually deployed before this change

The workflow copied these inputs into the Pages artifact before this pass:

- `docs/index.html`
- `docs/.nojekyll`
- `docs/v1/**`
- `docs/images/**`
- `docs/documentation/**`
- `docs/css/**`
- `docs/js/**`
- `docs/accessibility.html`
- `docs/privacy-policy.html`
- `docs/404.html` only if present

## Current HTML structure before this change

- Root homepage: redirect shell only
- Root accessibility page: redirect shell only
- Root privacy page: redirect shell only
- Main crawlable content: `docs/v1/index.html`, `docs/v1/accessibility.html`, `docs/v1/who-benefits.html`, `docs/v1/privacy-policy.html`, `docs/v1/releases.html`
- Main site structure on the content pages:
  - `nav.site-nav`
  - hero section
  - content sections using `section`, `div`, cards, grids, and footer
  - homepage and releases pages did not use a `main` wrapper

## Metadata state before this change

### Existing meta tags

- Root pages had only basic `title`, `description`, charset, viewport, and refresh tags
- `docs/v1/index.html` had:
  - `title`
  - `meta name="description"`
  - `og:title`
  - `og:description`
  - `og:type`
  - `og:image`
- `docs/v1/releases.html` had the same limited Open Graph set
- `docs/v1/accessibility.html`, `docs/v1/who-benefits.html`, and `docs/v1/privacy-policy.html` had no Open Graph tags

### Open Graph before this change

- Present only on:
  - `docs/v1/index.html`
  - `docs/v1/releases.html`
- Missing from the root share URLs that most users would copy
- No `og:url`
- No `og:site_name`
- Image values were relative paths

### Twitter card tags before this change

- None on any public page

### Canonical URLs before this change

- None

### Robots before this change

- No `robots.txt`
- No page-level robots directives

### Sitemap before this change

- No `sitemap.xml`
- Live check returned `404` for:
  - `https://vatsalunadkat.github.io/ERICKeyboard/robots.txt`
  - `https://vatsalunadkat.github.io/ERICKeyboard/sitemap.xml`

### Structured data before this change

- None

## Main indexing, crawlability, and social issues before this change

- The root homepage URL was a thin client-side refresh page instead of the actual content page
- Social crawlers hitting the root homepage URL had weak metadata and no stable preview image configuration
- No canonical URLs existed, so search engines had no explicit preferred version
- The real content lived under `/v1/`, while the root URL behaved like a shell
- No XML sitemap existed for search engines or AI crawlers
- No `robots.txt` existed to advertise the sitemap or give clear crawl guidance
- No Twitter card tags existed
- No structured data existed for `SoftwareApplication`, `WebSite`, or project identity
- Search Console verification support was missing
- Accessibility and audience pages had generic H1s and limited search-facing metadata

## What changed in this pass

### Root Pages URLs are now the canonical crawlable pages

- Replaced the thin root redirect files with real content pages:
  - `docs/index.html`
  - `docs/accessibility.html`
  - `docs/privacy-policy.html`
- Added root content pages for:
  - `docs/who-benefits.html`
  - `docs/releases.html`
- Kept the visual design, layout classes, and overall structure aligned with the previous site

### Legacy `/v1` URLs are now treated as legacy URLs

- Replaced the `/v1` HTML pages with lightweight redirect stubs that:
  - refresh to the root canonical URLs
  - include `noindex, follow`
  - point canonical tags at the new root URLs

### Technical SEO added to each public root page

- Unique descriptive `title`
- Unique `meta description`
- Absolute `canonical` URL
- `robots` meta allowing indexing
- Clear, descriptive `h1`
- More search-friendly and AI-friendly copy
- Better internal anchor text such as `GitHub Repository` and `Privacy Policy`

### Open Graph and Twitter card improvements

Added to each root content page:

- `og:title`
- `og:description`
- `og:type`
- `og:url`
- `og:image`
- `og:image:alt`
- `twitter:card`
- `twitter:title`
- `twitter:description`
- `twitter:image`
- `twitter:image:alt`

### Preview image configuration

- Reused an existing project asset as the homepage and page preview image:
  - `https://vatsalunadkat.github.io/ERICKeyboard/documentation/logo/ERICK_feature_graphic_black.png`
- No new marketing image was invented; the configured image stays truthful to the existing product

### Structured data added

- Added `SoftwareApplication` JSON-LD
- Added `WebSite` JSON-LD on the homepage
- Added `Organization` JSON-LD on the homepage to represent the project identity

Structured data now includes:

- `name`
- `applicationCategory`
- `operatingSystem`
- `description`
- `creator`
- `url`
- `softwareVersion`
- `license`

### Robots and sitemap

- Added `docs/robots.txt`
- Added `docs/sitemap.xml`
- Sitemap now matches the intended deployed canonical URLs exactly:
  - `https://vatsalunadkat.github.io/ERICKeyboard/`
  - `https://vatsalunadkat.github.io/ERICKeyboard/accessibility.html`
  - `https://vatsalunadkat.github.io/ERICKeyboard/who-benefits.html`
  - `https://vatsalunadkat.github.io/ERICKeyboard/privacy-policy.html`
  - `https://vatsalunadkat.github.io/ERICKeyboard/releases.html`

### Search Console verification support

- Added the recommended HTML meta tag method to the homepage head:
  - `meta name="google-site-verification" content="REPLACE_WITH_SEARCH_CONSOLE_TOKEN"`
- This is a placeholder until the real Search Console token is issued

### GitHub discoverability improvements

- Updated `README.md` copy to better reflect:
  - accessible keyboard
  - assistive technology
  - virtual keyboard
  - controller typing and gamepad typing
  - privacy-focused positioning
- Added the public website URL to the README
- Reviewed current remote repository description and topics:
  - Existing remote description is directionally relevant
  - Existing topics already include `accessibility`, `virtual-keyboard`, `assistive-technology`, and related tags
  - No remote repository setting changes were made in this pass because those settings are not versioned in the repo

### Pages workflow updates

Updated `.github/workflows/deploy-website.yml` so the Pages artifact now also includes:

- `docs/who-benefits.html`
- `docs/releases.html`
- `docs/robots.txt`
- `docs/sitemap.xml`

## Exact search terms targeted

- accessible keyboard
- accessibility keyboard
- virtual keyboard
- assistive technology keyboard
- controller typing
- gamepad typing
- motor accessibility keyboard
- privacy-focused keyboard
- offline keyboard
- Android and iOS accessibility keyboard
- one-handed typing keyboard
- controller keyboard

## Exact crawl and indexing improvements made

- Promoted the root GitHub Pages URLs from redirect shells to real content pages
- Reduced duplicate content risk by turning `/v1/` pages into legacy redirect pages with canonical signals
- Added canonical URLs to every root page
- Added indexable metadata to every root page
- Added structured data that clearly explains what ERICK is
- Added a sitemap for search engines and AI crawlers
- Added a `robots.txt` file that explicitly points crawlers to the sitemap
- Added richer social metadata so shared links resolve to a stable preview
- Improved the homepage, accessibility page, and audience page wording so AI systems can extract:
  - what ERICK is
  - who it helps
  - main features
  - supported platforms
  - privacy posture
  - install and usage basics

## What was missing before

- Canonicals
- Twitter cards
- Sitewide Open Graph coverage
- `robots.txt`
- `sitemap.xml`
- JSON-LD structured data
- Search Console verification support
- Crawl-friendly root content pages
- Root-level audience and releases pages

## Manual steps still required

### Google Search Console

Use a URL-prefix property for:

- `https://vatsalunadkat.github.io/ERICKeyboard/`

Then:

1. Create the URL-prefix property in Google Search Console.
2. Copy the issued verification token.
3. Replace `REPLACE_WITH_SEARCH_CONSOLE_TOKEN` in `docs/index.html`.
4. Deploy the site.
5. Click Verify in Search Console.
6. Submit `https://vatsalunadkat.github.io/ERICKeyboard/sitemap.xml`.
7. Use URL Inspection on the five canonical URLs and request indexing if needed.

### Bing Webmaster Tools

1. Add the same site using the URL-prefix `https://vatsalunadkat.github.io/ERICKeyboard/`.
2. Verify ownership with Bing's preferred supported method for the same Pages setup.
3. Submit `https://vatsalunadkat.github.io/ERICKeyboard/sitemap.xml`.

### Optional remote GitHub metadata cleanup

These are not repo-versioned changes, so they were reviewed but not changed here:

- Repository description can stay as-is or be expanded slightly to mention controller typing and privacy
- Repository topics are already relevant, but optional additions could include:
  - `controller-typing`
  - `gamepad-typing`
  - `motor-accessibility`
  - `privacy-focused`

## Files changed in this pass

- `.github/workflows/deploy-website.yml`
- `README.md`
- `docs/index.html`
- `docs/accessibility.html`
- `docs/who-benefits.html`
- `docs/privacy-policy.html`
- `docs/releases.html`
- `docs/robots.txt`
- `docs/sitemap.xml`
- `docs/v1/index.html`
- `docs/v1/accessibility.html`
- `docs/v1/who-benefits.html`
- `docs/v1/privacy-policy.html`
- `docs/v1/releases.html`
- `SEO_AUDIT.md`
