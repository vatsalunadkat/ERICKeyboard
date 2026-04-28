# ERICK-156 - French Language Support

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
| **Dependencies** | Depends on ERICK-140 shared language-profile architecture and should reuse ERICK-154 profile versioning rather than creating a one-off French predictor path |

---

## Objective

Add French support to ERICK with language-aware handling for accented letters, cedilla, apostrophe-driven elision, and French punctuation expectations while keeping the shared keyboard logic authoritative.

---

## Why This Needs Separate Treatment

- French relies heavily on apostrophes and elision forms such as `l'`, `d'`, `j'`, and `qu'`.
- French needs broad accent coverage, including `é`, `è`, `ê`, `ë`, `à`, `â`, `î`, `ï`, `ô`, `ù`, `û`, `ü`, and `ç`.
- French prediction quality will feel weak if the profile does not model common short glued sequences and punctuation habits.
- French spacing around `:`, `;`, `?`, and `!` should at least be reviewed so ERICK does not hard-code English-only punctuation assumptions.

---

## Scope

### 1. Add Shared French Layout And Character Metadata

Define logical-first French support in both dial modes and ensure the accent path remains discoverable without altering the shipped utility wheel behavior.

### 2. Add French Prediction Resources

The French profile should include:

- a French base dictionary
- default suggestions that feel French immediately
- bigrams or phrase fragments that reflect common French syntax
- isolated learned prediction data for French

### 3. Review Apostrophe And Punctuation Behavior

This ticket should verify that French word-entry and suggestion acceptance still behave sensibly around:

- apostrophe-led forms
- contractions and elisions
- punctuation that differs from typical English defaults

### 4. Update Learning And Help Copy

French-specific typing guidance should explain how accented letters and apostrophe-heavy words are entered.

---

## Out Of Scope

- Full French UI localization for every app screen
- Automatic locale-specific punctuation insertion beyond what is explicitly validated
- French efficiency layouts before logical-first support is stable

---

## Acceptance Criteria

- [ ] French is selectable without overwriting learned profiles for other languages
- [ ] French logical typing works in both 8-section and 6-section modes
- [ ] French accent coverage and `ç` are enterable without breaking current utility actions
- [ ] French prediction uses French dictionary and bigram data
- [ ] Apostrophe-heavy common French words remain typable and predictable
- [ ] Shared tests cover French typing, prediction isolation, and at least one apostrophe-sensitive case
