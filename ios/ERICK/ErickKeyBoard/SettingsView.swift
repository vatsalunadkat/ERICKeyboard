//
//  SettingsView.swift
//  ErickKeyBoard
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
    
    // Action closure when the user wants to dismiss settings from Keyboard Extension
    var onClose: (() -> Void)? = nil

    var body: some View {
        VStack(spacing: 0) {
            // Header
            HStack {
                Button(action: {
                    onClose?()
                }) {
                    Image(systemName: "arrow.left")
                        .font(.title3)
                        .padding()
                }
                Text("Keyboard Settings")
                    .font(.headline)
                Spacer()
            }
            .background(Color(UIColor.systemGray6))
            
            // Content
            Form {
                // Layout Section
                Section(header: Text("Keyboard Layout")) {
                    Picker("Layout Type", selection: $layoutType) {
                        Text("Logical (A–Z)").tag("logical")
                        Text("Efficiency").tag("efficiency")
                    }
                    .pickerStyle(.inline)
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
                            .font(.caption)
                            .fontWeight(.semibold)
                        
                        Text("✓ Does NOT collect any text you type\n✓ Does NOT store passwords\n✓ Only stores preferences locally")
                            .font(.caption2)
                            .foregroundColor(.secondary)
                    }
                    .padding(.vertical, 5)
                }
            }
        }
    }
}

struct SettingsView_Previews: PreviewProvider {
    static var previews: some View {
        SettingsView()
    }
}
