//
//  SettingsView.swift
//  ERICK
//
//  Created by ERICK on 2026/3/9.
//

import SwiftUI

struct SettingsView: View {
    private static let appGroupDefaults = UserDefaults(suiteName: "group.com.vatoo.erick") ?? .standard
    
    @AppStorage("layout_type", store: SettingsView.appGroupDefaults) private var layoutType: String = "logical"
    @AppStorage("dark_theme", store: SettingsView.appGroupDefaults) private var darkTheme: Bool = false
    @AppStorage("colorblind_mode", store: SettingsView.appGroupDefaults) private var colorblindMode: Bool = false
    @AppStorage("left_handed_mode", store: SettingsView.appGroupDefaults) private var leftHandedMode: Bool = false
    
    @Environment(\.dismiss) var dismiss

    var body: some View {
        NavigationView {
            Form {
                // Layout Section
                Section(header: Text("Keyboard Layout")) {
                    Picker("Layout Type", selection: $layoutType) {
                        Text("Logical (A–Z)").tag("logical")
                        Text("Efficiency (Coming Soon)").tag("efficiency")
                    }
                    .pickerStyle(.inline)
                    // The Efficiency layout will be enabled in Sprint 3 according to Android implementation
                    .onChange(of: layoutType) { newValue in
                        if newValue == "efficiency" {
                            // Revert to logical as efficiency is not yet available
                            layoutType = "logical"
                        }
                    }
                }
                
                // Appearance Section
                Section(header: Text("Appearance")) {
                    Toggle("Dark Theme", isOn: $darkTheme)
                }

                // Accessibility Section
                Section(header: Text("Accessibility")) {
                    Toggle("Colorblind Mode", isOn: $colorblindMode)
                    Toggle("Left-Handed Mode", isOn: $leftHandedMode)
                }
                
                // Privacy & Security Section
                Section(header: Text("Privacy & Security")) {
                    VStack(alignment: .leading, spacing: 10) {
                        Text("🔒 Your privacy is our priority. ERICKeyboard:")
                            .font(.subheadline)
                            .fontWeight(.semibold)
                        
                        Text("✓ Does NOT collect any text you type")
                        Text("✓ Does NOT store passwords or personal data")
                        Text("✓ Does NOT transmit any data from your device")
                        Text("✓ Only stores your keyboard preferences locally")
                        Text("✓ Has no internet permissions")
                        Text("✓ Is 100% open source for full transparency")
                    }
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .padding(.vertical, 5)
                }
            }
            .navigationTitle("Keyboard Settings")
            .navigationBarItems(leading: Button("Back", action: {
                dismiss()
            }))
        }
    }
}

struct AppSettingsView_Previews: PreviewProvider {
    static var previews: some View {
        SettingsView()
    }
}
