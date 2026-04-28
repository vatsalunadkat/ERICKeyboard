# Branch 9 - Personalization, Multilingual, And Future Splits

## Evidence Reviewed

- docs/documentation/Jira/ERICK-140.md
- android/shared/src/commonMain/kotlin/WordPredictionEngine.kt
- android/shared/src/commonMain/kotlin/CustomLayout.kt
- docs/documentation/APP_CONTEXT.md
- docs/documentation/Research/vatsal/results_and_logs/branch2_onboarding_training_plan_2026-04-28.md
- docs/documentation/Research/vatsal/results_and_logs/branch3_accessibility_persona_matrix_2026-04-28.md
- docs/documentation/Research/vatsal/results_and_logs/branch6_predictor_interaction_scorecard_2026-04-28.md
- docs/documentation/Research/vatsal/results_and_logs/branch7_mixed_task_workflow_pack_2026-04-28.md
- docs/documentation/Research/vatsal/results_and_logs/branch8_adoption_proxy_plan_2026-04-28.md

## Future-Split Ranking Memo

| Rank | Candidate | Why it ranks here | Recommendation |
|---|---|---|---|
| `1` | Onboarding instrumentation and routed first lessons | Branches 2 and 8 both point to first-session friction and missing local measurement as the main blocker. | Now split as ERICK-152 for the lesson-path refresh. Any later local instrumentation should stay separate if it expands beyond lesson routing. |
| `2` | Persona-specific setup guidance and presets | Branch 3 found that ERICK already implies distinct bundles for touch precision, low vision, one-handed, and controller-first use. | Now split as ERICK-153. |
| `3` | Predictor trust, visibility, and domain-aware assistance | Branch 6 found prediction value already exists, but the open problem is domain fit and trust, not layout ranking. | Now split as ERICK-154 ahead of layout personalization. |
| `4` | Mixed-task benchmark execution | Branch 7 found likely bottlenecks in symbols, navigation, and repeated utility use. | Run after the first onboarding and predictor follow-ups are scoped. |
| `5` | Controller-first benchmark execution and onboarding | Branch 4 showed real divergence from touch, but controller work should reuse the same onboarding and trust instrumentation. | Keep as a focused follow-up, not the first split. |
| `6` | Multilingual foundation refresh (`ERICK-140`) | Real ticket already exists, but it depends on cleaner English predictor, onboarding, and profile boundaries. | Keep in backlog until the first product-experience tickets land. |
| `7` | Algorithmic layout personalization beyond manual custom layouts | Even after the direct 8-section preset refresh, custom layouts already provide user-directed layout control and the repo still lacks stronger trust and multilingual baselines. | Lowest near-term priority. |

## Dependency Map For Personalization And Multilingual Work

| Candidate direction | Can start now? | Key dependencies | Why |
|---|---|---|---|
| Personalized onboarding or presets | Yes | Branch 2 routing plan, Branch 3 persona bundles, Branch 8 proxy counters | This is the best fit for the current evidence. |
| Domain-specific prediction assistance | Yes, after minimal trust instrumentation | Branch 6 predictor work, Branch 8 acceptance proxies | The shared predictor already learns locally and can evolve without moving layouts. |
| Profession-specific benchmark packs | Yes, but later than mixed-task execution | Branch 7 workflow pack and a stable scoring protocol | The repo can support these once the task families are less ambiguous. |
| Multilingual foundation | Not first | ERICK-140 shared language profile work, per-language learned profiles, localized lessons | Valuable, but broader and riskier than the first split-ready tickets. |
| Algorithmic layout personalization | No | Stable English baselines, trust evidence, 6-section constraints, migration risk acceptance | Current evidence argues against making this an early product bet. |

## Recommendation On Prediction-First Versus Layout-First Personalization

Prediction-first is the right default.

- The shared predictor already supports learned words, learned bigrams, and local profile persistence.
- Manual custom layouts already give ERICK a user-controlled layout-personalization surface today.
- Even after the direct 8-section preset refresh, further adaptive layout work remains a high-trust-risk path compared with predictor and onboarding improvements.
- In 6-section mode, layout complexity is especially sensitive and custom layouts remain outside the shipped path.

That means ERICK should personalize assistance before it personalizes layout.

## Branch 9 Conclusion

Branch 9 should end as a ranking memo, not as its own product ticket.

- The first split-ready work has now been captured as ERICK-152, ERICK-153, and ERICK-154.
- Multilingual work remains valid in ERICK-140, but it should still follow the new evidence on onboarding, trust, and English-side predictor behavior instead of jumping the queue.
- Algorithmic layout personalization should stay last because the repo already has manual custom layouts and the remaining trust and baseline risk is still high.