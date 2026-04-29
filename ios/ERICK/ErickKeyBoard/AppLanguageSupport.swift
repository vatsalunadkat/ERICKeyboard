import SharedKeyboard

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

func extensionLanguageSelfDisplayName(for keyboardLanguage: String) -> String {
    switch keyboardLanguage {
    case "spanish":
        return "Espanol"
    case "portuguese":
        return "Portugues"
    case "french":
        return "Francais"
    case "german":
        return "Deutsch"
    case "italian":
        return "Italiano"
    case "norwegian_bokmal":
        return "Norsk Bokmal"
    case "danish":
        return "Dansk"
    case "swedish":
        return "Svenska"
    case "finnish":
        return "Suomi"
    default:
        return "English"
    }
}
