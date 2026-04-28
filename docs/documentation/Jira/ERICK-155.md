# ERICK-155 - Portuguese Language Support

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
| **Dependencies** | Depends on ERICK-140 shared language-profile architecture and should build on the versioned prediction-profile work introduced by ERICK-154 |

---

## Objective

Add a first Portuguese profile to ERICK in both 8-section and 6-section modes, with clear handling for Portuguese diacritics, cedilla, and prediction data that stays isolated from English and Spanish.

---

## Why This Is Its Own Ticket

- Portuguese cannot be treated as Spanish with a few extra letters.
- The ticket must explicitly choose an initial target variant, such as Brazilian Portuguese or European Portuguese, instead of silently mixing them.
- Portuguese needs support for `ã`, `õ`, `á`, `à`, `â`, `é`, `ê`, `í`, `ó`, `ô`, `ú`, and `ç`.
- Prediction quality depends heavily on getting Portuguese function words, verb forms, and common contractions into the base profile.

---

## Scope

### 1. Lock The First Portuguese Variant

Before layout and dictionary work starts, choose the first supported Portuguese target and document it clearly:

- Brazilian Portuguese (`pt-BR`)
- European Portuguese (`pt-PT`)
- or a narrowly defined shared core if the corpus and punctuation rules can justify it

The choice should be explicit in settings, tests, and documentation.

### 2. Add Shared Portuguese Layout Resources

Ship logical-first Portuguese support across both dial modes:

- 8-section logical Portuguese map
- 6-section logical Portuguese map
- Portuguese preview labels where the active language affects visible output
- diacritic entry that preserves the shipped 6-section utility wheel

### 3. Add Portuguese Prediction Resources

The Portuguese profile should include:

- a Portuguese base dictionary tier set
- Portuguese default suggestions
- Portuguese bigrams and common short-form word sequences
- isolated learned words and learned bigrams for Portuguese

### 4. Update Discovery And Validation

The user must be able to discover how to type the Portuguese-specific characters that are not part of plain English.

Validation should cover touch and controller typing in both 8-section and 6-section modes.

---

## Out Of Scope

- Full parity between `pt-BR` and `pt-PT` in the first pass if only one variant ships initially
- Portuguese-specific efficiency layouts before logical-first support is stable
- Full translation of host-app marketing copy or website pages

---

## Acceptance Criteria

- [ ] The first supported Portuguese target is explicitly documented
- [ ] Portuguese is selectable without overwriting English or Spanish learned prediction data
- [ ] Portuguese logical typing works in both 8-section and 6-section modes
- [ ] `ã`, `õ`, accented vowels, and `ç` are enterable without regressing the shipped utility mapping
- [ ] Portuguese prediction uses Portuguese dictionary and bigram data
- [ ] Help or learning surfaces explain the Portuguese-specific character path clearly enough to discover
- [ ] Shared tests cover Portuguese profile selection, typing, and prediction isolation
