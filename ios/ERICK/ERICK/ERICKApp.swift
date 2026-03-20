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

    var body: some Scene {
        WindowGroup {
            ContentView()
                .preferredColorScheme(
                    themeMode == "dark" ? .dark :
                    themeMode == "light" ? .light : nil
                )
        }
    }
}
