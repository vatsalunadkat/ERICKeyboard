# Branch 6 - Prediction, Disambiguation, And Adaptive Assistance

## Evidence Reviewed

- android/shared/src/commonMain/kotlin/WordPredictionEngine.kt
- android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt
- android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt
- docs/documentation/User_Guide.md
- docs/documentation/Research/vatsal/results_and_logs/branch7_prediction_aware_benchmark_2026-04-28.md

## Current Predictor Surface Inventory

| Predictor surface | What ships now | Research implication |
|---|---|---|
| Prefix completions | `getSuggestions()` prioritizes completions first and shows up to three suggestions. | This is the strongest live assistance path today. |
| Lightweight corrections | If fewer than three completions exist and the prefix has at least two characters, the engine fills remaining slots with edit-distance corrections. | ERICK already performs limited correction, but only as an explicit suggestion, not a silent rewrite. |
| Next-word predictions | Empty-buffer state switches to bigram suggestions or defaults. | The feature exists, but earlier benchmark evidence says it rarely helps on the current shortform packs. |
| Learned words and bigrams | Accepted suggestions and committed words update a local learned profile. | Prediction-first personalization already exists in the shared layer, even though the UI does not expose it directly. |
| Suggestion visibility policy | Suggestions appear only when both dials are at home and are hidden during active preview. | The current predictor problem is at least partly about timing and discoverability, not only ranking. |
| Acceptance policy | Suggestion acceptance is explicit tap-only and may add leading or trailing spaces around punctuation boundaries. | Trust is currently preserved through explicit acceptance, which is a good baseline to keep. |

## Predictor Interaction Scorecard

| Interaction type | Current strength | Current limitation | Trust question | Most useful metric |
|---|---|---|---|---|
| Prefix completion | Strongest current assistance path | Still depends on the user noticing the bar when both dials rest | Does the bar appear often enough to become a habit? | suggestion impressions versus taps, useful prefix depth |
| Correction suggestions | Present without silent autocorrect | Only lightly surfaced and mixed in with completions | Do users see a correction as help or as uncertainty? | correction tap rate after error, rejection rate |
| Next-word prediction | Technically present | Weak hit rate on current shortform benchmark families | Does showing weak next-word suggestions reduce confidence? | next-word tap rate, post-accept continuation speed |
| Learned profile | Local-only and privacy-preserving | No explicit user-dictionary or profile management UI | Will users trust learning more if they can inspect or control it? | learned-word reuse rate, learned-bigram hit rate |
| Default suggestions | Simple start-of-input fallback | Static defaults (`I`, `The`, `Hello`) are not domain-aware | Do generic defaults help or distract? | start-of-field tap rate, abandonment after ignored defaults |

## Trust-And-Acceptance Memo For Suggestions

### What the code already says

- ERICK preserves trust by never auto-accepting suggestions.
- The bar is intentionally suppressed while a chord preview is active, which avoids accidental taps during steering.
- Acceptance is explicit and the resulting text change is visible.
- Local learning is already built into the shared predictor and persisted by platform delegates.

### What the current evidence says

- The Branch 7 prediction-aware benchmark found roughly `5.8%` to `6.2%` savings, but no layout-ranking change.
- Prefix completion accounts for almost all of the modeled value.
- Next-word prediction barely matters on the current shortform benchmark family.
- Branch 2 already concluded that prediction should stay outside the mandatory first-session teaching path.

### Branch 6 conclusion

The next predictor problem is more about trust, visibility, and domain relevance than about raw ranking.

- Keep prediction as an explicit assistive layer.
- Do not send predictor work back into the optimizer loop yet.
- Focus the next split on when suggestions appear, how they are explained, and whether domain-specific defaults improve acceptance.

## Recommendation On Domain-Specific Assistance Versus Layout Personalization

Prediction-first assistance is the stronger next bet.

- The shared predictor already supports local word learning and learned bigrams.
- Branch 1 showed that layout replacement can carry major migration cost, especially in 8-section mode.
- The earlier prediction-aware benchmark showed measurable benefit without changing layout order.

That makes domain-specific or persona-specific assistance a safer next split than layout personalization.

### Immediate recommendation

- prioritize domain-aware default suggestions or learned-state evaluation before adaptive layouts
- keep abbreviation and disambiguation support in the future bucket until trust and visibility are better measured
- treat suggestion teaching and suggestion visibility as UX work first, not optimizer work

## Ready-To-Split Follow-Up

Recommended follow-up scope:

- suggestion trust and visibility study in the live keyboard UI
- domain-specific default-suggestion and bigram experiments
- lightweight user-dictionary or learned-profile controls
- explicit `No-Go for now` on optimizer-coupled prediction scoring and layout-personalization work