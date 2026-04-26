# Branch 6 Hybrid Objective Proposal

Date: 2026-04-26

## Why Learnability Can Be Approximated Analytically

ERICK already exposes teaching structure in product surfaces instead of hiding it behind generic onboarding:

- `KeyboardLogic.kt` distinguishes logical chord order from the visual 6-section preview order.
- Quickstart explicitly teaches dials, utility swipes, input modes, and controller typing as separate concepts.
- Practice lessons already introduce content in a staged sequence: letters, then numbers, then punctuation or symbols.

That is enough to define first-pass learnability proxies without claiming that the app already has retention or user-study data.

## Proposed Learnability Proxies

| Proxy | What it measures | Why it is grounded in ERICK |
|---|---|---|
| Row coherence | how internally consistent each left-dial row feels as a visible group | users learn rows through preview bars and staged lessons |
| Preview predictability | how well a row reads in the visual preview order, especially in 6-section mode | the app already rotates 6-section previews intentionally |
| Lesson introduction span | how many distinct rows, utilities, and modes a learner must touch before completing early drills | practice lessons already encode a teaching sequence |
| Utility anchor stability | whether the taught single-swipe actions stay fixed and memorable across exercises | quickstart and utility drills rely on stable anchors |

## First Hybrid Objective

Use a score of the form:

`hybrid_score = efficiency_score + α * row_dispersion + β * preview_jump_penalty + γ * lesson_span_penalty`

Where:

- `efficiency_score` is the current optimizer objective
- `row_dispersion` penalizes rows whose characters lack obvious internal structure such as runs, shared type, or simple mnemonic grouping
- `preview_jump_penalty` penalizes layouts whose visible preview order creates large mental jumps between adjacent displayed symbols
- `lesson_span_penalty` penalizes layouts that require many row or mode introductions before the first guided lessons can be completed comfortably

## Suggested Seed Measurements

### Row dispersion

Count within-row discontinuities:

- letter to digit transitions
- letter to punctuation transitions
- large alphabetic jumps when a row is intended to be easy to scan

### Preview jump penalty

For 6-section mode, evaluate rows in the visual order `NE, SE, S, SW, NW, N`, not only the logical index order. A row that looks coherent in logical order but scrambled in preview order should not score as highly for learnability.

### Lesson span penalty

Use the quickstart and first guided lessons as anchors:

- `6-Section Basics`
- `Utility Swipes`
- `Assisted One-Handed`
- `Controller Drill`

Count how many distinct row groups, utilities, and layer changes a learner must understand before those drills become easy rather than merely possible.

## Recommendation Threshold

Treat a hybrid candidate as interesting only if:

1. its efficiency score is within `3%` of the best pure-efficiency layout
2. its learnability proxies improve by at least `15%`
3. the lesson sequence becomes simpler to explain without adding a new special case to quickstart or practice

Otherwise keep Branch 6 exploratory and leave the shipped path focused on pure-efficiency plus the earlier shipping-adjacent branches.