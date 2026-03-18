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
    @AppStorage("color_palette", store: SettingsView.appGroupDefaults) private var colorPalette: String = "okabe_ito"
    @AppStorage("left_handed_mode", store: SettingsView.appGroupDefaults) private var leftHandedMode: Bool = false
    
    @Environment(\.dismiss) var dismiss

    var body: some View {
        NavigationView {
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
                    Toggle("Enable Colorblind Mode", isOn: $colorblindMode)

                    if colorblindMode {
                        Text("Select the palette that works best for your type of color vision. Each option shows a preview of the 8 colors used on the keyboard.")
                            .font(.caption)
                            .foregroundColor(.secondary)

                        AppColorPaletteOption(
                            title: "Okabe-Ito (Universal)",
                            subtitle: "Recommended for all types of color vision deficiency",
                            palette: AppColorPaletteDefinitions.okabeIto,
                            selected: colorPalette == "okabe_ito",
                            onSelect: { colorPalette = "okabe_ito" }
                        )
                        AppColorPaletteOption(
                            title: "Deuteranopia (Green-blind)",
                            subtitle: "Optimized for green-blind users",
                            palette: AppColorPaletteDefinitions.deuteranopia,
                            selected: colorPalette == "deuteranopia",
                            onSelect: { colorPalette = "deuteranopia" }
                        )
                        AppColorPaletteOption(
                            title: "Protanopia (Red-blind)",
                            subtitle: "Optimized for red-blind users",
                            palette: AppColorPaletteDefinitions.protanopia,
                            selected: colorPalette == "protanopia",
                            onSelect: { colorPalette = "protanopia" }
                        )
                        AppColorPaletteOption(
                            title: "Tritanopia (Blue-blind)",
                            subtitle: "Optimized for blue-blind users",
                            palette: AppColorPaletteDefinitions.tritanopia,
                            selected: colorPalette == "tritanopia",
                            onSelect: { colorPalette = "tritanopia" }
                        )
                        AppColorPaletteOption(
                            title: "Pastel (Soft)",
                            subtitle: "Softer colors that are easier on the eyes",
                            palette: AppColorPaletteDefinitions.pastel,
                            selected: colorPalette == "pastel",
                            onSelect: { colorPalette = "pastel" }
                        )
                    }

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

// MARK: - Color Palette Definitions & UI Components (Main App)

private struct AppColorPaletteEntry {
    let name: String
    let hex: String
}

private struct AppColorPaletteDefinitions {
    static let okabeIto: [AppColorPaletteEntry] = [
        .init(name: "Orange", hex: "#E69F00"),
        .init(name: "Sky Blue", hex: "#56B4E9"),
        .init(name: "Bluish Green", hex: "#009E73"),
        .init(name: "Yellow", hex: "#F0E442"),
        .init(name: "Blue", hex: "#0072B2"),
        .init(name: "Vermillion", hex: "#D55E00"),
        .init(name: "Reddish Purple", hex: "#CC79A7"),
        .init(name: "Black", hex: "#000000")
    ]
    static let deuteranopia: [AppColorPaletteEntry] = [
        .init(name: "Blue", hex: "#0072B2"),
        .init(name: "Orange", hex: "#E69F00"),
        .init(name: "Light Blue", hex: "#56B4E9"),
        .init(name: "Yellow", hex: "#F0E442"),
        .init(name: "Dark Red", hex: "#CC3311"),
        .init(name: "Teal", hex: "#009988"),
        .init(name: "Pink", hex: "#EE7733"),
        .init(name: "Black", hex: "#000000")
    ]
    static let protanopia: [AppColorPaletteEntry] = [
        .init(name: "Blue", hex: "#0077BB"),
        .init(name: "Cyan", hex: "#33BBEE"),
        .init(name: "Teal", hex: "#009988"),
        .init(name: "Yellow", hex: "#EE7733"),
        .init(name: "Orange", hex: "#CC3311"),
        .init(name: "Magenta", hex: "#EE3377"),
        .init(name: "Grey", hex: "#BBBBBB"),
        .init(name: "Black", hex: "#000000")
    ]
    static let tritanopia: [AppColorPaletteEntry] = [
        .init(name: "Red", hex: "#CC3311"),
        .init(name: "Blue", hex: "#0077BB"),
        .init(name: "Yellow", hex: "#EECC66"),
        .init(name: "Cyan", hex: "#33BBEE"),
        .init(name: "Magenta", hex: "#EE3377"),
        .init(name: "Teal", hex: "#009988"),
        .init(name: "Grey", hex: "#BBBBBB"),
        .init(name: "Black", hex: "#000000")
    ]
    static let pastel: [AppColorPaletteEntry] = [
        .init(name: "Rose", hex: "#F4A6B0"),
        .init(name: "Peach", hex: "#F6C9A0"),
        .init(name: "Lemon", hex: "#FDE9A0"),
        .init(name: "Mint", hex: "#A8DFC0"),
        .init(name: "Sky", hex: "#A0C4E8"),
        .init(name: "Lavender", hex: "#C4A8D8"),
        .init(name: "Lilac", hex: "#D8A8C8"),
        .init(name: "Slate", hex: "#8B8B8B")
    ]
}

private struct AppColorPaletteOption: View {
    let title: String
    let subtitle: String
    let palette: [AppColorPaletteEntry]
    let selected: Bool
    let onSelect: () -> Void

    var body: some View {
        Button(action: onSelect) {
            VStack(alignment: .leading, spacing: 6) {
                HStack {
                    Image(systemName: selected ? "largecircle.fill.circle" : "circle")
                        .foregroundColor(selected ? .accentColor : .secondary)
                    VStack(alignment: .leading) {
                        Text(title).foregroundColor(.primary)
                        Text(subtitle)
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }

                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 6) {
                        ForEach(Array(palette.enumerated()), id: \.offset) { _, entry in
                            VStack(spacing: 2) {
                                RoundedRectangle(cornerRadius: 4)
                                    .fill(Color(hex: entry.hex))
                                    .frame(width: 32, height: 32)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 4)
                                            .stroke(Color.secondary.opacity(0.3), lineWidth: 1)
                                    )
                                Text(entry.name)
                                    .font(.system(size: 8))
                                    .foregroundColor(.secondary)
                            }
                        }
                    }
                    .padding(.leading, 28)
                }
            }
        }
    }
}

extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3:
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6:
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8:
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (1, 1, 1, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}
