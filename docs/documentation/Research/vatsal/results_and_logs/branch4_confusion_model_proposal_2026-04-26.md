# Branch 4 Confusion Model Proposal

Date: 2026-04-26

## Why This Branch Can Start Now

The repo already has a shared controller normalization path in `android/shared/src/commonMain/kotlin/ControllerInputProcessor.kt` that exposes:

- raw stick values
- dead-zone-adjusted stick values
- magnitude
- resolved direction
- active versus inactive state

The Android diagnostics screen in `android/app/src/main/java/com/vatoo/erick/ControllerDiagnosticsActivity.kt` already renders those values locally and confirms that it is using the same shared normalization path as the keyboard state machine. That is enough to define the first privacy-safe confusion buckets without storing typed content.

## Proposed Confusion Categories

### Shared touch and controller buckets

| Bucket | What it means | Why it matters |
|---|---|---|
| Adjacent-direction slip | intended direction resolves to a one-step neighbor | most likely mis-hit family on both 6-section and 8-section geometry |
| Mirror-direction slip | intended direction resolves to the opposite side of the dial | higher-cost but high-impact miss, especially for strong diagonal travel |
| Dead-zone jitter | motion repeatedly crosses active/inactive threshold before a stable direction is formed | can turn one intended chord into a cancel or wrong commit |
| Overshoot / ring-cross feel | motion exits through one segment but lands in the next segment before commit | captures “too far” movement rather than neighbor ambiguity |

### Controller-specific buckets

| Bucket | Observable local signal |
|---|---|
| Snap-back reversal | last active direction differs from the release-adjacent direction after magnitude falls toward zero |
| Calibration-induced bias | confusion rate changes materially when dead zone or Y inversion changes |
| Assisted-lock mismatch | locked left direction and resolved right-stick direction form a high-error pair during assisted input |

## Privacy-Safe Collection Plan

Record only local aggregate buckets, not raw typed text and not raw stick traces.

Recommended local event schema:

| Field | Example | Keep? |
|---|---|---|
| device class | `touch` or `controller` | yes |
| dial mode | `6-section` or `8-section` | yes |
| input mode | `instant`, `confirm`, `assisted` | yes |
| expected bucket | `NE` | yes |
| resolved bucket | `N` | yes |
| confusion type | `adjacent-slip` | yes |
| dead-zone band | `0.15-0.20` | yes for controller |
| target text | actual typed string | no |
| raw axis trace | full motion series | no |

Start collection only in local debugging or practice surfaces. Do not export per-event logs. Keep only bucket counts and session totals.

## First Candidate Scoring Term

Let `C(p, q)` be the probability that chord position `p` is confused with position `q`.

Use a first analytic penalty before any logging exists:

`confusion_cost = λ * Σ_(a,b) C(pos(a), pos(b)) * (uni(a) + uni(b) + β * bi(a,b) + β * bi(b,a))`

Where:

- `pos(x)` is the chord position of character `x`
- `uni` and `bi` are the existing unigram and bigram weights
- `C(p, q)` starts from hand-built category weights rather than user logs
- `λ` controls how much confusion matters relative to physical effort
- `β` upweights high-frequency transitions that become costly when two common letters are easy to confuse

## Recommended Seed Matrix

Start with these relative weights before calibration:

| Pair type | Seed weight |
|---|---|
| same slot | `0.00` |
| one-step angular neighbor on one dial | `1.00` |
| one-step angular neighbor on both dials | `1.40` |
| opposite-side mirror on one dial | `0.45` |
| opposite-side mirror on both dials | `0.75` |
| inactive-to-active jitter near dead zone | `0.60` |
| controller snap-back reversal | `0.85` |

These weights are intentionally ordinal. The first job of local traces is to confirm whether the ordering is directionally right, not to claim final probabilities.

## Practical Recommendation

- Treat Branch 4 as ready for a local-only instrumentation spike, not as blocked on a full telemetry design.
- Start with controller diagnostics and practice drills because both already explain calibration and controlled input tasks to the user.
- Use aggregate confusion buckets to calibrate `C(p, q)` before attempting any optimizer integration.