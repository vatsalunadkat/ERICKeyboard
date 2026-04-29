import SwiftUI
import SharedKeyboard

let erickAppGroupDefaults = UserDefaults(suiteName: "group.com.vatoo.erick") ?? .standard

private struct ErickLanguageKey: EnvironmentKey {
    static let defaultValue = "english"
}

extension EnvironmentValues {
    var erickLanguageKey: String {
        get { self[ErickLanguageKey.self] }
        set { self[ErickLanguageKey.self] = newValue }
    }
}

func erickLocaleIdentifier(for keyboardLanguage: String) -> String {
    switch keyboardLanguage {
    case "spanish":
        return "es"
    case "portuguese":
        return "pt"
    case "french":
        return "fr"
    case "german":
        return "de"
    case "italian":
        return "it"
    case "norwegian_bokmal":
        return "nb"
    case "danish":
        return "da"
    case "swedish":
        return "sv"
    case "finnish":
        return "fi"
    default:
        return "en"
    }
}

func sharedKeyboardLanguage(for keyboardLanguage: String) -> KeyboardLanguage {
    switch keyboardLanguage {
    case "spanish":
        return .spanish
    case "portuguese":
        return .portuguese
    case "french":
        return .french
    case "german":
        return .german
    case "italian":
        return .italian
    case "norwegian_bokmal":
        return .norwegian_bokmal
    case "danish":
        return .danish
    case "swedish":
        return .swedish
    case "finnish":
        return .finnish
    default:
        return .english
    }
}

func erickText(_ english: String, languageKey: String) -> String {
    ErickAppTranslations.shared.text(language: sharedKeyboardLanguage(for: languageKey), english: english)
}

func recoverableEnglishTitle(for keyboardLanguage: String, english: String) -> String {
    let localized = erickText(english, languageKey: keyboardLanguage)
    return keyboardLanguage == "english" || localized.caseInsensitiveCompare(english) == .orderedSame
        ? localized
        : "\(localized) (\(english))"
}

func englishLanguageDisplayName(for keyboardLanguage: String) -> String {
    switch keyboardLanguage {
    case "spanish":
        return "Spanish"
    case "portuguese":
        return "Portuguese"
    case "french":
        return "French"
    case "german":
        return "German"
    case "italian":
        return "Italian"
    case "norwegian_bokmal":
        return "Norwegian Bokmal"
    case "danish":
        return "Danish"
    case "swedish":
        return "Swedish"
    case "finnish":
        return "Finnish"
    default:
        return "English"
    }
}