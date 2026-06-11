# ERICK — Google Ads Campaign Kit

App: **ERICK — Inclusive Keyboard**
Package: `com.vatoo.erick`
Store URL: https://play.google.com/store/apps/details?id=com.vatoo.erick
GitHub: https://github.com/vatsalunadkat/ERICKeyboard

This kit contains everything needed to launch a Google Ads campaign for ERICK:
5 headlines, 5 descriptions, 20 App Store screenshot prompts, and 20 ready-to-upload HTML5 ads
(in [google-ads-html5/](google-ads-html5/)).

---

## 1. Campaign Strategy (read first)

**Goal: quality over volume.** We are *not* chasing the maximum number of installs. We want
people who genuinely need ERICK and will keep using it long-term — primarily **disabled users**
with a real text-entry barrier. A smaller, well-retained audience is the success metric, not raw
install count.

### Who we want (and why they stay)

- Motor disabilities, limited fine-finger control, tremor
- Repetitive strain injury (RSI), joint pain, hand fatigue
- One-handed users (permanent or temporary, e.g. injury, amputation, hemiplegia)
- People who type with a **game controller** instead of touch (motor access, TV/console)
- Dyslexia / visual-tracking needs (OpenDyslexic font, live previews)
- Colorblind users (colorblind-safe palettes)
- Privacy-critical users who refuse cloud keyboards

These users convert *and retain* because ERICK solves a daily, recurring pain point for them.

### Who we are NOT optimizing for

- Casual users looking for a "fun" or "novelty" keyboard
- People who just want emoji/GIF features (we have emoji, but it is not the hook)
- Pure speed-typists who are happy with QWERTY

### Recommended Google Ads setup

- **Campaign type:** App campaign (App promotion) → **Android, Google Play only.**
  iOS is "coming soon" and cannot be promoted yet — keep all copy/creatives Android-safe.
- **Optimization:** Optimize for **in-app actions / retention** (e.g. keyboard enabled +
  first practice lesson completed) rather than raw install volume, once you have a conversion
  signal. Start on installs, then switch to the deeper event as data accrues.
- **Audience signals / targeting ideas** (to bias toward genuine-need users):
  - Custom segments on search terms: *accessibility keyboard, one handed keyboard, big button
    keyboard, keyboard for disability, controller keyboard android, RSI typing, tremor typing,
    adaptive keyboard, switch access typing.*
  - Affinity / detailed demographics: assistive technology, disability support, AAC.
  - Placements: disability and accessibility communities, assistive-tech YouTube channels,
    r/disability-adjacent sites (via Display/Custom segments).
- **Geo/language:** Start in your strongest markets; ERICK UI ships in EN, ES, PT, FR, DE, IT,
  NB, DA, SV, FI — but **only English copy is provided below**. Localize before enabling other
  languages (the Play Store listing is already localized in `Play_Store_Copy.md`).
- **Landing/secondary destinations:** Play listing (primary). The website
  `who-benefits.html` and `accessibility.html` are strong reinforcement pages for Display/Search.

### Honest-claims guardrail (keep us compliant + trustworthy)

- ✅ Safe: large targets, lower reach/movement, one-handed mode, controller typing, fully offline,
  on-device predictions, accessibility-first design, "research-informed."
- ❌ Avoid: medical claims ("treats", "cures", "therapy"), fake testimonials, "fastest keyboard",
  or claiming research *proves* a specific group types faster. Mirror the research-safety note in
  `Social_Media_Copy.md`.

---

## 2. Brand System (use in every creative)

**Brand colors = the ERICK Pastel palette** (from `ColorPalettes.kt` / `ColorPaletteComponents.swift`).
Pastel is intentionally the brand identity: soft, calm, low-stress — matching the "calmer typing"
positioning. **On pastel, text and icons are always black (`#000000`)** — this is the shipped
contrast rule. Do not put white text on pastel.

| Name | Hex | Swatch use |
|------|-----|-----------|
| Rose | `#F4A6B0` | pain / comfort angle |
| Peach | `#F6C9A0` | warmth / CTA angle |
| Lemon | `#FDE9A0` | highlight / attention |
| Mint | `#A8DFC0` | ease / one-handed |
| Sky | `#A0C4E8` | trust / motor access |
| Lavender | `#C4A8D8` | calm / focus |
| Lilac | `#D8A8C8` | controller / play |
| Slate | `#8B8B8B` | privacy / neutral |

- **Text/icon color on pastel:** `#000000` (black). Always.
- **Neutral ink for body text on white:** `#1c1b1f`.
- **The dial motif** (a circle split into the 8 pastel segments) is the core recognizable brand
  device. Reuse it, but vary it per creative so ads stay visually distinct.
- **Logos:** `docs/documentation/logo/ERICK_black.png`, `ERICK_white.png`,
  `ERICK_feature_graphic_black.png`. App icon: `docs/images/erick-logo.png`.
- **Fonts:** clean sans (e.g. Inter/Roboto). OpenDyslexic only when demonstrating the dyslexia feature.

> **Why 20 of each?** Google Ads optimizes by testing creatives against each other. The 20 images
> and 20 HTML5 ads below are intentionally **visually different** (different dominant pastel color,
> layout, headline angle, and animation) so the system has real variety to find winners. They are
> not 20 copies of one design.

---

## 3. Headlines (5) — max 30 characters

Each line shown with its exact character count. All ≤ 30. Written for genuine-need users.

| # | Headline | Chars | Angle |
|---|----------|-------|-------|
| H1 | `Typing shouldn't hurt` | 21 | Pain / RSI / fatigue |
| H2 | `Type one-handed with ease` | 25 | One-handed users |
| H3 | `Tremor-friendly typing` | 22 | Motor / tremor |
| H4 | `Big dials, not tiny keys` | 24 | Limited dexterity / large targets |
| H5 | `Type with a controller` | 22 | Controller / TV typing |

**Optional extras for rotation** (Google Ads lets you add several headlines; all ≤ 30):

| Headline | Chars |
|----------|-------|
| `An accessibility keyboard` | 25 |
| `Made for motor access` | 21 |
| `Bigger keys, fewer slips` | 24 |
| `A calmer way to type` | 20 |
| `100% offline keyboard` | 21 |

---

## 4. Descriptions (5) — max 90 characters

Each line shown with its exact character count. All ≤ 90.

| # | Description | Chars |
|---|-------------|-------|
| D1 | `Type by combining two big dials. Large targets, less reach, fewer mistakes.` | 75 |
| D2 | `Built for motor needs, pain, fatigue, and one-handed use. Accessibility first.` | 78 |
| D3 | `Type with touch or a game controller. Fully offline, no cloud typing data.` | 74 |
| D4 | `Learn step by step with quickstart and practice lessons. Free on Google Play.` | 77 |
| D5 | `Dyslexia-friendly fonts, colorblind-safe colors, and one-handed mode. Try free.` | 79 |

---

## 5. App Store Screenshots — 20 image prompts

Format requested: **a real app screenshot inside a phone mockup, on a pastel background, with a
short title + subtitle** (classic App Store / Play Store "feature screenshot" style).

You provide the raw screenshots; each prompt below tells you **which ERICK screen to capture** and
gives the **exact title + subtitle + background color + layout** so an image generator (or a
designer in Figma/Canva) can assemble it. Backgrounds and layouts are deliberately varied so the 20
are visually distinct for A/B testing.

### Screens you can actually capture in ERICK

Capture these on a real device/emulator (Android, since the campaign is Android-only):

**Keyboard (in any app like Messages/Notes/Gmail):**
1. 8-section dial (default Logical layout) with a word being typed + live prediction strip
2. 6-section dial mode (`v1.2_six_dial_mode.png` already exists as reference)
3. Pastel palette active on the dials
4. Colorblind-safe palette active
5. OpenDyslexic / dyslexia-friendly font active
6. Dark mode keyboard
7. One-handed / Assisted mode (row locked)
8. Emoji keyboard mode
9. Symbols layer (6-section)
10. Left-handed mode

**Host app:**
11. Main / home screen (quickstart entry)
12. Settings screen
13. Custom palette editor (`CustomPaletteEditorScreen`)
14. Custom layout list / editor
15. Practice Hub lesson list (`PracticeHubActivity`)
16. A practice lesson detail
17. Quote practice / typing game (`TypingGameScreen`)
18. Help screen (`HelpActivity`)
19. Controller diagnostics (`ControllerDiagnosticsActivity`)

**Real-world / hero:**
20. Controller-in-hands typing photo (`real_users_trying_ERICK_1.jpg`, `v1.0_android_controller`)

> Tip: shoot all keyboard shots in the **same host app** and **same wallpaper** so the set looks
> cohesive even though backgrounds differ.

### The 20 prompts

Each prompt is copy-paste ready. Replace `[SCREENSHOT]` with the captured image. Keep the phone
mockup a clean modern Android device, screenshot perfectly inset, soft shadow. Titles are short and
high-contrast **black text on the pastel background**.

**IMG-01 — Pain/RSI hook · Rose `#F4A6B0`**
> App Store screenshot, solid Rose `#F4A6B0` background, single upright Android phone mockup
> centered-low showing [SCREENSHOT: 8-section dial keyboard mid-word with prediction strip].
> Title (top, bold black): "Typing shouldn't hurt". Subtitle (black, lighter): "Big targets. Less
> reach. Fewer painful taps." Soft drop shadow under phone, generous margins, no other elements.

**IMG-02 — Two-dial explainer · Sky `#A0C4E8`**
> App Store screenshot, Sky `#A0C4E8` background with a faint large pastel dial-circle watermark
> top-right, Android phone angled slightly left showing [SCREENSHOT: 8-section dial keyboard].
> Title: "Type by direction, not tiny keys". Subtitle: "Combine two big dials into one letter."
> Black text, clean sans font.

**IMG-03 — One-handed · Mint `#A8DFC0`**
> App Store screenshot, Mint `#A8DFC0` background, phone on the right third showing [SCREENSHOT:
> one-handed / Assisted mode keyboard], title + subtitle stacked on the left third. Title: "Built
> for one-handed use". Subtitle: "Lock a row so one hand finishes the chord." Black text.

**IMG-04 — Tremor/motor · Lavender `#C4A8D8`**
> App Store screenshot, Lavender `#C4A8D8` background, phone centered showing [SCREENSHOT: pastel
> palette dial keyboard]. Title (top): "Tremor-friendly typing". Subtitle (bottom): "Large, steady
> targets that are easier to control." Black text, calm minimal layout.

**IMG-05 — Controller typing · Lilac `#D8A8C8`**
> App Store screenshot, Lilac `#D8A8C8` background, phone centered showing [SCREENSHOT: keyboard
> while a game controller is connected], small line-art game-controller icon near the title. Title:
> "Type with a game controller". Subtitle: "Touch or gamepad — on phones, TVs, and consoles." Black text.

**IMG-06 — Large targets · Peach `#F6C9A0`**
> App Store screenshot, Peach `#F6C9A0` background, phone centered-low showing [SCREENSHOT: 6-section
> dial mode]. Title: "Bigger keys, fewer mistakes". Subtitle: "Two large dials replace a crowded
> grid." Black text. Add one faint pastel dial graphic behind the phone.

**IMG-07 — Privacy · Slate `#8B8B8B`**
> App Store screenshot, Slate `#8B8B8B` background, phone centered showing [SCREENSHOT: settings
> screen or keyboard], a small black padlock icon by the title. Title: "100% offline. Always private."
> Subtitle: "No internet permission. Keystrokes never leave your phone." Black text.

**IMG-08 — Dyslexia · Lemon `#FDE9A0`**
> App Store screenshot, Lemon `#FDE9A0` background, phone centered showing [SCREENSHOT: keyboard with
> OpenDyslexic font enabled]. Title: "Dyslexia-friendly fonts". Subtitle: "OpenDyslexic, live previews,
> and clear letter tracking." Black text.

**IMG-09 — Colorblind · multi-pastel**
> App Store screenshot, soft white-to-pastel gradient background with a horizontal row of the 8 pastel
> swatches near the bottom, phone centered showing [SCREENSHOT: colorblind-safe palette keyboard].
> Title: "Colorblind-safe by design". Subtitle: "Multiple palettes so every direction stays distinct."
> Black text.

**IMG-10 — Learn step by step · Mint `#A8DFC0`**
> App Store screenshot, Mint `#A8DFC0` background, phone centered showing [SCREENSHOT: Practice Hub
> lesson list]. Title: "Learn one step at a time". Subtitle: "Quickstart and practice lessons, no
> memorizing." Black text.

**IMG-11 — Ergonomic/fatigue · Sky `#A0C4E8`**
> App Store screenshot, Sky `#A0C4E8` background, phone tilted slightly right showing [SCREENSHOT:
> 8-section dial keyboard]. Title: "Less reach, less strain". Subtitle: "Broad, repeatable motions
> instead of scattered taps." Black text.

**IMG-12 — Brand statement · Lavender `#C4A8D8`**
> App Store screenshot, Lavender `#C4A8D8` background, phone centered showing [SCREENSHOT: home /
> main screen], ERICK black logo (`ERICK_black.png`) small at the top. Title: "An accessibility-first
> keyboard". Subtitle: "Designed for the people standard keyboards leave out." Black text.

**IMG-13 — Bold anti-tiny-keys · Rose `#F4A6B0` + Lilac `#D8A8C8`**
> App Store screenshot, diagonal two-tone background split Rose `#F4A6B0` / Lilac `#D8A8C8`, phone
> centered showing [SCREENSHOT: 8-section dial keyboard]. Big bold title: "No more tiny keys".
> Subtitle: "A whole new way to type on your phone." Black text.

**IMG-14 — Eyes-free · Slate `#8B8B8B` + Sky `#A0C4E8`**
> App Store screenshot, Slate-to-Sky gradient background, phone centered showing [SCREENSHOT: dial
> keyboard with large preview]. Title: "Type without looking". Subtitle: "Predictable directions you
> can feel, not hunt for." Black text.

**IMG-15 — Motor access direct · Sky `#A0C4E8`**
> App Store screenshot, Sky `#A0C4E8` background, phone on left showing [SCREENSHOT: keyboard], title
> + subtitle on right. Title: "Made for motor accessibility". Subtitle: "Large targets and controller
> input for limited dexterity." Black text.

**IMG-16 — Couch/TV · Lilac `#D8A8C8`**
> App Store screenshot, Lilac `#D8A8C8` background, phone centered with a faint TV + controller line-art
> behind it, showing [SCREENSHOT: controller diagnostics or keyboard with controller]. Title: "Type
> from the couch". Subtitle: "Controller typing for TVs, consoles, and set-top boxes." Black text.

**IMG-17 — CTA / free · Peach `#F6C9A0`**
> App Store screenshot, Peach `#F6C9A0` background, phone centered showing [SCREENSHOT: 8-section dial
> keyboard], a black "Get it free on Google Play" pill button graphic under the phone. Title: "Free on
> Google Play". Subtitle: "No ads in the keyboard. No tracking." Black text.

**IMG-18 — Calm · Lavender `#C4A8D8`**
> App Store screenshot, soft Lavender `#C4A8D8` radial-glow background, phone centered showing
> [SCREENSHOT: pastel palette keyboard]. Title: "A calmer way to type". Subtitle: "Soft pastel design
> built to reduce typing stress." Black text.

**IMG-19 — Customization · multi-pastel chips**
> App Store screenshot, white background with scattered soft pastel rounded chips, phone centered
> showing [SCREENSHOT: custom palette editor or custom layout editor]. Title: "Your keys, your colors".
> Subtitle: "Custom palettes, layouts, themes, and fonts." Black text.

**IMG-20 — Human hero · Rose `#F4A6B0`**
> App Store screenshot, warm Rose `#F4A6B0` background, real photo [SCREENSHOT: hands using a controller
> to type — `real_users_trying_ERICK_1.jpg`] composited inside or beside a phone mockup. Title: "Typing,
> made comfortable". Subtitle: "An inclusive keyboard for real, everyday hands." Black text.

---

## 6. HTML5 ads — 20 files (ready to upload)

Located in **[google-ads-html5/](google-ads-html5/)**. Each is a **single self-contained `.html`
file** (no external files, no libraries), with:

- the required `<meta name="ad.size" content="width=W,height=H">` tag,
- a single `clickTag` variable pointing to the Play listing,
- pure-CSS animation (lightweight, well under Google's 150 KB limit),
- the ERICK **pastel** brand colors with **black text** (brand contrast rule).

They are **intentionally visually distinct** (different size, dominant color, layout, headline,
and animation) so Google Ads can test which performs best.

### How to upload

1. Open the [google-ads-html5/](google-ads-html5/) folder.
2. **Zip each `.html` file individually** (Google Ads accepts a single HTML file per zip, or the raw
   file via the HTML5 upload). One creative = one zip.
   - macOS: select a file → right-click → Compress. Repeat per file, or run the script in
     section 7 to zip all 20 at once.
3. In Google Ads: **Campaign → Ads → + → Upload display ad → HTML5**, then drop in each zip.
4. Set the **Final URL** to `https://play.google.com/store/apps/details?id=com.vatoo.erick`
   (this matches the built-in `clickTag`).
5. Preview each, then add to the ad group.

> Note: App campaigns auto-assemble creatives from your assets, so HTML5 banners are most useful in
> a **Display campaign** (or Display network) that reinforces the App campaign. Use the App campaign
> for installs and the HTML5 set for retargeting/awareness in accessibility placements.

### Index of the 20 HTML5 ads

| # | File | Size | Dominant color | Headline angle |
|---|------|------|----------------|----------------|
| 01 | [ad-01-300x250-pain-rose.html](google-ads-html5/ad-01-300x250-pain-rose.html) | 300×250 | Rose | Typing shouldn't hurt |
| 02 | [ad-02-336x280-twodials-sky.html](google-ads-html5/ad-02-336x280-twodials-sky.html) | 336×280 | Sky | Type by direction |
| 03 | [ad-03-728x90-onehanded-mint.html](google-ads-html5/ad-03-728x90-onehanded-mint.html) | 728×90 | Mint | One-handed with ease |
| 04 | [ad-04-300x600-tremor-lavender.html](google-ads-html5/ad-04-300x600-tremor-lavender.html) | 300×600 | Lavender | Tremor-friendly typing |
| 05 | [ad-05-160x600-controller-lilac.html](google-ads-html5/ad-05-160x600-controller-lilac.html) | 160×600 | Lilac | Type with a controller |
| 06 | [ad-06-320x50-bigkeys-peach.html](google-ads-html5/ad-06-320x50-bigkeys-peach.html) | 320×50 | Peach | Big dials, not tiny keys |
| 07 | [ad-07-320x100-a11y-sky.html](google-ads-html5/ad-07-320x100-a11y-sky.html) | 320×100 | Sky | Accessibility-first |
| 08 | [ad-08-970x250-billboard-multi.html](google-ads-html5/ad-08-970x250-billboard-multi.html) | 970×250 | Multi-pastel | No tiny keys |
| 09 | [ad-09-250x250-offline-slate.html](google-ads-html5/ad-09-250x250-offline-slate.html) | 250×250 | Slate | 100% offline & private |
| 10 | [ad-10-300x250-targets-peach.html](google-ads-html5/ad-10-300x250-targets-peach.html) | 300×250 | Peach | Bigger keys, fewer slips |
| 11 | [ad-11-336x280-strain-mint.html](google-ads-html5/ad-11-336x280-strain-mint.html) | 336×280 | Mint | Less reach, less strain |
| 12 | [ad-12-468x60-brand-lavender.html](google-ads-html5/ad-12-468x60-brand-lavender.html) | 468×60 | Lavender | An accessible keyboard |
| 13 | [ad-13-300x250-bold-rose-lilac.html](google-ads-html5/ad-13-300x250-bold-rose-lilac.html) | 300×250 | Rose/Lilac | No more tiny keys |
| 14 | [ad-14-970x90-eyesfree-slate.html](google-ads-html5/ad-14-970x90-eyesfree-slate.html) | 970×90 | Slate/Sky | Type without looking |
| 15 | [ad-15-320x50-motor-sky.html](google-ads-html5/ad-15-320x50-motor-sky.html) | 320×50 | Sky | Made for motor access |
| 16 | [ad-16-120x600-tv-lilac.html](google-ads-html5/ad-16-120x600-tv-lilac.html) | 120×600 | Lilac | Couch & TV typing |
| 17 | [ad-17-728x90-free-peach.html](google-ads-html5/ad-17-728x90-free-peach.html) | 728×90 | Peach | Free on Google Play |
| 18 | [ad-18-300x600-calm-lavender.html](google-ads-html5/ad-18-300x600-calm-lavender.html) | 300×600 | Lavender | A calmer way to type |
| 19 | [ad-19-160x600-custom-multi.html](google-ads-html5/ad-19-160x600-custom-multi.html) | 160×600 | Multi-pastel | Your keys, your colors |
| 20 | [ad-20-320x100-comfort-rose.html](google-ads-html5/ad-20-320x100-comfort-rose.html) | 320×100 | Rose | Typing made comfortable |

**Size coverage** (good A/B + inventory mix): 300×250 ×3, 336×280 ×2, 728×90 ×2, 300×600 ×2,
160×600 ×2, 320×50 ×2, 320×100 ×2, 970×250, 970×90, 468×60, 250×250, 120×600.

---

## 7. Optional: zip all 20 HTML5 ads at once

From the repo root, this creates one zip per ad in `google-ads-html5/zips/`:

```bash
cd docs/documentation/google-ads-html5
mkdir -p zips
for f in ad-*.html; do
  zip -j "zips/${f%.html}.zip" "$f"
done
```

Upload each `zips/ad-XX-....zip` to Google Ads as an HTML5 display creative.

---

## 8. Pre-launch checklist

- [ ] Capture the 19 in-app screenshots + 1 hero photo listed in section 5.
- [ ] Assemble the 20 App Store screenshots from the prompts (keep black text on pastel).
- [ ] Confirm all 5 headlines ≤ 30 chars and 5 descriptions ≤ 90 chars (counts above).
- [ ] Upload the 20 HTML5 zips; verify each previews and clicks through to the Play listing.
- [ ] Set campaign to **Android / Google Play only**; do not mention iOS as available.
- [ ] Configure accessibility-leaning audience signals / custom segments (section 1).
- [ ] Start on installs; move to a deeper retention event once the conversion signal is stable.
- [ ] Re-localize copy before enabling non-English markets.
