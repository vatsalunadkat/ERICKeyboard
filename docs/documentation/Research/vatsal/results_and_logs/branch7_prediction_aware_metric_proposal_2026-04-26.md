# Branch 7 Prediction-Aware Metric Proposal

Date: 2026-04-26

## Shipped Prediction Behavior That Matters To Layout Research

The shared prediction path in `KeyboardStateMachine.kt` makes two different kinds of savings:

1. Prefix completion while `wordBuffer` is non-empty via `predictor.getSuggestions(prefix)`.
2. Next-word prediction when `wordBuffer` is empty via `predictor.getNextWordSuggestions(lastCompletedWord)`.

Suggestion acceptance is not a generic text replace. `acceptSuggestion(...)` already handles:

- deleting the current prefix
- optional leading space insertion in next-word mode
- optional trailing space after replacing a partial word
- learning boosts for accepted suggestions
- learned bigram updates for the accepted word sequence

That means Branch 7 should model prediction as a shipped interaction contract, not as vague “autocomplete magic.”

## Recommended Evaluation Layers

### 1. Raw entry baseline

Score the word or phrase with no prediction acceptance.

### 2. Prefix-completion path

For each target word, find the shortest typed prefix length `k` where the target appears in the top three suggestions. Then score:

`completion_cost(word) = cost(prefix[0:k]) + tap_cost`

This captures what prediction really saves: later letters after a useful prefix becomes visible.

### 3. Next-word path

For a word pair `(prev, next)`, if `next` appears in the top three next-word suggestions after `prev`, score:

`next_word_cost(prev, next) = boundary_cost + tap_cost`

This represents the shipped “suggestions at rest” behavior after a completed word.

## First Combined Metric

Use post-hoc benchmarking first:

`prediction_adjusted_cost = min(raw_entry_cost, completion_cost, next_word_cost_if_available)`

Run this as a comparison harness over the benchmark packs before integrating it into the optimizer objective. The goal is to learn how much prediction compresses later-letter cost and whether that changes layout ranking enough to justify coupling layout and prediction.

## Why Post-Hoc First Is Safer

- The predictor already has learned words and learned bigrams, so integrated optimization would otherwise depend on a moving personalization state.
- `acceptSuggestion(...)` includes spacing and punctuation semantics that are easier to benchmark after layout scoring than to approximate inside the optimizer.
- The current branch still lacks empirical acceptance rates, so any integrated objective would be mostly assumption-driven.

## Recommended First Measurements

| Measurement | Why it matters |
|---|---|
| shortest useful prefix depth | tells us how many letters a layout must still optimize before completion can take over |
| top-3 next-word hit rate | measures how often bigram predictions can replace full word entry |
| tap-substitution savings | compares one suggestion tap against the number of avoided chord entries |
| punctuation-aware acceptance rate | checks how often prediction remains useful around realistic sentence boundaries |

## Recommendation

Keep Branch 7 shipping-adjacent, but treat it as a post-hoc evaluation branch first. Only consider optimizer integration if prediction-aware benchmarking actually changes layout ranking on the benchmark packs.