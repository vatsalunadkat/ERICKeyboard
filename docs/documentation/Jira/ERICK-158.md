# ERICK-158 - Italian Language Support

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, i18n, shared, android, ios, prediction, backlog-followup |
| **Dependencies** | Depends on ERICK-140 shared language-profile architecture and should reuse the same per-language profile storage path rather than adding platform-specific Italian behavior |

---

## Objective

Add Italian support to ERICK with logical-first typing in both dial modes, Italian dictionary and bigram resources, and a clean path for accented vowels and apostrophe-driven elision.

---

## Why This Ticket Can Stay Smaller

- Italian stays within a familiar Latin-script surface.
- The special-character surface is narrower than French or Portuguese.
- The core implementation should mainly be shared profile work, prediction resources, and learnability updates.

---

## Scope

### 1. Add Shared Italian Layout Resources

Support Italian in 8-section and 6-section logical layouts with a discoverable path for `à`, `è`, `é`, `ì`, `ò`, and `ù`.

### 2. Add Italian Prediction Resources

The Italian profile should include:

- Italian base dictionary tiers
- default suggestions that feel recognizably Italian
- bigrams for common articles, prepositions, and verb helpers
- isolated learned prediction data for Italian

### 3. Validate Apostrophe And Elision Cases

The ticket should confirm that common apostrophe-led forms remain easy to type and predict.

---

## Out Of Scope

- Full Italian app localization
- Italian efficiency layouts before logical-first support is stable

---

## Acceptance Criteria

- [ ] Italian is selectable without overwriting other learned language profiles
- [ ] Italian logical typing works in both 8-section and 6-section modes
- [ ] Italian accented vowels are enterable without regressing shipped utility actions
- [ ] Italian prediction uses Italian dictionary and bigram data
- [ ] Shared tests cover Italian typing, prediction isolation, and an apostrophe-sensitive case
