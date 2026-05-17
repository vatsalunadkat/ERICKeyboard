# ERICK-161 - Emoji Keyboard
| Field | Value |
|---|---|
| **Status** | Completed |
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 21 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, emoji, android, ios, shared, ui, accessibility |
| **Dependencies** | None. Builds on the shipped 6-section utility wheel (`ERICK-139`) and the AI-first hardening file split (`ERICK-141`); does not modify either. |
---
## Description
Add an emoji keyboard panel to ERICK on both Android and iOS that mirrors the **functionality** and **emoji repertoire** of Google Gboard's emoji UI without copying any of Google's proprietary UI assets, code, or content surfaces.
The user enters the emoji panel by tapping a new `emoji` button placed on the **left** of the existing preview bar. The current settings (gear) button stays on the **right** unchanged. The shift / caps / symbols mode indicator currently rendered in the preview bar's left slot is **relocated to a small floating badge centered just below the preview bar**, overlaid on the empty visual space between the two dials.
**Total keyboard height does not change in any mode.** The emoji panel reuses the existing dial area; the preview bar stays in place. This applies to both 6-section and 8-section dial modes.
> Brand and content note: Gboard is a Google product. Its emoji repertoire, ordering, and category grouping come from the Unicode standard and CLDR (`emoji-test.txt`, `cldr-json`), which any keyboard may consume. Google's icons, animations, layouts, sticker packs, and GIF networks are proprietary and **out of scope** for this ticket. "Same as Gboard" in this ticket means **same Unicode emoji set, same Unicode/CLDR-defined categories and ordering, same standard interactions (tap to commit, long-press for skin tone), and same recently-used behavior pattern**, rendered with the host OS's native emoji font.
This ticket adds a new top-level **`Emoticons`** category in the panel for ASCII-style faces such as `:)`, `:-(`, `xD`, `<3`, plus widely used kaomoji like `^_^`, `T_T`, and `¯\_(ツ)_/¯`.

## Implementation Update

Implemented on Android, iOS, and the shared module as of 2026-05-17.

- Shared work is complete: `KeyboardMode.EMOJI`, `InputAction.TOGGLE_EMOJI`, `KeyboardStateMachine.toggleEmojiPanel()`, suggestion suppression in emoji mode, and the vendored shared `emoji_data.json` payload are all checked in with focused shared tests.
- Android and iOS both ship custom `EmojiPanelView` implementations backed by the same shared Unicode/CLDR catalog and the same curated `Emoticons` dataset. Android intentionally uses the shared-catalog path instead of the earlier Jetpack emoji-picker proposal so both platforms keep the same ordering, tab set, recents behavior, and tone-picker model.
- The release landed as `v2.2.0`, and the public release surfaces now carry a dedicated `v2.2.0` entry instead of overwriting the earlier `v2.0.0` multilingual release history.
- Android post-implementation follow-up fixed the long-press tone picker so it no longer triggers a transient keyboard relayout and tightened the emoji-panel container styling so the category strip stays visually separate from the scrolling grid.
- Local validation completed in this workspace: `cd android && .\gradlew.bat :shared:testAndroidHostTest`, `cd android && .\gradlew.bat assembleDebug`, and `cd android && .\gradlew.bat assembleSharedKeyboardXCFramework` all passed.
- Remaining external validation: build the iOS app and keyboard extension on Apple hardware against the refreshed `SharedKeyboard.xcframework`.

---
## Goals
1. Add a Gboard-equivalent emoji panel as a new keyboard mode on both platforms.
2. Add an `Emoticons` category for ASCII faces and common kaomoji.
3. Relocate the existing shift / caps / symbols indicator without growing the keyboard.
4. Keep behavior changes shared-module-first; let each platform handle only its UI rendering.
5. Persist a per-platform recently-used list local to the device.
---
## Non-Goals (Out Of Scope)
- Keyword search bar inside the emoji panel.
- Stickers, GIFs, animated emoji, or any networked content.
- Custom emoji art assets. The OS font renders all standard Unicode emoji.
- Custom layout integration (custom layouts remain disabled in 6-section mode and unaffected here).
- Multi-skin-tone variants for couple and family ZWJ sequences. We ship single-modifier skin tone only in the first pass.
- A separate "frequently used" list. `Recent` is sufficient for the first pass.
---
## Historical Pre-Implementation Snapshot
### Preview Bar (Android)
- Layout: `android/app/src/main/res/layout/keyboard_simple.xml` defines `live_preview_container` as a 40dp `FrameLayout` containing the shift indicator (`shift_indicator`, start gravity), the preview capsule (`live_preview_capsule`, center gravity), the suggestion bar (`suggestion_bar`, match-parent center), and the settings button (`btn_settings`, end gravity).
- Wiring: `MyInputMethodService.onCreateInputView()` inflates the layout, binds the views, and registers the settings click listener that opens `SettingsActivity`.
- Mode indicator text is updated by `MyInputMethodService.updateShiftIndicator()` from `onModeChanged()`.
### Preview Bar (iOS)
- Layout: `ios/ERICK/ErickKeyBoard/KeyboardContainerView.swift` defines a `ZStack` whose top `HStack` is a 40pt-tall row with three sections: a 36pt mode glyph slot on the left, a centered preview-or-suggestion area, and a 36pt gear button on the right.
- Mode indicator glyphs (`↑`, `↑↑`, `#`, `#↑`) live in the left slot of that `HStack` and are driven by `viewModel.keyboardMode`.
### Keyboard Mode
- `android/shared/src/commonMain/kotlin/KeyboardContracts.kt` defines `enum class KeyboardMode { NORMAL, SHIFTED, CAPS_LOCKED, SYMBOLS, SYMBOLS_SHIFTED }`.
- `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` owns `currentMode` (private setter) and `preSymbolsMode` so symbols toggle restores the prior mode.
- `KeyboardActionDelegate.onModeChanged(mode)` is the only platform-side mode notification.
### Input Actions
- `KeyboardContracts.kt` defines `enum class InputAction { SPACE, ENTER, BACKSPACE, DELETE_FORWARD, DELETE_WORD, TOGGLE_SHIFT, TOGGLE_CAPS, TOGGLE_SYMBOLS, MOVE_HOME, MOVE_END, DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT, PAGE_UP, PAGE_DOWN, TAB }`.
- There is no globe / next-IME button in either platform's keyboard view. `shouldOfferSwitchingToNextInputMethod` and `needsInputModeSwitchKey` are not used.
### Symbols Toggle
- 6-section right-only swipe `N` triggers `InputAction.TOGGLE_SYMBOLS` via `KeyboardLogic.getSingleSwipeResult6()`.
- 8-section mode does not have a symbols toggle on the dial; symbols mode is reachable only in 6-section mode today.
---
## Original Implementation Plan
### 1. Top Bar Layout Change (Both Platforms)
Replace the current preview bar contents with three slots from left to right:
[ emoji button (36) ][ preview-or-suggestions (flex) ][ gear button (36) ]

- The settings (gear) button stays on the right exactly as today.
- The shift / caps / symbols / symbols-shifted indicator moves OUT of the preview bar.
- The emoji button takes the left 36dp/pt slot. Its glyph is `😀` in `NORMAL`/`SHIFTED`/`CAPS_LOCKED`/`SYMBOLS`/`SYMBOLS_SHIFTED`. In `EMOJI` mode the same button shows `ABC` and acts as the return-to-previous-mode action.
- Bar height stays 40dp/pt.
### 2. Floating Shift / Caps Badge (Both Platforms)
A small pill rendered as an overlay centered horizontally below the preview bar, anchored in the empty visual space between the two dials.
- Visible only when `currentMode in { SHIFTED, CAPS_LOCKED, SYMBOLS, SYMBOLS_SHIFTED }`.
- Hidden in `NORMAL` and `EMOJI`.
- Shape: rounded pill, ~28dp/pt tall, ~36-44dp/pt wide depending on glyph.
- Glyphs and colors carry over unchanged: `↑` (shifted, theme foreground), `↑↑` (caps, `#D32F2F`), `#` (symbols, `#FF6F00` Android / `#FF9800` iOS), `#↑` (symbols-shifted, same orange).
- Implementation: Android adds the badge as a sibling overlay inside a `FrameLayout` that wraps the existing preview row + joystick row, anchored `top|center_horizontal` with a top margin equal to the preview row height. iOS adds it as a `ZStack` overlay aligned `.top` with `.padding(.top, 44)`.
- The badge does not consume vertical space and does not change keyboard height.
### 3. New `EMOJI` Keyboard Mode (Shared)
Add `EMOJI` to `KeyboardMode`:
enum class KeyboardMode { NORMAL, SHIFTED, CAPS_LOCKED, SYMBOLS, SYMBOLS_SHIFTED, EMOJI }

`KeyboardStateMachine` adds a `preEmojiMode` field mirroring `preSymbolsMode`, and a single new public method:
fun toggleEmojiPanel()

Behavior:
- If `currentMode != EMOJI`, store `preEmojiMode = currentMode` and set `currentMode = EMOJI`.
- If `currentMode == EMOJI`, set `currentMode = preEmojiMode` (defaults to `NORMAL`).
- `onModeChanged(EMOJI)` is fired so platforms can swap UI.
- While `currentMode == EMOJI`:
  - Dial inputs are ignored (touch and controller). `handleTouch` early-returns. The state machine consumes the events without committing anything.
  - Suggestions are paused. `updateSuggestions()` is a no-op while in EMOJI mode (matches existing symbols behavior).
  - Word buffer is **not** cleared. Returning to `preEmojiMode` resumes the prior word context.
- `setDialSectionMode()` does not auto-exit EMOJI mode. Section change is allowed but invisible until the user returns from EMOJI.
### 4. New `TOGGLE_EMOJI` Input Action
Add to `InputAction`:
TOGGLE_EMOJI

This is wired purely as a platform UI button; it is **not** assigned to any single-swipe direction in either dial mode. No regression to the 6-section utility wheel (`NE` Shift, `SE` Space, `S` Period, `SW` Enter, `NW` Backspace, `N` Symbols) or the 8-section single-swipe map.
`KeyboardActionDelegate` is unchanged. Platforms call `stateMachine.toggleEmojiPanel()` directly when the emoji or `ABC` button is tapped, which is consistent with how settings is handled today.
### 5. Emoji Panel UI (Both Platforms)
When `currentMode == EMOJI`, the dial area is hidden and replaced by the emoji panel. The preview bar stays. Total keyboard height is unchanged.
Panel structure top to bottom:
[ Recent | Smileys | People | Animals | Food | Travel | Activities | Objects | Symbols | Flags | Emoticons ] [ scrollable grid (system-rendered emoji) ] [ ABC ........................................................................................... ⌫ ]

- **Tab strip**: horizontally scrollable, 11 tabs in fixed order. Tabs use Unicode-defined groups for consistency with `emoji-test.txt`. The new `Emoticons` tab is appended last.
- **Grid**: vertical scroll, 8 columns on phones at default density, ~36dp/pt cell size. The grid renders text emoji directly through the system font.
- **Bottom bar**: fixed-height ~36dp/pt. `ABC` returns to the previous mode (calls `toggleEmojiPanel()`); `⌫` sends `InputAction.BACKSPACE` and reuses the existing accelerating-backspace handling.
Long-press behavior on emoji that have skin-tone variants:
- A horizontal popover appears above the cell with 6 options: yellow (default) plus the 5 Fitzpatrick modifier sequences `1F3FB`–`1F3FF`.
- Selecting a tone commits the modified emoji and remembers the chosen tone for that base emoji until the keyboard view is destroyed (in-memory only for the first pass; persistence is a follow-up).
### 6. Emoticons Category
A curated list of widely used ASCII smileys and kaomoji that commit as plain text. Vendored as a single shared resource list so both platforms render the same content.
Initial set (final list to be locked during implementation):
ASCII smileys: :) :-) :D :-D ;) ;-) :( :-( D: :P :-P :p :* :-* :| :-| :O :-O xD XD <3 </3 :3 :') :/ :S 8) B) =) =D =P :^)

Kaomoji: ^^ ^~ ^.^ T_T T-T >< -- o_O O_o (^^) (T_T) (><) (o_o) (^o^) (^▽^) (◕‿◕) (¬‿¬) (｡◕‿◕｡) (✿◠‿◠) ¯_(ツ)_/¯ (╯°□°)╯︵┻━┻ ʕ•ᴥ•ʔ (づ｡◕‿‿◕｡)づ ( ͡° ͜ʖ ͡°)

Tap commits the literal string via `delegate.commitText(...)`. No skin-tone popover for emoticons.
### 7. Recently Used
- Recents tab shows up to **32** entries, most-recent-first.
- Tapping any emoji in any tab moves it to the front of the recents list.
- Recents merge emoji and emoticons in a single recency-ordered list.
- Persistence:
  - Android: a new `recent_emojis` string preference in `PreferencesManager` (DataStore). Stored as a JSON array of strings.
  - iOS: `recent_emojis` key in App Group `UserDefaults` (`group.com.vatoo.erick`). Stored as a JSON array of strings.
- Recents survive keyboard re-creation and host-app restart.
- Recents do not sync across devices (no cloud sync today).
### 8. Emoji Data Source
The shared module vendors a small JSON resource describing categories, ordering, and skin-tone variants, derived from Unicode `emoji-test.txt` and CLDR. The same JSON is consumed by both platforms.
- File: `android/shared/src/commonMain/resources/emoji_data.json`.
- Schema: array of categories `{ id, displayKey, items: [{ codepoints, baseGlyph, hasSkinTone, toneVariants[] }] }`.
- Display labels are looked up via `ErickAppTranslations` so localized headings (`Smileys`, `People`, etc.) follow the existing translation pipeline.
- The `Emoticons` category is a separate JSON section because it carries plain-text strings rather than Unicode codepoints.
### 9. Android Implementation
- `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` reacts to `onModeChanged(EMOJI)` by hiding the joystick row and showing a new `EmojiPanelView`. The settings click handler is unchanged.
- The new emoji button on the preview bar's left side is added to `keyboard_simple.xml` and wired in `onCreateInputView()` to call `stateMachine.toggleEmojiPanel()`.
- `EmojiPanelView` is a small Kotlin `LinearLayout` wrapper that hosts `androidx.emoji2:emoji2-emojipicker:1.6.0`'s `EmojiPickerView` for the standard Unicode categories plus a sibling `RecyclerView` for the new `Emoticons` tab. The wrapper switches between the two based on the active tab.
- Recents are fed into `EmojiPickerView` via a custom `RecentEmojiProvider` backed by the new DataStore key.
- The floating shift / caps badge is a new `TextView` child of a wrapping `FrameLayout`, anchored top-center with a top margin equal to the preview row height.
- The settings (gear) button is unmodified.
### 10. iOS Implementation
- `ios/ERICK/ErickKeyBoard/KeyboardContainerView.swift` reacts to `viewModel.keyboardMode == .emoji` by hiding the dial `GeometryReader` block and showing a new `EmojiPanelView`.
- The emoji button on the preview bar's left side is added to the top `HStack` and calls a new closure on the container that ultimately invokes `stateMachine.toggleEmojiPanel()` on the controller.
- `EmojiPanelView` is a SwiftUI view containing a horizontal `ScrollView` of category tabs, a `LazyVGrid` of cells, and a bottom `HStack` for `ABC` and `⌫`.
- Cells render emoji as `Text(...)` using the Apple Color Emoji system font. Skin-tone popover uses a SwiftUI `Menu` or a custom long-press overlay.
- Recents use App Group `UserDefaults` and a small `RecentEmojisStore` helper.
- The floating shift / caps badge is a `ZStack` overlay aligned `.top` with a top padding equal to the preview row height.
### 11. Localization
- New translation keys added to `ErickAppTranslations`:
  - `emoji_tab_recent`
  - `emoji_tab_smileys`
  - `emoji_tab_people`
  - `emoji_tab_animals`
  - `emoji_tab_food`
  - `emoji_tab_travel`
  - `emoji_tab_activities`
  - `emoji_tab_objects`
  - `emoji_tab_symbols`
  - `emoji_tab_flags`
  - `emoji_tab_emoticons`
  - `emoji_button_open`
  - `emoji_button_back_abc`
- English fallback is supplied for all keys; non-English profiles inherit the fallback unless they choose to override.
### 12. Custom Layouts
- The emoji button is part of the keyboard chrome, not a chord or single-swipe binding. Custom layouts cannot bind to or override emoji-related actions in this ticket.
- Custom layouts remain disabled in 6-section mode (existing invariant from ERICK-139).
### 13. Controller Behavior
- A connected game controller in EMOJI mode does not move the dials. Stick input is consumed without committing.
- Controller buttons that were not previously assigned to single-swipe actions in this ticket are unchanged.
- A future ticket may map a controller button to `TOGGLE_EMOJI`. Not in scope here.
---
## Original Planned Files
| File | Changes |
|---|---|
| `android/shared/src/commonMain/kotlin/KeyboardContracts.kt` | Add `EMOJI` to `KeyboardMode`, add `TOGGLE_EMOJI` to `InputAction` |
| `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` | Add `preEmojiMode`, `toggleEmojiPanel()`, ignore dial input and pause suggestions while `EMOJI`, restore prior mode on exit |
| `android/shared/src/commonMain/kotlin/ErickAppTranslations.kt` | Add 13 new translation keys for tab labels and button labels |
| `android/shared/src/commonMain/resources/emoji_data.json` | New vendored Unicode/CLDR-derived emoji + emoticons dataset |
| `android/shared/src/commonTest/kotlin/KeyboardStateMachineTest.kt` | Add `togglingEmojiPanelStoresAndRestoresPreviousMode`, `dialInputIsIgnoredInEmojiMode`, `suggestionsAreSuppressedInEmojiMode` |
| `android/app/src/main/res/layout/keyboard_simple.xml` | Add emoji button to preview row left slot, wrap content in a `FrameLayout` so the floating badge can be overlaid |
| `android/app/src/main/res/drawable/ic_emoji.xml` | New emoji button icon (or reuse a system smiley vector) |
| `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` | Wire emoji button, react to `onModeChanged(EMOJI)`, show/hide `EmojiPanelView`, update floating shift/caps badge from `updateShiftIndicator()` |
| `android/app/src/main/java/com/vatoo/erick/EmojiPanelView.kt` | New view hosting `EmojiPickerView` plus the `Emoticons` tab and recents wiring |
| `android/app/src/main/java/com/vatoo/erick/EmoticonsAdapter.kt` | New `RecyclerView.Adapter` for the emoticons grid |
| `android/app/src/main/java/com/vatoo/erick/RecentEmojisProviderImpl.kt` | New `RecentEmojiProvider` implementation backed by DataStore |
| `android/app/src/main/java/com/vatoo/erick/PreferencesManager.kt` | Add `recent_emojis` preference (JSON-encoded list) |
| `android/app/build.gradle.kts` | Add `androidx.emoji2:emoji2-emojipicker:1.6.0` dependency |
| `android/gradle/libs.versions.toml` | Add Jetpack emoji-picker version reference |
| `ios/ERICK/ErickKeyBoard/KeyboardViewModel.swift` | Surface a `keyboardMode == .emoji` branch and a `recentEmojis` array |
| `ios/ERICK/ErickKeyBoard/KeyboardContainerView.swift` | Add emoji button on preview bar left slot, swap dial area for `EmojiPanelView` when in EMOJI mode, add floating shift/caps overlay |
| `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift` | Bridge button taps to `stateMachine.toggleEmojiPanel()`, persist recents, handle `onModeChanged(.emoji)` |
| `ios/ERICK/ErickKeyBoard/EmojiPanelView.swift` | New SwiftUI view: tab strip + `LazyVGrid` + bottom bar |
| `ios/ERICK/ErickKeyBoard/EmojiCellView.swift` | New SwiftUI view for a single emoji cell with long-press skin-tone popover |
| `ios/ERICK/ErickKeyBoard/EmojiCatalog.swift` | Loads `emoji_data.json` from the shared framework, exposes categories |
| `ios/ERICK/ErickKeyBoard/RecentEmojisStore.swift` | App Group `UserDefaults`-backed recents store |
| `docs/documentation/User_Guide.md` | Add an `Emoji Keyboard` section: how to open, categories, skin tones, emoticons, recents, returning via `ABC` |
| `docs/documentation/joystick_wireframe.drawio` | Update preview-bar callouts: emoji button on left, gear on right, floating shift/caps badge below |
| `APP_CONTEXT.md` | Document `KeyboardMode.EMOJI`, `TOGGLE_EMOJI`, the new `EmojiPanelView` on each platform, and the floating shift/caps badge |
| `docs/documentation/APP_CONTEXT.md` | Mirror the root `APP_CONTEXT.md` change |
| `CHANGELOG.md` | Add a release entry under the next version describing the emoji keyboard, the new emoticons category, and the relocated shift/caps indicator |
| `README.md` | One sentence in the Features list mentioning the emoji keyboard with skin tones, recents, and the emoticons category |
---
## Acceptance Criteria
- [ ] A new `KeyboardMode.EMOJI` value exists in `KeyboardContracts.kt` and is reported through `onModeChanged`.
- [ ] A new `InputAction.TOGGLE_EMOJI` value exists in `KeyboardContracts.kt`. It is **not** assigned to any single-swipe direction.
- [ ] `KeyboardStateMachine.toggleEmojiPanel()` enters EMOJI mode from any non-EMOJI mode, storing the prior mode in `preEmojiMode`.
- [ ] Calling `toggleEmojiPanel()` while in EMOJI mode restores `preEmojiMode` (defaults to `NORMAL`).
- [ ] Dial input (touch and controller) is ignored while `currentMode == EMOJI`. The word buffer is not cleared.
- [ ] Suggestions are suppressed while `currentMode == EMOJI` and resume on exit.
- [ ] The 6-section utility wheel mapping (`NE` Shift, `SE` Space, `S` Period, `SW` Enter, `NW` Backspace, `N` Symbols) is unchanged.
- [ ] The 8-section single-swipe map is unchanged.
- [ ] The Android preview bar shows the emoji button on the left, preview/suggestions in the middle, and the gear on the right. Total bar height is 40dp.
- [ ] The iOS preview bar shows the emoji button on the left, preview/suggestions in the middle, and the gear on the right. Total bar height is 40pt.
- [ ] The floating shift / caps / symbols / symbols-shifted badge appears centered below the preview bar between the dials, and only when the corresponding mode is active. It is hidden in NORMAL and EMOJI.
- [ ] Total keyboard height is identical to today in NORMAL, SHIFTED, CAPS_LOCKED, SYMBOLS, SYMBOLS_SHIFTED, and EMOJI modes.
- [ ] Tapping the emoji button on the preview bar opens the emoji panel. The button glyph changes to `ABC` while in EMOJI mode and tapping it returns to the previous mode.
- [ ] The emoji panel shows 11 tabs in this order: `Recent`, `Smileys & Emotion`, `People & Body`, `Animals & Nature`, `Food & Drink`, `Travel & Places`, `Activities`, `Objects`, `Symbols`, `Flags`, `Emoticons`.
- [ ] The standard Unicode categories follow `emoji-test.txt` ordering as of the vendored data version.
- [ ] Tapping any emoji or emoticon commits its text and updates the recents tab so it appears at the front of `Recent` on the next render.
- [ ] Long-press on a skin-tonable emoji shows a 6-option popover (default + 5 Fitzpatrick tones). Selecting a tone commits the modified codepoint sequence.
- [ ] The bottom bar of the emoji panel has a working `ABC` button and a working `⌫` button. `⌫` reuses the existing accelerating backspace.
- [ ] The `Emoticons` tab shows the curated ASCII / kaomoji set and tapping any entry commits the literal text.
- [ ] Recents persist across keyboard re-creation and host-app restart on both platforms.
- [ ] Recents are capped at 32 entries.
- [ ] Custom layouts are unaffected. They remain disabled in 6-section mode.
- [ ] Shared tests cover: enter/exit EMOJI mode, dial input ignored, suggestions suppressed, prior mode restoration including from CAPS_LOCKED and SYMBOLS.
- [ ] Android shared tests pass: `cd android && .\gradlew.bat :shared:testAndroidHostTest`.
- [ ] Android debug build passes: `cd android && .\gradlew.bat assembleDebug`.
- [ ] Android XCFramework rebuild passes: `cd android && .\gradlew.bat assembleSharedKeyboardXCFramework`.
- [ ] On Apple hardware, the iOS app and extension build clean against the refreshed `SharedKeyboard.xcframework`.
- [ ] `User_Guide.md`, `APP_CONTEXT.md` (root + mirrored copy), `joystick_wireframe.drawio`, `CHANGELOG.md`, and `README.md` are updated as specified.
---
## Original Estimated Sub-Tasks
| Sub-Task | Points | Description |
|---|---|---|
| Shared: `KeyboardMode.EMOJI` + `InputAction.TOGGLE_EMOJI` + `toggleEmojiPanel()` | 2 | Enum updates, state machine transition with `preEmojiMode`, dial-input gate, suggestion gate |
| Shared: emoji + emoticons data resource | 2 | Vendor `emoji_data.json` from Unicode/CLDR, define schema and loader, hand-curate emoticons list |
| Shared: tests for EMOJI mode transitions | 1 | New cases in `KeyboardStateMachineTest.kt` |
| Shared: translation keys | 1 | Add 13 keys to `ErickAppTranslations` with English fallback |
| Android: preview bar redesign | 1 | Add emoji button to `keyboard_simple.xml`, wrap root for badge overlay, wire click |
| Android: floating shift/caps badge | 1 | New overlay TextView, drive from `updateShiftIndicator()` |
| Android: emoji panel + Jetpack picker integration | 4 | `EmojiPanelView`, hosting `EmojiPickerView`, custom recents provider, emoticons tab adapter, panel show/hide |
| Android: recents persistence | 1 | DataStore preference + JSON serialization |
| iOS: preview bar redesign | 1 | Add emoji button to top `HStack`, wire callback |
| iOS: floating shift/caps badge | 1 | `ZStack` overlay, drive from `keyboardMode` |
| iOS: emoji panel SwiftUI grid | 4 | `EmojiPanelView`, `EmojiCellView`, tab strip, skin-tone popover, bottom bar |
| iOS: recents persistence | 1 | `RecentEmojisStore` backed by App Group `UserDefaults` |
| Documentation, diagrams, changelog | 1 | `User_Guide.md`, `APP_CONTEXT.md` (root + mirror), `joystick_wireframe.drawio`, `CHANGELOG.md`, `README.md` |
**Total**: ~21 story points
---
## Historical Risks And Open Questions
- **Android picker lock-in risk resolved.** The shipped implementation uses the same shared-catalog-backed custom panel approach on Android and iOS, so the earlier Jetpack `EmojiPickerView` lock-in concern no longer applies.
- **iOS extension memory.** Apple does not document a fixed limit. The vendored emoji JSON should be small (low hundreds of KB). Avoid loading raster atlases. Profile a release build on device.
- **Skin-tone state across sessions.** First pass keeps the chosen tone in memory only. A follow-up ticket should persist per-emoji tone choice if usage data justifies it.
- **Localization of category labels.** English fallback is shipped now; non-English profiles inherit until each language profile chooses to override.
- **Emoji rendering parity.** Android system emoji and Apple Color Emoji are visually different by design. We do not bundle a custom emoji font in this ticket.
- **Vendored data version.** The exact Unicode emoji version is fixed at the time the `emoji_data.json` is generated. Keeping it current is a small periodic maintenance task.