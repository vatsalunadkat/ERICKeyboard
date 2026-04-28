# Branch 3 - Accessibility Persona Evaluation

## Evidence Reviewed

- android/app/src/main/java/com/vatoo/erick/BenefitAudienceContent.kt
- android/app/src/main/java/com/vatoo/erick/MainSettingsContent.kt
- android/app/src/main/java/com/vatoo/erick/LearningAndPracticeModels.kt
- android/app/src/main/java/com/vatoo/erick/HelpActivity.kt
- android/app/src/main/java/com/vatoo/erick/PreferencesManager.kt
- docs/documentation/User_Guide.md

## Persona-And-Task Matrix

| Persona bundle | Representative tasks | Current ERICK surfaces that matter most | Current gap |
|---|---|---|---|
| Motor variability / reduced precision touch | Short messages, search terms, correction after a mistake | 6-section dial, Logical layout, input modes, haptics, lesson preset auto-application | No persona-specific recommended bundle in settings or onboarding |
| Low vision / high visual effort | Short phrases, symbols, scanning preview rows | 6-section dial, colorblind palettes, OpenDyslexic on Android, preview-based lessons, haptics | Guidance is generic; no visual-access starter preset or persona-targeted setup card |
| One-handed use | Quick replies, utility actions, short note entry | Assisted mode, Left-Handed Mode, dedicated Assisted One-Handed lesson | One-handed setup is taught only in one lesson and not surfaced as a tailored starting path |
| Controller-first use | TV search, couch typing, short punctuation-heavy tasks | Controller Diagnostics, dead zone, Y-axis inversion, controller drill, controller quickstart step | Controller is still framed as an alternate path instead of a primary persona bundle |
| Mixed fatigue / long-session typing | Longer messaging, repeated short phrases, low-effort editing | 6-section dial, haptics toggle, typing sounds toggle, offline predictions, lessons | No fatigue-oriented default bundle or benchmark tasks yet |

## Accessibility Evaluation Scorecard

| Evaluation area | Current state | Evidence level | Notes |
|---|---|---|---|
| Larger-target typing path | Strong | Shipped and taught | 6-section mode is a real product path with Quickstart, lessons, and guide coverage. |
| One-handed typing path | Strong but isolated | Shipped and taught in one lesson | Assisted mode and Left-Handed Mode exist, but they are not promoted as a persona-specific setup recommendation. |
| Controller alternative input | Strong on Android, partial at product-guidance level | Shipped diagnostics plus lesson | The mechanics are real, but guidance still lives in diagnostics and generic copy instead of persona routing. |
| Visual differentiation and scan support | Strong at option level, partial at guidance level | Shipped palettes and fonts | The settings surface offers multiple palette and font choices, but it does not recommend which bundle matches which need. |
| Fatigue and comfort tuning | Partial | Benefit copy plus settings toggles | ERICK can plausibly help here, but the product does not yet define a fatigue-oriented task bundle or default setup. |
| Persona-specific setup guidance | Gap | Not shipped | Current copy says ERICK can help many groups, but setup remains one-size-fits-all. |

## Recommended Default Bundles Or Setup Guidance Memo

### 1. Reduced precision touch

- Start in 6-section mode.
- Keep the Logical layout first.
- Prefer `Steady Type` when accidental commits are a bigger problem than raw speed.
- Enable haptics when the device and user tolerate it.

Why: this is the clearest current larger-target path, and it aligns with ERICK's shipped accessibility claims.

### 2. Low vision / high visual effort

- Start in 6-section mode.
- Keep the Logical layout first.
- Use the colorblind-safe palette family when it improves contrast for the user.
- Use OpenDyslexic on Android when letter tracking is the priority.
- Keep haptics on as a non-visual confirmation channel.

Why: the current settings surface already exposes the right ingredients, but users are forced to assemble the bundle themselves.

### 3. One-handed use

- Start with Assisted mode and the Logical layout.
- Offer Left-Handed Mode as a physical-orientation toggle, not as a separate persona.
- Keep the first one-handed lesson close to setup instead of burying it after general lessons.

Why: ERICK already has a working one-handed flow, but it is taught too late to function as a primary entry path.

### 4. Controller-first use

- Start in 8-section Logical Quick Type to match the current controller drill and default controller lesson assumptions.
- Route the user through dead zone and Y-axis checks before text drills.
- Keep controller haptics enabled when the hardware exposes rumble.

Why: the current controller path is real enough to deserve a bundle, and it should not be treated as a side note inside generic onboarding.

### 5. Mixed fatigue / long-session typing

- Start in 6-section Logical.
- Keep predictions available as an optional assistive layer.
- Let sound and haptic feedback be comfort toggles instead of defaulting both on.

Why: the comfort story is plausible but still under-measured, so the bundle should stay conservative until Branch 6 and Branch 8 evidence lands.

## Branch 3 Recommendation

Branch 3 is complete at the persona-research level.

- ERICK no longer needs generic accessibility-only messaging as the sole framing.
- The current settings and lessons already imply distinct bundles for reduced precision touch, low-vision scanning, one-handed use, controller-first use, and fatigue-oriented use.
- That means persona-specific setup guidance is ready to split into product work.

## Ready-To-Split Follow-Up

Recommended follow-up scope:

- persona-specific setup cards or presets in settings and onboarding
- persona-specific practice entry points for one-handed and controller-first users
- guidance copy that maps shipped settings to real task bundles instead of broad claims
- a smaller validation study that compares the recommended bundles against the current generic setup path