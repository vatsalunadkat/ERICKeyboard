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
        return .norwegianBokmal
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

func extensionEnglishLanguageDisplayName(for keyboardLanguage: String) -> String {
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

func bilingualExtensionLanguageDisplayName(for keyboardLanguage: String) -> String {
    let selfName = extensionLanguageSelfDisplayName(for: keyboardLanguage)
    let englishName = extensionEnglishLanguageDisplayName(for: keyboardLanguage)
    return selfName.caseInsensitiveCompare(englishName) == .orderedSame ? englishName : "\(selfName) (\(englishName))"
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
