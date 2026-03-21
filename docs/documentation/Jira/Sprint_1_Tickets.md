# ERICK — Sprint 1 Tickets

**Sprint**: SCRUM Sprint 1  
**Start Date**: February 23, 2026 (Monday)  
**End Date**: February 27, 2026 (Friday)  
**Project**: ERICK - Agile Methods  

**Sprint Goal**: MVP product delivered by Friday  
**Basic Mindset**: Well documented work for each group  

**Team**:
- **Group 1** — Research Team (keyboard/joystick library exploration)
- **Group 2** — Lead UI Designer + Documentation
- **Group 3** — Game Development / Jetpack Compose Design
- **Group 4** — System Integration (Android IME)
- **Product Owner** — Backlog management and sprint planning

---

## SCRUM-19 — Research: Keyboard and Joystick Libraries on Android

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Assignee** | Khair Muhammad, Taimoor Athar Malik |
| **Group** | Group 1 |
| **Labels** | research, android |
| **Dependencies** | None |

### Description

Find keyboard and joystick libraries on GitHub and test them on Android. The goal is to find open-source libraries that accept swipe/joystick input which can then be mapped to keyboard characters. Download and run these libraries, and test whether a particular angle can be mapped to a particular input key (e.g. input in -20 degrees to +20 degrees should type number 2).

### Deliverable

An evaluated list of candidate libraries with a working proof-of-concept or notes on how each can be adapted for the ERICK chord keyboard.

### Acceptance Criteria

- [ ] At least 2–3 open-source joystick/swipe libraries found and evaluated
- [ ] Libraries downloaded, run, and tested on an Android device
- [ ] Angle-to-key mapping tested with at least one candidate library
- [ ] Findings documented and shared with the team

---

## SCRUM-20 — UI Design: App Wireframes and Screen Mockups

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Assignee** | Angel, Irshad |
| **Group** | Group 2 |
| **Labels** | design, UI |
| **Dependencies** | Group 3 + Group 2 must sync early on visual style so SCRUM-27 assets match the design |

### Description

Design in Figma or other tools what the app should look like. The keyboard (2 joysticks and extra buttons) and the game screens (if there is time). Aim to create something good enough to publish on the Google Play Store.

### Deliverable

Figma (or equivalent) design files covering the keyboard screen and at least one game screen, with a consistent visual style suitable for production.

### Acceptance Criteria

- [ ] Keyboard screen mockup created showing the 2-joystick layout
- [ ] At least one game screen designed
- [ ] Visual style guide defined (colors, typography, spacing)
- [ ] Design files committed to or linked from the repository

---

## SCRUM-21 — Documentation: Architecture Diagrams and File Structure

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Assignee** | Angel, Irshad |
| **Group** | Group 2 |
| **Labels** | documentation |
| **Dependencies** | None |

### Description

Create and maintain project documentation including architectural diagrams that show how the system components connect, and a file structure overview for the codebase.

### Deliverable

Architecture diagram and file structure documentation committed to the repository.

### Acceptance Criteria

- [ ] Architecture diagram created showing major system components and their relationships
- [ ] File structure overview documented
- [ ] Documentation committed to the repository under `documentation/`

---

## SCRUM-22 — Jetpack Compose / XML: Keyboard Screen Implementation

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Assignee** | Adi, Nazgul |
| **Group** | Group 3 |
| **Labels** | android, UI, keyboard |
| **Dependencies** | Group 3 + Group 2 must sync early on visual style so SCRUM-27 assets match the design |

### Description

From the Figma designs (SCRUM-20), create the keyboard screen in Android Studio using XML layouts or Jetpack Compose. One team member focuses on the keyboard layout. The design must be dynamic — display properly on all common screen sizes.

### Deliverable

A working keyboard screen that displays the 2-joystick ERICK keyboard layout in Android Studio.

### Acceptance Criteria

- [ ] XML or Compose screen for the keyboard layout created in Android Studio
- [ ] Layout is dynamic and adapts to common screen sizes
- [ ] Design matches Figma mockups from SCRUM-20
- [ ] Keyboard screen renders without errors in the emulator

---

## SCRUM-26 — Jetpack Compose / XML: Game Screen Implementation

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Assignee** | Adi, Nazgul |
| **Group** | Group 3 |
| **Labels** | android, UI, game |
| **Dependencies** | Group 3 + Group 2 must sync early on visual style |

### Description

Create XML or Compose game screens in Android Studio. The screens represent the game area where keyboard typing exercises take place. The design must be dynamic and display properly on all common screen sizes.

### Deliverable

XML or Compose game screen(s) that can serve as the foundation for game development work in Sprint 2.

### Acceptance Criteria

- [ ] Screen(s) for the game area created in Android Studio
- [ ] Layout is dynamic and responsive across screen sizes
- [ ] Visual style is consistent with the keyboard screen (SCRUM-22)

---

## SCRUM-27 — Game Assets: Open-Source Asset Collection and First Prototype

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Assignee** | Adi, Nazgul |
| **Group** | Group 3 |
| **Labels** | game, assets |
| **Dependencies** | Must align with visual style from Group 2 (SCRUM-20) |

### Description

Build the first prototype gameplay/training flow and make it look real using open-source (CC0/MIT) assets. Explore Godot as a potential game engine and find resources or templates to prepare for full game development in Sprint 2.

**Standup update (Feb 26)**: New direction — explore Godot, find resources or templates to learn and prepare for game design. The team needs to figure out what kind of game best fits the ERICK use case — something that keeps the user engaged while practicing keyboard input.

**Product Owner guidance**: The game assets collected now are likely to be reusable in the future, especially small materials like menu buttons and interface buttons. Once the game type is confirmed, this document will be referenced for basic gameplay design through to complex function planning.

### Deliverable

A collection of CC0/open-source assets relevant to the intended game scenario and/or a Godot prototype scene.

### Acceptance Criteria

- [ ] Open-source game assets collected and documented
- [ ] Initial exploration of Godot engine completed
- [ ] Findings or prototype committed to the repository
- [ ] Assets are reusable for Sprint 2 game development

---

## SCRUM-23 — System Integration: InputMethodService Implementation

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Highest |
| **Assignee** | Xingxing, Valgot |
| **Group** | Group 4 |
| **Labels** | android, IME |
| **Dependencies** | None |

### Description

Implement the `InputMethodService` so that the Android system recognizes the ERICK app as a keyboard and can send characters to other apps such as Notepad or WhatsApp.

### Deliverable

A working Android keyboard service that the system can activate and that can inject characters into any text field in any app.

### Acceptance Criteria

- [ ] `InputMethodService` subclass implemented
- [ ] Android system recognizes ERICK as an available keyboard
- [ ] Characters can be injected into external apps (e.g. Notepad, WhatsApp)
- [ ] No crash on keyboard open or character injection

---

## SCRUM-25 — System Integration: AndroidManifest Keyboard Configuration

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Assignee** | Xingxing, Valgot |
| **Group** | Group 4 |
| **Labels** | android, IME, infrastructure |
| **Dependencies** | SCRUM-23 |

### Description

Configure `AndroidManifest.xml` to declare the Input Method Editor (IME) service correctly, ensuring all required intent filters, metadata, and permissions are set so the keyboard is selectable by the user in Android Settings → Language & Input.

### Deliverable

A correctly configured `AndroidManifest.xml` with the full IME service declaration.

### Acceptance Criteria

- [ ] `AndroidManifest.xml` declares the IME service with correct intent filters
- [ ] Keyboard is selectable in Android Settings → Language & Input
- [ ] No manifest warnings or build errors

---

## SCRUM-34 — Research: iOS Keyboard Integration via Xcode & Swift

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Assignee** | Valgot |
| **Group** | Group 4 |
| **Labels** | iOS, research |
| **Sprint** | Sprint 1 (added mid-sprint) |
| **Dependencies** | None |

### Description

Research how to build and pop up a custom keyboard on iOS using Xcode and Swift. This groundwork will inform the iOS keyboard extension implementation planned for Sprint 2.

### Deliverable

Research notes and/or a proof-of-concept Xcode project demonstrating how a custom iOS keyboard extension can be created and activated.

### Acceptance Criteria

- [ ] iOS keyboard extension mechanism (UIInputViewController / App Extension) researched and understood
- [ ] Key findings documented for the Sprint 2 iOS development team
- [ ] Proof-of-concept created or research notes committed to the repository

---

## SCRUM-35 — Showcases: Keyboard Usage Demonstration

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Low |
| **Assignee** | Vatsal (Product Owner) |
| **Labels** | onboarding, UX |
| **Sprint** | Sprint 1 (added mid-sprint) |
| **Dependencies** | Keyboard design must be complete |

### Description

After completing the keyboard design, build showcases to demonstrate to users how to use the ERICK chord keyboard. Reference: Finger Dance as a model for interactive keyboard learning.

### Deliverable

At least one showcase or demo flow that illustrates how to use the ERICK chord keyboard input system.

### Acceptance Criteria

- [ ] At least one showcase demo created
- [ ] Demo illustrates the chord input method clearly
- [ ] Demo is suitable for first-time users
