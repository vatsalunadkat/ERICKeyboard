# ERICK-157 - German Language Support

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
| **Dependencies** | Depends on ERICK-140 shared language profiles and benefits from ERICK-154 versioned prediction storage for future per-language learning |

---

## Objective

Add German support to ERICK across both dial modes with explicit coverage for umlauts, `ß`, capitalization-sensitive word patterns, and prediction behavior that does not collapse under long compounds.

---

## Why This Needs Separate Treatment

- German requires `ä`, `ö`, `ü`, and `ß`.
- German noun capitalization means suggestion quality cannot assume the same lowercase-only usage patterns as English.
- Long compounds make word completion and correction behavior more important than a small accent-only patch would suggest.

---

## Scope

### 1. Add Shared German Layout Resources

Ship logical-first German support for both dial modes with a discoverable path for umlauts and `ß`.

### 2. Add German Prediction Resources

The German profile should include:

- a German base dictionary
- capitalization-aware default suggestions where appropriate
- bigrams tuned for common German word order
- isolated learned prediction data for German

### 3. Validate Suggestion Behavior For Compounds

This does not require full morphological analysis in the first pass, but the ticket should validate that ERICK remains usable when German words are longer and more compound-heavy than typical English suggestions.

### 4. Update Help And Practice Surfaces

Explain how to enter umlauts and `ß`, and ensure at least one German-specific lesson or help example exists where language-aware examples are shown.

---

## Out Of Scope

- Full German UI localization for host-app screens
- A full compound-word segmenter in the first pass
- German efficiency layouts before logical-first support is stable

---

## Acceptance Criteria

- [ ] German is selectable without overwriting learned profiles for English or other languages
- [ ] German logical typing works in both 8-section and 6-section modes
- [ ] `ä`, `ö`, `ü`, and `ß` are enterable without regressing shipped utility actions
- [ ] German prediction uses German dictionary and bigram data
- [ ] At least one capitalization-sensitive suggestion case is covered in tests
- [ ] Shared tests cover German typing, prediction isolation, and long-word completion behavior
