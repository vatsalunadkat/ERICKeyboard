//
//  ERICKApp.swift
//  ERICK
//
//  Created by Starship on 2026/3/2.
//

import SwiftUI

@main
struct ERICKApp: App {
    @AppStorage("theme_mode", store: UserDefaults(suiteName: "group.com.vatoo.erick") ?? .standard)
    private var themeMode: String = "system"
    @AppStorage("keyboard_language", store: UserDefaults(suiteName: "group.com.vatoo.erick") ?? .standard)
    private var keyboardLanguage: String = "english"

    var body: some Scene {
        WindowGroup {
            ContentView()
                .preferredColorScheme(
                    themeMode == "dark" ? .dark :
                    themeMode == "light" ? .light : nil
                )
                .environment(\.erickLanguageKey, keyboardLanguage)
                .environment(\.locale, Locale(identifier: erickLocaleIdentifier(for: keyboardLanguage)))
        }
    }
}
