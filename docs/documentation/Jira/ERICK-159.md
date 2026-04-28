# ERICK-159 - Scandinavian Language Pack (Norwegian Bokmal, Danish, Swedish)

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
| **Dependencies** | Depends on ERICK-140 shared language profiles and should reuse ERICK-154 profile versioning so the three languages do not share one blended learned predictor |

---

## Objective

Add a Scandinavian expansion pack covering Norwegian Bokmal, Danish, and Swedish, using one coordinated implementation ticket because the languages share a large amount of Latin-script structure while still requiring separate profiles and dictionaries.

---

## Why These Languages Belong Together

- All three can build on the same shared language-profile architecture without a new script or a new dial model.
- Their extra-character sets are closely related:
  - Norwegian Bokmal and Danish rely on `æ`, `ø`, and `å`
  - Swedish relies on `å`, `ä`, and `ö`
- The implementation work should share a large amount of layout, preview, settings, and test scaffolding even though prediction resources must remain distinct.

---

## Scope

### 1. Add Separate Shared Profiles

Ship three distinct language profiles:

- Norwegian Bokmal
- Danish
- Swedish

Each profile should have its own:

- display name and language code
- dictionary tiers
- default suggestions
- bigram data
- learned prediction storage

### 2. Reuse Shared Character And Discovery Patterns

Where practical, the extra-character entry path should be designed once and reused across the three profiles without changing the shipped utility wheel invariants.

### 3. Validate Similarity Without Blending

The ticket should explicitly avoid a shortcut where the three languages are treated as one combined predictor.

Shared scaffolding is fine. Shared learned history is not.

---

## Out Of Scope

- Nynorsk support in the first Norwegian pass
- Region-specific spelling variants beyond the first selected profile for each language
- Scandinavian efficiency layouts before logical-first support is stable

---

## Acceptance Criteria

- [ ] Norwegian Bokmal, Danish, and Swedish are selectable as distinct languages
- [ ] Each language keeps separate learned prediction data
- [ ] The required extra letters for each language are enterable without regressing shipped utility actions
- [ ] Logical typing works in both 8-section and 6-section modes for all three languages
- [ ] Prediction resources are separate per language even if layout scaffolding is shared
- [ ] Shared tests cover profile switching among the three languages and learned-profile isolation
