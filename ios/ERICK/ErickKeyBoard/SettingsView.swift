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
    @AppStorage("color_palette", store: SettingsView.appGroupDefaults) private var colorPalette: String = "okabe_ito"
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
                    Toggle("Enable Colorblind Mode", isOn: $colorblindMode)

                    if colorblindMode {
                        Text("Select the palette that works best for your type of color vision. Each option shows a preview of the 8 colors used on the keyboard.")
                            .font(.caption)
                            .foregroundColor(.secondary)

                        ColorPaletteOption(
                            title: "Okabe-Ito (Universal)",
                            subtitle: "Recommended for all types of color vision deficiency",
                            palette: ColorPaletteDefinitions.okabeIto,
                            selected: colorPalette == "okabe_ito",
                            onSelect: { colorPalette = "okabe_ito" }
                        )
                        ColorPaletteOption(
                            title: "Deuteranopia (Green-blind)",
                            subtitle: "Optimized for green-blind users",
                            palette: ColorPaletteDefinitions.deuteranopia,
                            selected: colorPalette == "deuteranopia",
                            onSelect: { colorPalette = "deuteranopia" }
                        )
                        ColorPaletteOption(
                            title: "Protanopia (Red-blind)",
                            subtitle: "Optimized for red-blind users",
                            palette: ColorPaletteDefinitions.protanopia,
                            selected: colorPalette == "protanopia",
                            onSelect: { colorPalette = "protanopia" }
                        )
                        ColorPaletteOption(
                            title: "Tritanopia (Blue-blind)",
                            subtitle: "Optimized for blue-blind users",
                            palette: ColorPaletteDefinitions.tritanopia,
                            selected: colorPalette == "tritanopia",
                            onSelect: { colorPalette = "tritanopia" }
                        )
                        ColorPaletteOption(
                            title: "Pastel (Soft)",
                            subtitle: "Softer colors that are easier on the eyes",
                            palette: ColorPaletteDefinitions.pastel,
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

// MARK: - Color Palette Definitions & UI Components

struct ColorPaletteEntry {
    let name: String
    let hex: String
}

struct ColorPaletteDefinitions {
    static let defaultPalette: [ColorPaletteEntry] = [
        .init(name: "Red", hex: "#E60012"),
        .init(name: "Orange", hex: "#F39800"),
        .init(name: "Yellow", hex: "#FFF100"),
        .init(name: "Green", hex: "#009944"),
        .init(name: "Blue", hex: "#0068B7"),
        .init(name: "Indigo", hex: "#1D2088"),
        .init(name: "Violet", hex: "#920783"),
        .init(name: "Black", hex: "#000000")
    ]

    static let okabeIto: [ColorPaletteEntry] = [
        .init(name: "Orange", hex: "#E69F00"),
        .init(name: "Sky Blue", hex: "#56B4E9"),
        .init(name: "Bluish Green", hex: "#009E73"),
        .init(name: "Yellow", hex: "#F0E442"),
        .init(name: "Blue", hex: "#0072B2"),
        .init(name: "Vermillion", hex: "#D55E00"),
        .init(name: "Reddish Purple", hex: "#CC79A7"),
        .init(name: "Black", hex: "#000000")
    ]

    static let deuteranopia: [ColorPaletteEntry] = [
        .init(name: "Blue", hex: "#0072B2"),
        .init(name: "Orange", hex: "#E69F00"),
        .init(name: "Light Blue", hex: "#56B4E9"),
        .init(name: "Yellow", hex: "#F0E442"),
        .init(name: "Dark Red", hex: "#CC3311"),
        .init(name: "Teal", hex: "#009988"),
        .init(name: "Pink", hex: "#EE7733"),
        .init(name: "Black", hex: "#000000")
    ]

    static let protanopia: [ColorPaletteEntry] = [
        .init(name: "Blue", hex: "#0077BB"),
        .init(name: "Cyan", hex: "#33BBEE"),
        .init(name: "Teal", hex: "#009988"),
        .init(name: "Yellow", hex: "#EE7733"),
        .init(name: "Orange", hex: "#CC3311"),
        .init(name: "Magenta", hex: "#EE3377"),
        .init(name: "Grey", hex: "#BBBBBB"),
        .init(name: "Black", hex: "#000000")
    ]

    static let tritanopia: [ColorPaletteEntry] = [
        .init(name: "Red", hex: "#CC3311"),
        .init(name: "Blue", hex: "#0077BB"),
        .init(name: "Yellow", hex: "#EECC66"),
        .init(name: "Cyan", hex: "#33BBEE"),
        .init(name: "Magenta", hex: "#EE3377"),
        .init(name: "Teal", hex: "#009988"),
        .init(name: "Grey", hex: "#BBBBBB"),
        .init(name: "Black", hex: "#000000")
    ]

    static let pastel: [ColorPaletteEntry] = [
        .init(name: "Rose", hex: "#F4A6B0"),
        .init(name: "Peach", hex: "#F6C9A0"),
        .init(name: "Lemon", hex: "#FDE9A0"),
        .init(name: "Mint", hex: "#A8DFC0"),
        .init(name: "Sky", hex: "#A0C4E8"),
        .init(name: "Lavender", hex: "#C4A8D8"),
        .init(name: "Lilac", hex: "#D8A8C8"),
        .init(name: "Slate", hex: "#8B8B8B")
    ]

    static func palette(for key: String) -> [ColorPaletteEntry] {
        switch key {
        case "okabe_ito": return okabeIto
        case "deuteranopia": return deuteranopia
        case "protanopia": return protanopia
        case "tritanopia": return tritanopia
        case "pastel": return pastel
        default: return defaultPalette
        }
    }

    static func contrastTextColor(hex: String) -> Color {
        let clean = hex.trimmingCharacters(in: CharacterSet(charactersIn: "#"))
        guard clean.count >= 6 else { return .white }
        let r = Double(Int(clean.prefix(2), radix: 16) ?? 0)
        let g = Double(Int(clean.dropFirst(2).prefix(2), radix: 16) ?? 0)
        let b = Double(Int(clean.dropFirst(4).prefix(2), radix: 16) ?? 0)
        let luminance = 0.299 * r + 0.587 * g + 0.114 * b
        return luminance > 186 ? .black : .white
    }
}

private struct ColorPaletteOption: View {
    let title: String
    let subtitle: String
    let palette: [ColorPaletteEntry]
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
