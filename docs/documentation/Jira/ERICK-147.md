# ERICK-147 - Guided Onboarding & Practice Flows

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 13 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, onboarding, practice, android, ios, accessibility |
| **Dependencies** | Build on the older tutorial idea from ERICK-129, but target the shipped 6-section, one-handed, and controller flows validated on ERICK-139 and ERICK-141 |

---

## Objective

Add guided onboarding and reusable practice flows that teach new users how to type with ERICK across the modes that now matter most in production: 6-section, one-handed assisted input, and physical controllers.

---

## Why This Matters

- The keyboard now supports multiple dial modes, three input modes, left-handed routing, and controller typing, but new users still have to infer most of the model from static settings and experimentation.
- The current typing game is good for free practice, but it does not teach the mechanics of a first chord, assisted locking, or controller stick coordination.
- The existing broad onboarding backlog item predates the shipped 6-section geometry and current controller behavior, so it no longer describes the most important learning path.

---

## Current Architecture

### Host-App Entry Points
- Android host app entry points live under `android/app/src/main/java/com/vatoo/erick/`.
- iOS host app entry points live under `ios/ERICK/ERICK/`.

### Shared Behavior
- `android/shared/src/commonMain/kotlin/KeyboardLogic.kt` defines chord results, dial geometry, and right-dial single-swipe actions.
- `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` defines the behavior users need to learn for instant, confirm, and assisted modes.

### Practice Surface
- `ios/ERICK/ERICK/TypingGameView.swift` already provides quote practice but not structured instruction.
- The Android host app exposes setup and settings surfaces, but not progressive lessons.

---

## Proposed Changes

### 1. First-Run Quickstart

Add an optional first-run walkthrough that teaches:

- what each dial does
- how to type the first chord
- how right-dial single-swipe actions work
- how to skip or replay the tutorial later

### 2. Mode-Aware Lessons

Add short guided lessons for:

- 8-section basics
- 6-section basics with the rotated utility wheel
- one-handed assisted typing
- controller typing with both sticks

Each lesson should define a small success condition such as typing a target chord, triggering a specific utility swipe, or completing a short phrase.

### 3. Practice Paths

Create reusable practice modules instead of a single freeform mode:

- beginner drills for letter-group recognition
- utility-swipe drills for space, shift, backspace, enter, and symbols
- one-handed drills focused on assisted lock + right-side selection
- controller drills focused on dead-zone control and dual-stick timing
- quote practice as the advanced/freeform phase

### 4. Progress & Replay

Track whether onboarding has been completed and which lessons have been attempted so users can:

- resume later
- replay a specific lesson
- skip directly to practice once they know the system

### 5. Cross-Platform Consistency

Keep lesson content and terminology aligned across Android and iOS even if the presentation layer differs between Compose and SwiftUI.

---

## Validation

- Android host app builds after adding onboarding/practice entry points.
- iOS host app builds after adding the new lesson surfaces.
- Manual smoke tests confirm a fresh install can:
  - start the quickstart flow
  - skip it
  - replay it later
  - complete at least one 6-section lesson, one assisted lesson, and one controller lesson

---

## Acceptance Criteria

1. New users see an optional guided quickstart instead of being dropped directly into unexplained controls.
2. Users can replay onboarding or jump into targeted practice modules later.
3. The guided content covers 6-section typing, assisted one-handed input, and controller typing explicitly.
4. Quote practice remains available as a separate freeform mode.
5. Android and iOS terminology stay aligned for the same lessons and controls.
