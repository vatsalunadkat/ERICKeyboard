# ERICK — Google Ads Campaign Kit

App: **ERICK — Inclusive Keyboard**
Package: `com.vatoo.erick`
Store URL: https://play.google.com/store/apps/details?id=com.vatoo.erick
GitHub: https://github.com/vatsalunadkat/ERICKeyboard

This kit contains everything for an ERICK Google Ads campaign: 5 headlines, 5 descriptions,
20 App Store screenshot prompts, and 20 ready-to-upload HTML5 ads (in
[google-ads-html5/](google-ads-html5/)).

**Writing rule for this whole campaign: keep the language very simple.** Short words, short
sentences, plain meaning. Many of the people we want to reach value clear, calm, easy wording.

---

## 1. The 12 things this campaign is about

Every headline, description, screenshot, and HTML5 ad maps to one of these. They are ordered by
priority. (Privacy is included but low priority.)

1. **For users with disability** — accessibility first.
2. **For one-handed users** — type fully with one hand.
3. **Ad free** — no ads inside the keyboard.
4. **Forever free, no hidden costs** — free to keep, no surprise charges.
5. **Privacy focused** *(low priority)* — typing stays on the phone.
6. **Gentle, calm, decluttered** — soft design, no clutter, low stress.
7. **For users with autism** — a clear, logical A–Z layout that is easy to predict.
8. **Colorblind color options and dyslexia-friendly fonts** — easier to see and read.
9. **Fully customizable** — your colors, your layout, your fonts.
10. **Backed by research** — built on accessibility research.
11. **Support for all types of controllers** — Xbox, PlayStation, 8BitDo and more.
12. **Built for motor needs, pain, fatigue, and one-handed use** — accessibility first.

---

## 2. Campaign Strategy (read first)

**Goal: quality over volume.** We are *not* chasing the most installs. We want people who truly
need ERICK and will keep using it — first and foremost **disabled users** with a real typing
barrier. A small, loyal, long-term audience is the win, not a big install number.

### Who we want (and why they stay)

- People with disabilities and motor needs (limited finger control, tremor, weakness)
- One-handed users (permanent or temporary — injury, amputation, hemiplegia)
- People with pain or fatigue (RSI, joint pain, tiredness from typing)
- Autistic users who prefer a clear, predictable, logical layout
- Dyslexic users (dyslexia-friendly font, live previews) and colorblind users (safe color sets)
- People who type with a game controller instead of touch
- People who want a calm, clutter-free, ad-free, private keyboard

They stay because ERICK fixes a daily problem for them — not a one-time novelty.

### Who we are NOT optimizing for

- People who just want a "fun" or novelty keyboard
- People who only want emoji/GIF features (we have emoji, but it is not the hook)
- Fast QWERTY typists who already have no problem

### Recommended Google Ads setup

- **Campaign type:** App campaign (App promotion) → **Android, Google Play only.** iOS is "coming
  soon" and cannot be promoted yet, so keep all copy Android-safe.
- **HTML5 ads run in a Display campaign.** App campaigns auto-assemble their own creatives, so use
  the App campaign for installs and run the 20 HTML5 banners in a **Display campaign** that
  reinforces it in accessibility-related placements.
- **Optimize for retention, not raw installs.** Start on installs, then switch to a deeper event
  (keyboard enabled + first practice lesson done) once you have a conversion signal.
- **Audience signals / search terms to lean into:** *accessibility keyboard, one handed keyboard,
  big button keyboard, keyboard for disability, controller keyboard android, RSI typing, tremor
  typing, dyslexia keyboard, autism keyboard, adaptive keyboard, switch access typing.*
- **Placements:** disability and accessibility communities, assistive-tech YouTube channels,
  dyslexia/autism support sites (via Display custom segments).
- **Language:** Only **English** copy is provided here. ERICK's UI also ships in ES, PT, FR, DE,
  IT, NB, DA, SV, FI, and the Play listing is localized in `Play_Store_Copy.md` — localize before
  enabling other languages.
- **Reinforcement pages:** Play listing (primary), plus `who-benefits.html` and `accessibility.html`.

### Honest-claims guardrail (stay compliant + trustworthy)

- ✅ Safe: big targets, less reach/movement, one-handed mode, controller support, fully offline,
  on-device predictions, ad-free, free, accessibility-first, "built on accessibility research."
- ❌ Avoid: medical claims ("treats", "cures", "therapy"); fake testimonials; "fastest keyboard";
  and **do not claim research proves autistic users type faster** — frame autism around the clear,
  logical, predictable layout (a design choice), not a proven speed result. Mirror the research
  note in `Social_Media_Copy.md`.

---

## 3. Brand System (use in every creative)

**Brand colors = the ERICK Pastel palette** (from `ColorPalettes.kt` / `ColorPaletteComponents.swift`).
Pastel *is* the brand: soft, calm, low-stress — exactly the "gentle, calm, decluttered" message.
**On pastel, text and icons are always black (`#000000`)** — this is the shipped contrast rule.
Never put white text on pastel.

| Name | Hex | Used in this kit for |
|------|-----|----------------------|
| Rose | `#F4A6B0` | motor / pain / colorblind+dyslexia |
| Peach | `#F6C9A0` | free / no hidden costs |
| Lemon | `#FDE9A0` | ad-free / dyslexia+colorblind |
| Mint | `#A8DFC0` | one-handed / motor |
| Sky | `#A0C4E8` | disability / autism (logical layout) |
| Lavender | `#C4A8D8` | calm, decluttered |
| Lilac | `#D8A8C8` | controllers |
| Slate | `#8B8B8B` | privacy / research |

- **Text/icon color on pastel:** `#000000` (black). Always.
- **The dial** (a circle split into the 8 pastel segments) is the core brand shape. Reuse it, but
  vary it per ad so creatives stay visually different.
- **Logos:** `docs/documentation/logo/ERICK_black.png`, `ERICK_white.png`,
  `ERICK_feature_graphic_black.png`. App icon: `docs/images/erick-logo.png`.
- **Fonts:** clean sans (Inter/Roboto). Use OpenDyslexic only in the dyslexia creative.

> **Why 20 different ones?** Google Ads finds winners by testing creatives against each other.
> The 20 images and 20 HTML5 ads are intentionally **visually different** — different size,
> dominant pastel color, layout, headline, and animation. They are not 20 copies of one design.

---

## 4. Headlines (5) — max 30 characters

Simple wording. Exact character counts shown; all ≤ 30.

| # | Headline | Chars | Theme |
|---|----------|-------|-------|
| H1 | `Made for disabled users` | 23 | 1 Disability |
| H2 | `Type with one hand` | 18 | 2 One-handed |
| H3 | `Free forever. No ads.` | 21 | 3 + 4 Ad-free / free |
| H4 | `A calm, simple keyboard` | 23 | 6 Calm, decluttered |
| H5 | `Type with any controller` | 24 | 11 All controllers |

**Extra headlines for rotation** (add them all in Google Ads; all ≤ 30):

| Headline | Chars | Theme |
|----------|-------|-------|
| `Big keys, not tiny ones` | 23 | 12 Big targets |
| `Gentle on sore hands` | 20 | 12 Pain / fatigue |
| `A clear A to Z layout` | 21 | 7 Autism / logical |
| `Easy to see and read` | 20 | 8 Colorblind / dyslexia |
| `Make it your own` | 16 | 9 Customizable |
| `Built on research` | 17 | 10 Research |
| `Your typing stays private` | 25 | 5 Privacy |
| `No ads in your keyboard` | 23 | 3 Ad-free |

---

## 5. Descriptions (5) — max 90 characters

Simple wording. Exact character counts shown; all ≤ 90.

| # | Description | Chars | Themes |
|---|-------------|-------|--------|
| D1 | `Made for disabled users first. Big, easy keys instead of tiny ones.` | 67 | 1, 12 |
| D2 | `Type with just one hand, or with any game controller.` | 53 | 2, 11 |
| D3 | `Free forever. No ads, no hidden costs, no tracking.` | 51 | 3, 4, 5 |
| D4 | `A calm, simple layout with letters in a clear A to Z order.` | 59 | 6, 7 |
| D5 | `Colorblind-safe colors, dyslexia-friendly fonts, fully your own.` | 64 | 8, 9 |

**Extra description for rotation** (≤ 90):

| Description | Chars | Themes |
|-------------|-------|--------|
| `Built on accessibility research. Your typing stays on your phone.` | 65 | 10, 5 |

---

## 6. App Store Screenshots — 20 image prompts

Format: **a real app screenshot inside a phone mockup, on a pastel background, with a short
title + subtitle** (the classic Play Store "feature screenshot" look). You provide the raw
screenshot; each prompt gives the screen to capture, the exact title + subtitle, the background
color, and the layout. The 20 are matched 1:1 to the 20 HTML5 ads (same theme + color), and
backgrounds/layouts vary so they are visually distinct for A/B testing.

**All titles and subtitles are black text on the pastel background.** Keep wording simple.

### Screens you can capture in ERICK (Android)

Capture on a real device/emulator:

**Keyboard (inside Messages / Notes / Gmail):**
1. 8-section dial, Logical layout, mid-word, with the prediction strip showing
2. 6-section dial mode (`v1.2_six_dial_mode.png` exists as a reference)
3. Pastel palette on the dials
4. Colorblind-safe palette on the dials
5. Dyslexia-friendly (OpenDyslexic) font on
6. One-handed / Assisted mode (a row locked)
7. Dark mode keyboard
8. Keyboard with a game controller connected

**Host app:**
9. Home / main screen (quickstart entry)
10. Settings screen
11. Custom palette editor (`CustomPaletteEditorScreen`)
12. Custom layout editor / list (`CustomLayoutEditorScreen`)
13. Practice Hub (`PracticeHubActivity`)
14. Help screen (`HelpActivity`)
15. Controller diagnostics (`ControllerDiagnosticsActivity`)

**Real photo:**
16. Hands using a controller to type (`real_users_trying_ERICK_1.jpg`, `v1.0_android_controller`)

> Tip: shoot every keyboard screenshot in the **same app** with the **same wallpaper** so the set
> feels like one family even though the backgrounds differ.

### The 20 prompts

Replace `[SCREENSHOT]` with the captured image. Phone mockup = clean modern Android, screenshot
inset cleanly, soft shadow.

**IMG-01 — Disability · Sky `#A0C4E8`**
> Sky `#A0C4E8` background, phone centered showing [SCREENSHOT: 8-section dial keyboard mid-word].
> Title: "An easier keyboard". Subtitle: "Made for disabled users first." Black text, lots of space.

**IMG-02 — One-handed · Mint `#A8DFC0`**
> Mint `#A8DFC0` background, phone on the right showing [SCREENSHOT: one-handed / Assisted mode],
> text on the left. Title: "Made for one hand". Subtitle: "One hand can do all the typing." Black text.

**IMG-03 — Free forever · Peach `#F6C9A0`**
> Peach `#F6C9A0` background, phone centered showing [SCREENSHOT: dial keyboard], a small black
> "Free" tag near the title. Title: "Free forever". Subtitle: "No hidden costs. No surprise charges."

**IMG-04 — Ad-free · Lemon `#FDE9A0`**
> Lemon `#FDE9A0` background, phone centered showing [SCREENSHOT: clean dial keyboard]. Title:
> "No ads. Ever." Subtitle: "A keyboard with no ads inside." Black text, very clean.

**IMG-05 — Controllers · Lilac `#D8A8C8`**
> Lilac `#D8A8C8` background, phone centered showing [SCREENSHOT: keyboard with a controller
> connected], small black controller icon by the title. Title: "Type with any controller".
> Subtitle: "Xbox, PlayStation, 8BitDo and more."

**IMG-06 — Calm, decluttered · Lavender `#C4A8D8`**
> Soft Lavender `#C4A8D8` background, phone centered showing [SCREENSHOT: pastel palette keyboard].
> Title: "A calm, simple keyboard". Subtitle: "No clutter. Soft colors. Easy to read."

**IMG-07 — Autism / logical · Sky `#A0C4E8`**
> Sky `#A0C4E8` background, phone centered showing [SCREENSHOT: Logical layout keyboard with A–Z
> letters clearly visible]. Title: "Letters in a clear order". Subtitle: "A simple A to Z layout,
> made with autistic users in mind."

**IMG-08 — Colorblind + dyslexia · Rose `#F4A6B0`**
> Rose `#F4A6B0` background with a small row of pastel dots, phone centered showing [SCREENSHOT:
> colorblind-safe palette keyboard, or dyslexia font on]. Title: "Easy to see and read". Subtitle:
> "Colorblind-safe colors. Dyslexia-friendly fonts."

**IMG-09 — Customizable · white + pastel chips**
> White background with scattered soft pastel rounded chips, phone centered showing [SCREENSHOT:
> custom palette editor]. Title: "Make it your own". Subtitle: "Your colors. Your layout. Your fonts."

**IMG-10 — Research · Slate `#8B8B8B`**
> Slate `#8B8B8B` background, phone centered showing [SCREENSHOT: home screen or keyboard]. Title:
> "Built on research". Subtitle: "Designed around easier motor access and less movement." Black text.

**IMG-11 — Motor / pain · Rose `#F4A6B0`**
> Rose `#F4A6B0` background, phone centered showing [SCREENSHOT: dial keyboard with big targets].
> Title: "Gentle on sore hands". Subtitle: "Less reach and less effort for pain and fatigue."

**IMG-12 — Big, clear, calm · Lavender `#C4A8D8`**
> Lavender `#C4A8D8` radial-glow background, phone centered showing [SCREENSHOT: large pastel
> keyboard]. Title: "Big, clear and calm". Subtitle: "Large keys. Soft colors. No stress."

**IMG-13 — One-handed (variant) · Mint `#A8DFC0`**
> Mint `#A8DFC0` background, phone centered showing [SCREENSHOT: one-handed / Assisted mode]. Title:
> "One hand is enough". Subtitle: "Type fully with just one hand." Black text.

**IMG-14 — Disability hero · multi-pastel on white**
> White background with a thin 8-color pastel strip along the bottom, phone centered showing
> [SCREENSHOT: home / main screen]. Title: "A keyboard for everyone". Subtitle: "Made for disabled
> users first." ERICK black logo small at the top.

**IMG-15 — Customizable colors · multi-pastel chips**
> White background with pastel chips, phone centered showing [SCREENSHOT: custom layout editor].
> Title: "Your keys, your colors". Subtitle: "Custom colors, layouts and fonts." Black text.

**IMG-16 — Free + no ads · Peach `#F6C9A0`**
> Peach `#F6C9A0` background, phone centered showing [SCREENSHOT: dial keyboard], a black "Get it
> free on Google Play" pill under the phone. Title: "Free. No ads. No catch." Subtitle: "Forever
> free, with no hidden costs."

**IMG-17 — Controllers (human) · Lilac `#D8A8C8`**
> Warm Lilac `#D8A8C8` background, real photo [SCREENSHOT: hands using a controller —
> `real_users_trying_ERICK_1.jpg`] beside or inside a phone mockup. Title: "Works with your
> controller". Subtitle: "Type without touching the screen."

**IMG-18 — Privacy · Slate `#8B8B8B`**
> Slate `#8B8B8B` background, phone centered showing [SCREENSHOT: settings screen], small black
> padlock by the title. Title: "Stays on your phone". Subtitle: "Your typing never leaves your device."

**IMG-19 — Motor / fatigue (variant) · Mint `#A8DFC0`**
> Mint `#A8DFC0` background, phone centered showing [SCREENSHOT: dial keyboard]. Title: "Kinder to
> tired hands". Subtitle: "Big targets, less reach, less strain." Black text.

**IMG-20 — Dyslexia / colorblind (variant) · Lemon `#FDE9A0`**
> Lemon `#FDE9A0` background with a small row of pastel dots, phone centered showing [SCREENSHOT:
> keyboard with OpenDyslexic font on]. Title: "Colors and fonts that help". Subtitle:
> "Colorblind-safe colors and dyslexia-friendly fonts."

---

## 7. HTML5 ads — 20 files (ready to upload)

Located in **[google-ads-html5/](google-ads-html5/)**. Each is one self-contained `.html` file
(no external files, no libraries), with:

- the required `<meta name="ad.size" content="width=W,height=H">` tag,
- one `clickTag` variable pointing to the Play listing,
- pure-CSS animation (lightweight, well under the limits),
- ERICK **pastel** brand colors with **black text** (brand contrast rule),
- simple wording, each ad on one of the 12 themes.

They are **visually distinct** — different size, color, layout, headline, and animation — so Google
Ads can test which works best.

### Supported sizes only

These 20 use **only sizes Google Ads accepts for HTML5**, spread across 10 dimensions. **300×250
and 320×50 are deliberately not used** (per your account's rejections). If you ever see a
"dimensions do not match a supported size" error, run the file through Google's official HTML5
validator first (`h5validator.appspot.com/adwords/asset`) — that error can also fire for
non-size reasons (e.g. packaging).

### How to upload

1. Open [google-ads-html5/](google-ads-html5/).
2. **Zip each `.html` file on its own** (one creative = one zip). Use the script in section 8 to
   zip all 20 at once.
3. In Google Ads (Display campaign): **Ads → + → Upload display ad → HTML5**, drop in each zip.
4. Set the **Final URL** to `https://play.google.com/store/apps/details?id=com.vatoo.erick`
   (matches the built-in `clickTag`).
5. Preview each, then add to the ad group.

### Index of the 20 HTML5 ads

| # | File | Size | Color | Theme |
|---|------|------|-------|-------|
| 01 | [ad-01-336x280-accessibility-sky.html](google-ads-html5/ad-01-336x280-accessibility-sky.html) | 336×280 | Sky | 1 Disability |
| 02 | [ad-02-300x600-onehanded-mint.html](google-ads-html5/ad-02-300x600-onehanded-mint.html) | 300×600 | Mint | 2 One-handed |
| 03 | [ad-03-728x90-freeforever-peach.html](google-ads-html5/ad-03-728x90-freeforever-peach.html) | 728×90 | Peach | 4 Forever free |
| 04 | [ad-04-970x250-adfree-lemon.html](google-ads-html5/ad-04-970x250-adfree-lemon.html) | 970×250 | Lemon | 3 Ad-free |
| 05 | [ad-05-160x600-controllers-lilac.html](google-ads-html5/ad-05-160x600-controllers-lilac.html) | 160×600 | Lilac | 11 Controllers |
| 06 | [ad-06-320x100-calm-lavender.html](google-ads-html5/ad-06-320x100-calm-lavender.html) | 320×100 | Lavender | 6 Calm |
| 07 | [ad-07-250x250-autism-sky.html](google-ads-html5/ad-07-250x250-autism-sky.html) | 250×250 | Sky | 7 Autism / logical |
| 08 | [ad-08-200x200-colorblind-rose.html](google-ads-html5/ad-08-200x200-colorblind-rose.html) | 200×200 | Rose | 8 Colorblind + dyslexia |
| 09 | [ad-09-300x1050-custom-multi.html](google-ads-html5/ad-09-300x1050-custom-multi.html) | 300×1050 | Multi | 9 Customizable |
| 10 | [ad-10-970x90-research-slate.html](google-ads-html5/ad-10-970x90-research-slate.html) | 970×90 | Slate | 10 Research |
| 11 | [ad-11-336x280-motor-rose.html](google-ads-html5/ad-11-336x280-motor-rose.html) | 336×280 | Rose | 12 Motor / pain |
| 12 | [ad-12-300x600-calmclear-lavender.html](google-ads-html5/ad-12-300x600-calmclear-lavender.html) | 300×600 | Lavender | 6 Calm / big & clear |
| 13 | [ad-13-728x90-onehanded-mint.html](google-ads-html5/ad-13-728x90-onehanded-mint.html) | 728×90 | Mint | 2 One-handed |
| 14 | [ad-14-970x250-disability-multi.html](google-ads-html5/ad-14-970x250-disability-multi.html) | 970×250 | Multi | 1 Disability (hero) |
| 15 | [ad-15-160x600-custom-multi.html](google-ads-html5/ad-15-160x600-custom-multi.html) | 160×600 | Multi | 9 Customizable |
| 16 | [ad-16-320x100-freenoads-peach.html](google-ads-html5/ad-16-320x100-freenoads-peach.html) | 320×100 | Peach | 3 + 4 Free / no ads |
| 17 | [ad-17-250x250-controllers-lilac.html](google-ads-html5/ad-17-250x250-controllers-lilac.html) | 250×250 | Lilac | 11 Controllers |
| 18 | [ad-18-200x200-privacy-slate.html](google-ads-html5/ad-18-200x200-privacy-slate.html) | 200×200 | Slate | 5 Privacy |
| 19 | [ad-19-300x1050-motor-mint.html](google-ads-html5/ad-19-300x1050-motor-mint.html) | 300×1050 | Mint | 12 Motor / fatigue |
| 20 | [ad-20-970x90-dyslexia-lemon.html](google-ads-html5/ad-20-970x90-dyslexia-lemon.html) | 970×90 | Lemon | 8 Colorblind + dyslexia |

**Sizes used (all supported, 2 each):** 336×280, 300×600, 728×90, 970×250, 160×600, 320×100,
250×250, 200×200, 300×1050, 970×90.

**Theme coverage:** Disability 2, One-handed 2, Ad-free/Free 3, Privacy 1 (low priority by design),
Calm 2, Autism 1, Colorblind+dyslexia 2, Customizable 2, Research 1, Controllers 2, Motor/pain 2.

---

## 8. Optional: zip all 20 HTML5 ads at once

```bash
cd docs/documentation/google-ads-html5
mkdir -p zips
for f in ad-*.html; do
  zip -j "zips/${f%.html}.zip" "$f"
done
```

Upload each `zips/ad-XX-....zip` to Google Ads as an HTML5 display creative.

---

## 9. Pre-launch checklist

- [ ] Capture the in-app screenshots + the controller photo listed in section 6.
- [ ] Assemble the 20 App Store screenshots from the prompts (black text on pastel, simple wording).
- [ ] Headlines ≤ 30 chars and descriptions ≤ 90 chars (counts in sections 4 and 5).
- [ ] Zip and upload the 20 HTML5 ads; preview each and confirm it clicks through to the Play listing.
- [ ] Run any rejected HTML5 file through `h5validator.appspot.com/adwords/asset` to see the real reason.
- [ ] Set the campaign to **Android / Google Play only**; do not say iOS is available.
- [ ] Add the accessibility-leaning audience signals / search terms (section 2).
- [ ] Start on installs, then move to a retention event once the signal is stable.
- [ ] Re-localize copy before turning on non-English markets.
