# ERICK-144 - Split iOS Extension High-Risk Files

| Field | Value |
|---|---|
| **Type** | Tech Debt |
| **Priority** | High |
| **Story Points** | 8 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | ai-first, ios, maintainability, keyboard-extension |
| **Dependencies** | Preserve shipped 6-section mapping and extension behavior |

---

## Objective

Reduce the iOS extension edit surface by splitting the two largest extension files into focused view and controller units.

Status: In progress on `ERICK-141`. `SettingsView.swift` has already been reduced from 1156 lines to 443 by extracting `ColorPaletteComponents.swift`, `CustomPaletteEditorView.swift`, and `CustomLayoutViews.swift`. Remaining work: split `KeyboardViewController.swift` and validate the extension build on macOS.

---

## Evidence

- `ios/ERICK/ErickKeyBoard/SettingsView.swift` started at 1156 lines and is now 443 lines after the first extraction phase.
- `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift` is 1045 lines.
- ERICK-141 already flags both files as high-risk for lower-context AI edits.

---

## Scope

1. Extract palette and custom-layout UI out of `SettingsView.swift`.
2. Extract a dedicated view-model or state-holder layer out of `KeyboardViewController.swift`.
3. Keep runtime behavior unchanged.
4. Rebuild the shared framework and the `ERICK` Xcode project after the split.

---

## Acceptance Criteria

1. `SettingsView.swift` and `KeyboardViewController.swift` each shrink to a narrower single responsibility.
2. The extension still builds after refreshing `SharedKeyboard.xcframework`.
3. The shipped 6-section mapping and settings behavior remain unchanged.
