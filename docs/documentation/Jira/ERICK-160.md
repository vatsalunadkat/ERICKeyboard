# ERICK-160 - Finnish Language Support

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 8 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, i18n, shared, android, ios, prediction, backlog-followup |
| **Dependencies** | Depends on ERICK-140 shared language-profile architecture and should build on ERICK-154 versioned prediction storage to keep Finnish learning isolated |

---

## Objective

Add Finnish support to ERICK with logical-first typing in both dial modes, reliable handling for `ä` and `ö`, and prediction behavior that remains usable with longer inflected forms and compound-heavy vocabulary.

---

## Why Finnish Deserves Its Own Ticket

- Finnish is not just another accent pass on top of English.
- Word forms grow quickly through inflection and compounding, which changes what a useful predictor looks like.
- The first pass should still stay pragmatic and shared-code-first, but it should not assume English-style short-word behavior.

---

## Scope

### 1. Add Shared Finnish Layout Resources

Ship Finnish logical layouts for both 8-section and 6-section modes with a clear path for `ä` and `ö`.

### 2. Add Finnish Prediction Resources

The Finnish profile should include:

- Finnish base dictionary tiers
- Finnish default suggestions
- bigrams that reflect common Finnish function-word behavior
- isolated learned prediction data for Finnish

### 3. Validate Longer Word And Compound Behavior

The first pass does not need a full Finnish morphology engine, but it should verify that:

- longer completions stay readable
- learned words meaningfully improve repeated Finnish inputs
- compound-heavy typing does not collapse prediction usefulness immediately

### 4. Update Help And Learning Surfaces

Where language-aware examples are shown, include Finnish-specific examples so the product does not look English-only after the profile ships.

---

## Out Of Scope

- Full Finnish UI localization
- A full morphology analyzer in the first pass
- Finnish efficiency layouts before logical-first support is stable

---

## Acceptance Criteria

- [ ] Finnish is selectable without overwriting learned profiles for other languages
- [ ] Finnish logical typing works in both 8-section and 6-section modes
- [ ] `ä` and `ö` are enterable without regressing shipped utility actions
- [ ] Finnish prediction uses Finnish dictionary and bigram data
- [ ] Shared tests cover Finnish typing, prediction isolation, and at least one longer-word completion case
