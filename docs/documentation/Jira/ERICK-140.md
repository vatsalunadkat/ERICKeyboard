# ERICK-140 - Multi-Language Support (Spanish)

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 13 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, i18n, shared, android, ios, prediction |
| **Dependencies** | None |

---

## Description

Add multi-language support to ERICK, starting with **Spanish** as the first non-English language. This includes a Spanish chord layout, Spanish word prediction dictionary, Spanish autocorrect, and a language selector in settings. The architecture should be designed so that adding more languages later (French, German, etc.) requires only adding new dictionary and layout data, not structural code changes.

---

## Current Architecture

### Word Prediction
`WordPredictionEngine.kt` is a Trie-based engine with:
- Hardcoded English dictionary (~700 words across 4 frequency tiers)
- Bigram next-word prediction
- Levenshtein-distance autocorrect
- Default suggestions: `["I", "The", "Hello"]`
- Created via `WordPredictionEngine.createWithDefaultDictionary()` which calls `loadDefaultDictionary(engine)` containing inline English word lists

### Chord Layouts
`KeyboardLogic.kt` contains:
- Logical layout: A-Z alphabetical mapping (English alphabet)
- Efficiency layout: Frequency-optimized for English letter distribution
- Shifted maps: English punctuation and symbols
- All maps hardcoded as `Map<Direction, List<String>>` inline in the class

### State Machine
`KeyboardStateMachine.kt`:
- Creates predictor via `WordPredictionEngine.createWithDefaultDictionary()` at init
- No language awareness - always uses English

### Settings
- No language option exists on either platform
- Android: `PreferencesManager.kt` with DataStore
- iOS: `SettingsView.swift` with AppStorage

---

## Proposed Changes

### 1. Language Model Architecture

Create a `LanguageProfile` data class that encapsulates everything language-specific:

```kotlin
// New file: LanguageProfile.kt (shared module)
data class LanguageProfile(
    val languageCode: String,           // "en", "es"
    val displayName: String,            // "English", "Espanol"
    val alphabet: List<Char>,           // sorted character list for the language
    val logicalChordMap: Map<Direction, List<String>>,
    val logicalShiftedChordMap: Map<Direction, List<String>>,
    val efficiencyChordMap: Map<Direction, List<String>>,
    val efficiencyShiftedChordMap: Map<Direction, List<String>>,
    val dictionaryWords: Map<Int, List<String>>,  // tier -> word list
    val bigrams: List<Triple<String, String, Int>>, // word1, word2, frequency
    val defaultSuggestions: List<String>,
    val accentedCharacters: Map<Char, List<Char>>  // base char -> accented variants
)
```

### 2. Spanish Language Profile

#### Spanish Alphabet
The Spanish alphabet has 27 letters: the standard 26 English letters plus n with tilde. Accented vowels (a with accent, e with accent, i with accent, o with accent, u with accent, u with diaeresis) are also needed but are typically entered via accent modifiers rather than dedicated chord positions.

#### Spanish Logical Layout

Using the current 8-direction system (or 6-direction if ERICK-139 is implemented first):

**8-direction version (8x8 = 64 slots):**

```
         N    NE    E    SE    S    SW    W    NW
N   [    a     b     c     d     e     f    g     h  ]
NE  [    i     j     k     l     m     n    n~    o  ]
E   [    p     q     r     s     t     u    v     w  ]
SE  [    x     y     z     1     2     3    4     5  ]
S   [    6     7     8     9     0     '    /     ;  ]
SW  [    -     =     \     [     ]     `           ]
W   [    (accent modifier - see below)              ]
NW  [    (reserved)                                 ]
```

**6-direction version (6x6 = 36 slots):**

```
         N    NE    SE    S    SW    NW
N   [    a     b     c     d     e     f  ]
NE  [    g     h     i     j     k     l  ]
SE  [    m     n     n~    o     p     q  ]
S   [    r     s     t     u     v     w  ]
SW  [    x     y     z     1     2     3  ]
NW  [    4     5     6     7     8     9  ]
```

Note: 0 is lost in the 6x6 version. Options: put 0 on the shifted layer, or use the symbols mode from ERICK-139.

#### Accent Input Method

Spanish requires accented characters: a with accent, e with accent, i with accent, o with accent, u with accent, n with tilde, u with diaeresis.

**Option A - Accent modifier key**: Add an "accent" action to the single-swipe utility wheel. Pressing accent then typing a vowel produces the accented variant. Pressing accent twice then typing n produces n with tilde.

**Option B - Long-press**: Long-pressing a vowel chord shows a popup with accented variants. This is how standard keyboards handle it.

**Option C - Dedicated chord positions**: Give n with tilde its own chord (since it's a separate letter in Spanish). Accented vowels use the accent modifier approach since they are not separate letters.

**Recommendation**: Use Option C for n with tilde (dedicated position) + Option A for accented vowels (accent modifier key). This is the most efficient for Spanish typing.

#### Spanish Efficiency Layout

Must be optimized for Spanish letter frequency distribution:

| Letter | Spanish Frequency |
|---|---|
| e | 13.7% |
| a | 12.5% |
| o | 8.7% |
| s | 7.2% |
| n | 6.7% |
| r | 6.9% |
| i | 6.3% |
| l | 5.0% |
| d | 5.9% |
| t | 4.6% |
| c | 4.7% |
| u | 3.9% |

The top 6 letters (e, a, o, s, r, n) should be placed on the easiest same-direction chords, similar to how English places e, t, a on diagonals in the current Efficiency layout.

The optimizer (`erick_v5_vectorized.py`) needs to be run with Spanish corpus data instead of English.

### 3. Word Prediction - Spanish Dictionary

#### Dictionary Size
Target ~1000+ Spanish words across frequency tiers:

**Tier 1 (freq 1000) - Ultra-common:**
de, la, que, el, en, y, a, los, se, del, las, un, por, con, no, una, su, para, es, al, lo, como, mas, pero, sus, le, ya, o, este, si, porque, esta, son, entre, cuando, muy, sin, sobre, ser, tambien, me, hasta, hay, donde, quien, desde, todo, nos, durante, todos, uno, les, ni, contra, otros, ese, eso, ante, ellos, e, esto, mi, antes, algunos, que, unos, yo, otro, otras, otra, el, cual, poco, ella, nada, cada, mucho, fue, hacer, tiene, tiene, ha, dos, anos, tiempo, bien, parte, puede, gran, mismo, hoy, dia, vida

**Tier 2 (freq 500) - Common:**
gobierno, trabajo, mundo, pais, casa, hombre, vez, forma, lugar, solo, agua, ciudad, mejor, nuevo, numero, ejemplo, familia, grupo, problema, punto, derecho, estado, lado, modo, mano, tipo, centro, caso, mujer, razon, pueblo, noche, nombre, sociedad, accion, proceso, verdad, idea, campo, persona, politica, hora, cuenta, efecto, tierra, largo, historia, fuerza, juego, real, cambio, poder, paso, hecho, cuento

**Tier 3-4**: Extend with everyday vocabulary, technology terms, social media words.

#### Bigrams
Common Spanish word pairs:
- "de" -> "la", "los", "las", "un", "una", "el"
- "en" -> "el", "la", "los", "las", "un"
- "que" -> "el", "la", "los", "se", "no", "es"
- "a" -> "la", "los", "las", "un", "una"
- "el" -> "que", "de", "gobierno", "mundo"
- etc.

#### Default Suggestions
When buffer is empty: `["Hola", "Si", "No"]` (instead of English `["I", "The", "Hello"]`)

### 4. Autocorrect Adjustments

The current Levenshtein-distance autocorrect in `WordPredictionEngine.kt` works language-independently (it operates on character strings). However, Spanish considerations:

- Accented characters should be treated as close matches to their unaccented versions (e.g., "ano" should match "a-no" with accent)
- n with tilde should be treated as distinct from n (they are different letters in Spanish)
- The `getCorrections()` function may need a language-aware distance function or a pre-normalization step

### 5. Settings Changes

#### Language Selector

Add a "Language" setting to both platforms:

**Android** (`PreferencesManager.kt`):
```kotlin
private val LANGUAGE_KEY = stringPreferencesKey("language")
val language: Flow<String> = context.dataStore.data
    .map { preferences -> preferences[LANGUAGE_KEY] ?: "en" }
```

**Android** (`SettingsScreen.kt`):
- Add a "Language" section above "Layout Type"
- Show a dropdown/selector with available languages: English, Espanol
- Changing language immediately reloads the chord maps, dictionary, and default suggestions

**iOS** (`SettingsView.swift`):
```swift
@AppStorage("language", store: SettingsView.appGroupDefaults)
private var language: String = "en"
```

- Add a Picker in settings with available languages

### 6. State Machine Changes

**File**: `KeyboardStateMachine.kt`

- Accept a `languageCode` parameter (or observe it from preferences)
- On language change, rebuild the `WordPredictionEngine` with the new language's dictionary
- Pass the active `LanguageProfile` to `KeyboardLogic` for chord lookups
- Update default suggestions based on language

```kotlin
fun setLanguage(code: String) {
    val profile = LanguageProfileRegistry.getProfile(code)
    predictor = WordPredictionEngine.createWithDictionary(profile.dictionaryWords, profile.bigrams)
    currentProfile = profile
    delegate.onSuggestionsUpdated(profile.defaultSuggestions)
}
```

### 7. KeyboardLogic Changes

**File**: `KeyboardLogic.kt`

- `getChordResult()` should accept a `LanguageProfile` parameter (or the logic should look up the active profile)
- The hardcoded English maps remain as the default; Spanish maps are loaded from the Spanish profile
- `getDirectionFromXY()` does not change (directions are language-independent)

### 8. Language Profile Registry

Create a registry/factory that provides language profiles:

```kotlin
// New file: LanguageProfileRegistry.kt (shared module)
object LanguageProfileRegistry {
    private val profiles = mutableMapOf<String, LanguageProfile>()

    init {
        profiles["en"] = EnglishProfile.create()
        profiles["es"] = SpanishProfile.create()
    }

    fun getProfile(code: String): LanguageProfile = profiles[code] ?: profiles["en"]!!
    fun availableLanguages(): List<Pair<String, String>> = profiles.map { it.key to it.value.displayName }
}
```

### 9. Accent Modifier (Spanish-Specific)

Add an accent modifier action to `InputAction`:

```kotlin
enum class InputAction {
    // ... existing actions ...
    ACCENT_ACUTE,    // produces accented vowel on next input
    ACCENT_TILDE,    // produces n with tilde on next 'n' input
    ACCENT_DIAERESIS // produces u with diaeresis on next 'u' input
}
```

State machine handling:
- When `ACCENT_ACUTE` is triggered, set a flag
- On the next vowel chord, output the accented version instead of the plain vowel
- If the next input is not a vowel, output the accent mark as a standalone character and then process the chord normally
- Visual indicator on the keyboard (similar to Shift indicator) when accent mode is active

#### Accent Placement on Utility Wheel
- Replace one single-swipe direction with the accent modifier, or
- Add a long-press variant to an existing direction (e.g., long-press comma for accent)
- The accent key should only appear when the active language uses accented characters

### 10. iOS-Specific Considerations

- `JoystickView.swift` needs to read the active language profile for label rendering
- `KeyboardViewController.swift` needs to observe language preference from App Group defaults
- KMP shared module changes flow through the `.xcframework` build

### 11. Testing

- All existing English tests must continue passing
- Add Spanish-specific tests:
  - Chord mapping produces correct Spanish characters
  - n with tilde is accessible via its dedicated chord
  - Accent modifier + vowel produces accented character
  - Accent modifier + non-vowel cancels accent and types normally
  - Spanish word prediction returns Spanish words
  - Spanish autocorrect handles accented characters
  - Switching languages mid-session reloads dictionary and maps correctly
  - Switching back to English restores English behavior

---

## Files to Create

| File | Description |
|---|---|
| `android/shared/src/commonMain/kotlin/LanguageProfile.kt` | Language profile data class |
| `android/shared/src/commonMain/kotlin/LanguageProfileRegistry.kt` | Registry/factory for language profiles |
| `android/shared/src/commonMain/kotlin/EnglishProfile.kt` | English language profile (refactored from existing inline data) |
| `android/shared/src/commonMain/kotlin/SpanishProfile.kt` | Spanish language profile with dictionary, chord maps, bigrams |

## Files to Modify

| File | Changes |
|---|---|
| `android/shared/src/commonMain/kotlin/KeyboardContracts.kt` | Add `ACCENT_ACUTE`, `ACCENT_TILDE`, `ACCENT_DIAERESIS` to `InputAction` |
| `android/shared/src/commonMain/kotlin/KeyboardLogic.kt` | Accept language profile for chord lookups; move English maps to `EnglishProfile` |
| `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` | Language switching, accent modifier state, profile-aware prediction |
| `android/shared/src/commonMain/kotlin/WordPredictionEngine.kt` | New `createWithDictionary()` factory; accent-aware distance in autocorrect |
| `android/app/src/main/java/com/vatoo/erick/PreferencesManager.kt` | Add language preference |
| `android/app/src/main/java/com/vatoo/erick/SettingsScreen.kt` | Add language selector UI |
| `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` | Observe language preference, pass to state machine |
| `ios/ERICK/ERICK/SettingsView.swift` | Add language picker |
| `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift` | Observe language, pass to state machine |
| `ios/ERICK/ErickKeyBoard/JoystickView.swift` | Render labels from active language profile |
| `docs/documentation/User_Guide.md` | Add Spanish language section, accent input documentation |
| `docs/documentation/APP_CONTEXT.md` | Architecture update for multi-language support |

---

## Acceptance Criteria

- [ ] Language selector available in settings on both Android and iOS
- [ ] Two languages available: English (default) and Espanol
- [ ] Selecting Espanol switches chord layout to Spanish character arrangement
- [ ] Spanish Logical layout maps 27 Spanish letters (including n with tilde) + digits across the chord grid
- [ ] Spanish Efficiency layout is frequency-optimized for Spanish text
- [ ] n with tilde has a dedicated chord position in the Spanish layout
- [ ] Accent modifier key is accessible on the utility wheel when Spanish is active
- [ ] Accent modifier + vowel chord produces the accented vowel (a/e/i/o/u with accent)
- [ ] Accent modifier + non-vowel cancels the modifier and types normally
- [ ] Visual indicator shows when accent modifier is active (similar to Shift badge)
- [ ] Spanish word prediction dictionary loaded with 1000+ words across frequency tiers
- [ ] Spanish bigram predictions work (e.g., typing "de" suggests "la", "los", "las")
- [ ] Spanish default suggestions show "Hola", "Si", "No" when buffer is empty
- [ ] Autocorrect handles accented characters (e.g., "ano" suggests "a-no with accent")
- [ ] Switching language mid-typing session reloads dictionary and chord maps immediately
- [ ] Switching back to English restores full English behavior
- [ ] Language preference persists across app restarts
- [ ] Keyboard extension reads language preference from shared storage (DataStore / App Group)
- [ ] All existing English functionality unaffected (no regressions)
- [ ] Adding a future language requires only creating a new `LanguageProfile` implementation
- [ ] User Guide updated with Spanish language documentation

---

## Estimated Sub-Tasks

| Sub-Task | Points | Description |
|---|---|---|
| Shared: LanguageProfile architecture | 2 | Create data class, registry, refactor English into profile |
| Shared: Spanish profile + dictionary | 3 | Spanish chord maps, 1000+ word dictionary, bigrams |
| Shared: Accent modifier logic | 2 | InputAction additions, state machine accent state handling |
| Shared: Language-aware prediction | 2 | Factory method, accent-aware autocorrect |
| Android: Settings + preferences | 1 | Language selector UI, DataStore key |
| iOS: Settings + preferences | 1 | Language picker, AppStorage |
| Platform: UI label updates | 1 | JoystickView labels from active profile on both platforms |
| Documentation + tests | 1 | User Guide, unit tests for Spanish |

**Total**: ~13 story points
