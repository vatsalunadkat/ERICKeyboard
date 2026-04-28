# ERICK-154 - Predictor Trust, Suggestion Clarity, And Domain Assistance

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 8 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-77 Kotlin Shared Core |
| **Labels** | feature, prediction, shared, android, ios, trust, research-followup |
| **Dependencies** | Build on ERICK-148 learned prediction persistence and the ERICK-151 Branch 6 findings about trust, visibility, and domain fit |

---

## Objective

Make ERICK's prediction system easier to trust and more useful in real typing by clarifying what suggestions mean, where they come from, and how domain-specific vocabulary can be added without moving layouts around.

---

## Why This Ticket Exists

- The shared predictor already learns locally, but the product does not explain its behavior very clearly.
- ERICK-151 Branch 6 found that the next predictor win is not a new optimizer objective. It is better trust, visibility, and domain fit.
- Domain-aware help is lower-risk and more immediately useful than more automatic layout changes.

---

## Current Surfaces To Build On

### Shared
- `android/shared/src/commonMain/kotlin/WordPredictionEngine.kt`
- `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt`
- `android/shared/src/commonMain/kotlin/KeyboardContracts.kt`

### Platform UI
- Android suggestion surfaces in `MyInputMethodService.kt`
- Help and learning copy on Android and iOS

---

## Proposed Scope

### 1. Clarify Suggestion Types

Users should be able to tell whether ERICK is showing:

- a completion of the current word
- a correction candidate
- a next-word suggestion

This can be done through lightweight labeling, ordering, or visual treatment, but it must stay readable in the current keyboard-height budget.

### 2. Improve Prediction Explainability

Add short help or quickstart copy that explains:

- how learned suggestions are created
- when a suggestion replaces the current word versus appends a next word
- that learning stays local to the device

### 3. Add Opt-In Domain Assistance

Introduce a small first pass of optional domain packs or vocabulary bundles that improve suggestions for specific contexts without touching the layout itself.

Examples:

- conversational texting
- productivity / office writing
- accessibility / assistive communication
- controller / gaming terminology

This should be opt-in and local-first.

### 4. Keep Learned Profiles Compatible With Future Language Work

Prediction profile storage should be reviewed for basic versioning and future compatibility so ERICK-140 does not have to undo this ticket later.

That does not mean shipping multilingual prediction here. It means avoiding another dead-end serialization format.

---

## Guardrails

- No optimizer-coupled prediction scoring
- No silent correction policy changes that conflict with the Branch 5 `No-Go`
- No automatic layout changes or hidden personalization

---

## Acceptance Criteria

- [ ] Users can distinguish completion, correction, and next-word suggestion behavior clearly enough to build trust
- [ ] Help or quickstart copy explains how ERICK prediction works in plain language
- [ ] At least one opt-in domain assistance path exists without requiring a network service
- [ ] Learned prediction data remains local and compatible with future multi-language profile work
- [ ] Shared predictor behavior remains authoritative across Android and iOS
- [ ] The ticket does not introduce silent correction or automatic layout mutation