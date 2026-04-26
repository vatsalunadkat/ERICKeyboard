# Branch 5 Layout Variant Recommendation

Date: 2026-04-26

## Current Segment Set For Evaluation

Use these segments for benchmarking, not for immediate product branching:

| Segment | Why it matters now | Best current evaluation pack or surface |
|---|---|---|
| Touch-first | default phone usage remains the widest surface | `general-wordfreq-50k`, `messaging-shortform` |
| Controller-first | controller typing is a distinct product promise | `controller-tv-query`, `ControllerDiagnosticsActivity.kt` |
| Assisted one-handed | different lock-and-finish interaction pattern | assisted lessons and `InputMode.ASSISTED` flows |
| Cognitive / novice | learnability and visual clarity matter more than peak speed | quickstart, practice lessons, `Logical` layout guidance |
| Expert efficiency | pure speed and transition comfort matter most | full optimizer scorecards |

## Evidence From Current Product Surfaces

- The settings surface currently presents one built-in `Efficiency` option beside `Logical` and any user-created custom layouts.
- The help copy currently teaches a simple progression: `Logical` is easier to learn, `Efficiency` is faster later.
- The user guide also presents two built-in layouts, not a segment picker.
- Benefit and audience copy describes different user needs, but it does not imply that each audience already needs its own official Efficiency layout.
- Custom layouts remain an experimentation path in 8-section mode, which lowers the need to ship multiple official variants before the research case is strong.

## Product Complexity Cost Of Official Variants

| Cost area | Why it gets worse with multiple official Efficiency variants |
|---|---|
| Settings | `Layout` becomes a segment picker layered on top of dial mode, input mode, handedness, and custom layouts |
| Teaching | quickstart, practice lessons, help copy, and docs would need to explain which variant fits whom |
| Maintenance | every future optimizer rerun would need cross-segment regression checks instead of one primary Efficiency family |
| 6-section parity | custom layouts are currently framed as an 8-section path, so official 6-section variants would add more product-specific surface area than the current UI exposes |

## Recommendation

Treat Branch 5 as an evaluation branch, not a shipping branch, until earlier branches produce materially different winners for at least one segment.

Use this split rule before proposing an official segment-specific Efficiency layout:

1. A segment-specific winner must beat the global winner by at least `3%` on that segment's benchmark pack.
2. It must not lose by more than `1%` on the general pack.
3. The gain must survive Branch 3 utility-cost assumptions and any later Branch 4 confusion-aware reruns.
4. There must be a clear UI and lesson story for selecting the variant without making the default path harder to understand.

Until those conditions are met, keep one shipped Efficiency layout and use segment packs only as evaluation slices.