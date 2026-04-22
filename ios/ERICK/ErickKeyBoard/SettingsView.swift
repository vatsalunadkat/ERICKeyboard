//
//  SettingsView.swift
//  ErickKeyBoard
//
//  Created by ERICK on 2026/3/9.
//

import SwiftUI
import SharedKeyboard

struct SettingsView: View {
    static let appGroupDefaults = UserDefaults(suiteName: "group.com.vatoo.erick") ?? .standard

    @AppStorage("layout_type", store: SettingsView.appGroupDefaults) private var layoutType: String = "logical"
    @AppStorage("dark_theme", store: SettingsView.appGroupDefaults) private var darkTheme: Bool = false
    @AppStorage("theme_mode", store: SettingsView.appGroupDefaults) private var themeMode: String = "system"
    @AppStorage("colorblind_mode", store: SettingsView.appGroupDefaults) private var colorblindMode: Bool = false
    @AppStorage("color_palette", store: SettingsView.appGroupDefaults) private var colorPalette: String = "okabe_ito"
    @AppStorage("left_handed_mode", store: SettingsView.appGroupDefaults) private var leftHandedMode: Bool = false
    @AppStorage("custom_layout_id", store: SettingsView.appGroupDefaults) private var customLayoutId: String = ""
    @AppStorage("font_preference", store: SettingsView.appGroupDefaults) private var fontPreference: String = "system"
    @AppStorage("custom_palette_colors", store: SettingsView.appGroupDefaults) private var customPaletteColors: String = ColorPaletteDefinitions.defaultCustomColors
    @AppStorage("haptic_feedback", store: SettingsView.appGroupDefaults) private var hapticFeedback: Bool = false
    @AppStorage("typing_sounds", store: SettingsView.appGroupDefaults) private var typingSounds: Bool = false
    @AppStorage("input_mode", store: SettingsView.appGroupDefaults) private var inputMode: String = "instant"
    @AppStorage("six_section_dial", store: SettingsView.appGroupDefaults) private var sixSectionDial: Bool = false
    
    // Action closure when the user wants to dismiss settings from Keyboard Extension
    var onClose: (() -> Void)? = nil
    var onSettingsChanged: (() -> Void)? = nil

    @State private var showCustomLayoutList = false
    @State private var showCustomPaletteEditor = false

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

            if showCustomPaletteEditor {
                CustomPaletteEditorView(
                    customColors: $customPaletteColors,
                    onBack: { showCustomPaletteEditor = false }
                )
            } else if showCustomLayoutList {
                CustomLayoutListView(onBack: { showCustomLayoutList = false })
            } else {
                mainSettingsForm
            }
        }
        .onChange(of: layoutType) { _ in
            onSettingsChanged?()
        }
        .onChange(of: colorblindMode) { _ in
            onSettingsChanged?()
        }
        .onChange(of: colorPalette) { _ in
            onSettingsChanged?()
        }
        .onChange(of: customPaletteColors) { _ in
            onSettingsChanged?()
        }
        .onChange(of: themeMode) { _ in
            onSettingsChanged?()
        }
        .onChange(of: fontPreference) { _ in
            onSettingsChanged?()
        }
        .onChange(of: leftHandedMode) { _ in
            onSettingsChanged?()
        }
        .onChange(of: customLayoutId) { _ in
            onSettingsChanged?()
        }
        .onChange(of: hapticFeedback) { _ in
            onSettingsChanged?()
        }
        .onChange(of: typingSounds) { _ in
            onSettingsChanged?()
        }
        .onChange(of: inputMode) { _ in
            onSettingsChanged?()
        }
        .onChange(of: sixSectionDial) { _ in
            onSettingsChanged?()
        }
    }

    @State private var expandedSection: String? = nil

    private var mainSettingsForm: some View {
        ScrollView {
            VStack(spacing: 8) {
                // Dial Mode Section
                CollapsibleSettingsSection(
                    title: "Dial Mode",
                    isExpanded: expandedSection == "dial_mode",
                    onToggle: { expandedSection = expandedSection == "dial_mode" ? nil : "dial_mode" }
                ) {
                    VStack(alignment: .leading, spacing: 4) {
                        Toggle("6-Section Dial Mode", isOn: $sixSectionDial)
                            .padding(.horizontal, 12).padding(.vertical, 4)
                        Text("Switches both dials from 8 segments (45° each) to 6 segments (60° each). Includes a dedicated Symbols layer via the NW single-swipe.")
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 12)
                    }
                }

                // Layout Section
                CollapsibleSettingsSection(
                    title: "Keyboard Layout",
                    isExpanded: expandedSection == "layout",
                    onToggle: { expandedSection = expandedSection == "layout" ? nil : "layout" }
                ) {
                    VStack(spacing: 0) {
                        settingsRadioRow(label: "Logical (A–Z)", selected: layoutType == "logical") {
                            layoutType = "logical"
                        }
                        settingsRadioRow(label: "Efficiency", selected: layoutType == "efficiency") {
                            layoutType = "efficiency"
                        }

                        // Custom layouts
                        let storage = IOSCustomLayoutStorage()
                        let manager = CustomLayoutManager(storage: storage)
                        let _ = manager.loadAll()
                        let customs = manager.getAll()
                        ForEach(Array(customs.enumerated()), id: \.element.id) { _, cl in
                            settingsRadioRow(label: cl.name, selected: layoutType == "custom" && customLayoutId == cl.id) {
                                customLayoutId = cl.id
                                layoutType = "custom"
                            }
                        }

                        Divider().padding(.vertical, 4)

                        Button(action: { showCustomLayoutList = true }) {
                            HStack {
                                Image(systemName: "pencil.circle")
                                Text("Manage Custom Layouts")
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 12).padding(.vertical, 8)
                        }
                    }
                }

                // Appearance Section
                CollapsibleSettingsSection(
                    title: "Appearance",
                    isExpanded: expandedSection == "appearance",
                    onToggle: { expandedSection = expandedSection == "appearance" ? nil : "appearance" }
                ) {
                    VStack(spacing: 0) {
                        // Theme toggle
                        Text("Theme").font(.subheadline).fontWeight(.medium)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 12).padding(.bottom, 4)

                        Picker("Theme", selection: $themeMode) {
                            Text("System").tag("system")
                            Text("Light").tag("light")
                            Text("Dark").tag("dark")
                        }
                        .pickerStyle(.segmented)
                        .padding(.horizontal, 12).padding(.vertical, 4)

                        Divider().padding(.vertical, 4)

                        // Custom Font toggle
                        Toggle("Custom Font", isOn: Binding(
                            get: { fontPreference != "system" },
                            set: { newValue in
                                fontPreference = newValue ? "verdana" : "system"
                            }
                        ))
                            .padding(.horizontal, 12).padding(.vertical, 4)

                        if fontPreference != "system" {
                            settingsRadioRow(label: "Verdana", selected: fontPreference == "verdana") { fontPreference = "verdana" }
                            settingsRadioRow(label: "Georgia", selected: fontPreference == "georgia") { fontPreference = "georgia" }
                            settingsRadioRow(label: "OpenDyslexic", selected: fontPreference == "opendyslexic") { fontPreference = "opendyslexic" }
                        }

                        Divider().padding(.vertical, 4)

                        // Custom Colors toggle
                        Toggle("Custom Colors", isOn: Binding(
                            get: { colorPalette == "pastel" || colorPalette == "custom" },
                            set: { newValue in
                                if newValue {
                                    colorPalette = "pastel"
                                    if colorblindMode { colorblindMode = false }
                                } else {
                                    colorPalette = "okabe_ito"
                                }
                            }
                        ))
                            .padding(.horizontal, 12).padding(.vertical, 4)

                        if colorPalette == "pastel" || colorPalette == "custom" {
                            ColorPaletteOption(
                                title: "Pastel",
                                subtitle: "Softer colors that are easier on the eyes",
                                palette: ColorPaletteDefinitions.pastel,
                                selected: colorPalette == "pastel",
                                onSelect: { colorPalette = "pastel" }
                            )

                            CustomPaletteOption(
                                customColors: customPaletteColors,
                                selected: colorPalette == "custom",
                                onSelect: { colorPalette = "custom" },
                                onEditColors: { showCustomPaletteEditor = true }
                            )
                        }
                    }
                }

                // Accessibility Section
                CollapsibleSettingsSection(
                    title: "Accessibility",
                    isExpanded: expandedSection == "accessibility",
                    onToggle: { expandedSection = expandedSection == "accessibility" ? nil : "accessibility" }
                ) {
                    VStack(spacing: 4) {
                        Toggle("Enable Colorblind Mode", isOn: Binding(
                            get: { colorblindMode },
                            set: { newValue in
                                colorblindMode = newValue
                                if newValue && (colorPalette == "pastel" || colorPalette == "custom") {
                                    colorPalette = "okabe_ito"
                                }
                            }
                        ))
                            .padding(.horizontal, 12).padding(.vertical, 4)

                        if colorblindMode {
                            ColorPaletteOption(
                                title: "Okabe-Ito (Universal)",
                                subtitle: "Recommended for all types",
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
                        }

                        Toggle("Left-Handed Mode", isOn: $leftHandedMode)
                            .padding(.horizontal, 12).padding(.vertical, 4)
                    }
                }

                // Feedback Section
                CollapsibleSettingsSection(
                    title: "Feedback",
                    isExpanded: expandedSection == "feedback",
                    onToggle: { expandedSection = expandedSection == "feedback" ? nil : "feedback" }
                ) {
                    VStack(spacing: 4) {
                        Toggle("Haptic Feedback", isOn: $hapticFeedback)
                            .padding(.horizontal, 12).padding(.vertical, 4)
                        if hapticFeedback {
                            Text("Strong vibration for utility keys, light for letters.")
                                .font(.caption)
                                .foregroundColor(.secondary)
                                .padding(.horizontal, 12)
                        }

                        Toggle("Typing Sounds", isOn: $typingSounds)
                            .padding(.horizontal, 12).padding(.vertical, 4)
                    }
                }

                // Dial Mode Section
                CollapsibleSettingsSection(
                    title: "Dial Mode",
                    isExpanded: expandedSection == "dial_mode",
                    onToggle: { expandedSection = expandedSection == "dial_mode" ? nil : "dial_mode" }
                ) {
                    VStack(alignment: .leading, spacing: 4) {
                        Toggle("6-Section Dial Mode", isOn: $sixSectionDial)
                            .padding(.horizontal, 12).padding(.vertical, 4)
                        Text("Switches both dials from 8 segments (45° each) to 6 segments (60° each). Includes a dedicated Symbols layer via the NW single-swipe.")
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 12)
                    }
                }

                // Input Mode Section
                CollapsibleSettingsSection(
                    title: "Input Mode",
                    isExpanded: expandedSection == "input_mode",
                    onToggle: { expandedSection = expandedSection == "input_mode" ? nil : "input_mode" }
                ) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Choose how chords are triggered when using the dials.")
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 12).padding(.bottom, 4)

                        InputModeRadioOption(
                            title: "Quick Type",
                            description: "Type at full speed. Characters appear as soon as you release either dial.",
                            selected: inputMode == "instant",
                            action: { inputMode = "instant" }
                        )
                        InputModeRadioOption(
                            title: "Steady Type",
                            description: "Take your time. Characters appear only after both dials return to center.",
                            selected: inputMode == "confirm",
                            action: { inputMode = "confirm" }
                        )
                        InputModeRadioOption(
                            title: "One-Handed",
                            description: "Type with one hand. Lock a direction on the left dial, then swipe the right dial to type.",
                            selected: inputMode == "assisted",
                            action: { inputMode = "assisted" }
                        )
                    }
                }

                // Privacy & Security Section
                CollapsibleSettingsSection(
                    title: "Privacy & Security",
                    isExpanded: expandedSection == "privacy",
                    onToggle: { expandedSection = expandedSection == "privacy" ? nil : "privacy" }
                ) {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("🔒 Your privacy is our priority. ERICKeyboard:")
                            .font(.caption).fontWeight(.semibold)
                        Text("✓ Does NOT collect any text you type\n✓ Does NOT store passwords\n✓ Only stores preferences locally")
                            .font(.caption2).foregroundColor(.secondary)
                        Link(destination: URL(string: "https://github.com/vatsalunadkat/ERICKeyboard")!) {
                            HStack {
                                Text("\u{1F4BB} View on GitHub")
                                    .font(.caption)
                                    .fontWeight(.medium)
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 6)
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color.accentColor, lineWidth: 1)
                            )
                        }
                        .padding(.top, 4)
                    }
                    .padding(.horizontal, 12).padding(.vertical, 4)
                }
            }
            .padding(.horizontal, 8).padding(.vertical, 4)
        }
    }

    @ViewBuilder
    private func settingsRadioRow(label: String, selected: Bool, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            HStack(spacing: 8) {
                Image(systemName: selected ? "largecircle.fill.circle" : "circle")
                    .foregroundColor(selected ? .accentColor : .secondary)
                    .font(.body)
                Text(label).foregroundColor(.primary).font(.body)
                Spacer()
            }
            .padding(.horizontal, 12).padding(.vertical, 6)
        }
    }

    @ViewBuilder
    private func fontOption(key: String, label: String, font: Font) -> some View {
        Button(action: { fontPreference = key }) {
            HStack {
                Image(systemName: fontPreference == key ? "largecircle.fill.circle" : "circle")
                    .foregroundColor(fontPreference == key ? .accentColor : .secondary)
                Text(label).foregroundColor(.primary)
            }
        }
    }
}

// MARK: - Collapsible Settings Section

private struct CollapsibleSettingsSection<Content: View>: View {
    let title: String
    let isExpanded: Bool
    let onToggle: () -> Void
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(spacing: 0) {
            Button(action: onToggle) {
                HStack {
                    Text(title)
                        .font(.headline)
                        .foregroundColor(.accentColor)
                    Spacer()
                    Image(systemName: "chevron.down")
                        .rotationEffect(.degrees(isExpanded ? 180 : 0))
                        .foregroundColor(.accentColor)
                        .animation(.easeInOut(duration: 0.2), value: isExpanded)
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 10)
            }

            if isExpanded {
                content()
            }
        }
        .background(Color(UIColor.secondarySystemGroupedBackground))
        .cornerRadius(10)
    }
}

private struct InputModeRadioOption: View {
    let title: String
    let description: String
    let selected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(alignment: .top, spacing: 8) {
                Image(systemName: selected ? "largecircle.fill.circle" : "circle")
                    .foregroundColor(selected ? .accentColor : .secondary)
                    .padding(.top, 2)
                VStack(alignment: .leading, spacing: 2) {
                    Text(title)
                        .font(.body)
                        .foregroundColor(.primary)
                    Text(description)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                Spacer()
            }
            .padding(.horizontal, 12).padding(.vertical, 6)
        }
        .buttonStyle(.plain)
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

    static let defaultCustomColors = "#E60012,#F39800,#FFF100,#009944,#0068B7,#1D2088,#920783,#000000"

    static func customPalette() -> [ColorPaletteEntry] {
        let defaults = UserDefaults(suiteName: "group.com.vatoo.erick") ?? .standard
        let stored = defaults.string(forKey: "custom_palette_colors") ?? defaultCustomColors
        let hexList = stored.components(separatedBy: ",").map { $0.trimmingCharacters(in: .whitespaces) }
        let labels = ["Color 1", "Color 2", "Color 3", "Color 4", "Color 5", "Color 6", "Color 7", "Color 8"]
        return hexList.enumerated().map { (i, hex) in
            ColorPaletteEntry(name: labels[min(i, labels.count - 1)], hex: hex)
        }
    }

    static func palette(for key: String) -> [ColorPaletteEntry] {
        switch key {
        case "okabe_ito": return okabeIto
        case "deuteranopia": return deuteranopia
        case "protanopia": return protanopia
        case "tritanopia": return tritanopia
        case "pastel": return pastel
        case "custom": return customPalette()
        default: return defaultPalette
        }
    }

    // 6-color palette variants for 6-section dial mode
    static let defaultPalette6: [ColorPaletteEntry] = [
        .init(name: "Red", hex: "#E60012"),
        .init(name: "Orange", hex: "#F39800"),
        .init(name: "Green", hex: "#009944"),
        .init(name: "Blue", hex: "#0068B7"),
        .init(name: "Indigo", hex: "#1D2088"),
        .init(name: "Violet", hex: "#920783")
    ]
    static let okabeIto6: [ColorPaletteEntry] = [
        .init(name: "Orange", hex: "#E69F00"),
        .init(name: "Sky Blue", hex: "#56B4E9"),
        .init(name: "Bluish Green", hex: "#009E73"),
        .init(name: "Blue", hex: "#0072B2"),
        .init(name: "Vermillion", hex: "#D55E00"),
        .init(name: "Reddish Purple", hex: "#CC79A7")
    ]
    static let deuteranopia6: [ColorPaletteEntry] = [
        .init(name: "Blue", hex: "#0072B2"),
        .init(name: "Orange", hex: "#E69F00"),
        .init(name: "Yellow", hex: "#F0E442"),
        .init(name: "Dark Red", hex: "#CC3311"),
        .init(name: "Teal", hex: "#009988"),
        .init(name: "Pink", hex: "#EE7733")
    ]
    static let protanopia6: [ColorPaletteEntry] = [
        .init(name: "Blue", hex: "#0077BB"),
        .init(name: "Cyan", hex: "#33BBEE"),
        .init(name: "Yellow", hex: "#EE7733"),
        .init(name: "Orange", hex: "#CC3311"),
        .init(name: "Magenta", hex: "#EE3377"),
        .init(name: "Grey", hex: "#BBBBBB")
    ]
    static let tritanopia6: [ColorPaletteEntry] = [
        .init(name: "Red", hex: "#CC3311"),
        .init(name: "Blue", hex: "#0077BB"),
        .init(name: "Cyan", hex: "#33BBEE"),
        .init(name: "Magenta", hex: "#EE3377"),
        .init(name: "Teal", hex: "#009988"),
        .init(name: "Yellow", hex: "#EECC66")
    ]
    static let pastel6: [ColorPaletteEntry] = [
        .init(name: "Rose", hex: "#F4A6B0"),
        .init(name: "Peach", hex: "#F6C9A0"),
        .init(name: "Mint", hex: "#A8DFC0"),
        .init(name: "Sky", hex: "#A0C4E8"),
        .init(name: "Lavender", hex: "#C4A8D8"),
        .init(name: "Lilac", hex: "#D8A8C8")
    ]

    static func palette6(for key: String) -> [ColorPaletteEntry] {
        switch key {
        case "okabe_ito": return okabeIto6
        case "deuteranopia": return deuteranopia6
        case "protanopia": return protanopia6
        case "tritanopia": return tritanopia6
        case "pastel": return pastel6
        case "custom":
            let full = customPalette()
            return Array(full.prefix(6))
        default: return defaultPalette6
        }
    }

    static func contrastTextColor(hex: String, paletteKey: String? = nil) -> Color {
        if paletteKey == "pastel" { return .black }
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
            .padding(.horizontal, 12).padding(.vertical, 4)
        }
    }
}

// MARK: - Custom Palette Option

private struct CustomPaletteOption: View {
    let customColors: String
    let selected: Bool
    let onSelect: () -> Void
    let onEditColors: () -> Void

    private var hexList: [String] {
        customColors.components(separatedBy: ",").map { $0.trimmingCharacters(in: .whitespaces) }
    }

    var body: some View {
        Button(action: onSelect) {
            VStack(alignment: .leading, spacing: 6) {
                HStack {
                    Image(systemName: selected ? "largecircle.fill.circle" : "circle")
                        .foregroundColor(selected ? .accentColor : .secondary)
                    VStack(alignment: .leading) {
                        Text("Create Your Own").foregroundColor(.primary)
                        Text("Pick your own 8 colors")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    Spacer()
                    Button("Edit") { onEditColors() }
                        .font(.caption)
                        .buttonStyle(.bordered)
                }

                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 6) {
                        ForEach(Array(hexList.enumerated()), id: \.offset) { i, hex in
                            VStack(spacing: 2) {
                                RoundedRectangle(cornerRadius: 4)
                                    .fill(Color(hex: hex))
                                    .frame(width: 32, height: 32)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 4)
                                            .stroke(Color.secondary.opacity(0.3), lineWidth: 1)
                                    )
                                Text("\(i + 1)")
                                    .font(.system(size: 8))
                                    .foregroundColor(.secondary)
                            }
                        }
                    }
                    .padding(.leading, 28)
                }
            }
            .padding(.horizontal, 12).padding(.vertical, 4)
        }
    }
}

// MARK: - Custom Palette Editor View

struct CustomPaletteEditorView: View {
    @Binding var customColors: String
    var onBack: () -> Void

    private let directionLabels = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"]

    @State private var colors: [String] = []
    @State private var selectedIndex: Int = 0
    @State private var pickerColor: Color = .red

    var body: some View {
        VStack(spacing: 0) {
            // Header
            HStack {
                Button(action: onBack) {
                    Image(systemName: "arrow.left")
                        .font(.title3)
                        .padding()
                }
                Text("Custom Palette")
                    .font(.headline)
                Spacer()
                Button("Save") {
                    customColors = colors.joined(separator: ",")
                    onBack()
                }
                .padding()
            }
            .background(Color(UIColor.systemGray6))

            ScrollView {
                VStack(spacing: 16) {
                    Text("Tap a slot to edit its color:")
                        .font(.subheadline)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    // Color slots
                    HStack(spacing: 8) {
                        ForEach(0..<8, id: \.self) { index in
                            VStack(spacing: 2) {
                                RoundedRectangle(cornerRadius: 6)
                                    .fill(index < colors.count ? Color(hex: colors[index]) : Color.gray)
                                    .frame(width: 36, height: 36)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 6)
                                            .stroke(index == selectedIndex ? Color.accentColor : Color.secondary.opacity(0.3),
                                                    lineWidth: index == selectedIndex ? 3 : 1)
                                    )
                                    .onTapGesture {
                                        selectedIndex = index
                                        if index < colors.count {
                                            pickerColor = Color(hex: colors[index])
                                        }
                                    }
                                Text(directionLabels[index])
                                    .font(.system(size: 10))
                                    .foregroundColor(index == selectedIndex ? .accentColor : .secondary)
                            }
                        }
                    }

                    // Current color preview
                    RoundedRectangle(cornerRadius: 8)
                        .fill(selectedIndex < colors.count ? Color(hex: colors[selectedIndex]) : Color.gray)
                        .frame(height: 48)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.secondary.opacity(0.3), lineWidth: 1)
                        )

                    // Color picker
                    ColorPicker("Pick Color", selection: $pickerColor, supportsOpacity: false)
                        .onChange(of: pickerColor) { newColor in
                            if selectedIndex < colors.count {
                                colors[selectedIndex] = newColor.toHexString()
                            }
                        }

                    // Hex input
                    HStack {
                        Text("#")
                        TextField("Hex", text: Binding(
                            get: {
                                if selectedIndex < colors.count {
                                    return colors[selectedIndex].trimmingCharacters(in: CharacterSet(charactersIn: "#"))
                                }
                                return ""
                            },
                            set: { newValue in
                                let filtered = String(newValue.filter { "0123456789ABCDEFabcdef".contains($0) }.prefix(6))
                                if filtered.count == 6 && selectedIndex < colors.count {
                                    let hex = "#\(filtered.uppercased())"
                                    colors[selectedIndex] = hex
                                    pickerColor = Color(hex: hex)
                                }
                            }
                        ))
                        .textFieldStyle(.roundedBorder)
                        .autocapitalization(.allCharacters)
                        .disableAutocorrection(true)
                    }

                    // RGB inputs
                    HStack(spacing: 8) {
                        rgbField(label: "R", component: 0)
                        rgbField(label: "G", component: 1)
                        rgbField(label: "B", component: 2)
                    }
                }
                .padding(16)
            }
        }
        .onAppear {
            let hexList = customColors.components(separatedBy: ",").map { $0.trimmingCharacters(in: .whitespaces) }
            colors = hexList
            while colors.count < 8 { colors.append("#CCCCCC") }
            if !colors.isEmpty {
                pickerColor = Color(hex: colors[0])
            }
        }
    }

    @ViewBuilder
    private func rgbField(label: String, component: Int) -> some View {
        VStack {
            Text(label).font(.caption)
            TextField(label, text: Binding(
                get: {
                    guard selectedIndex < colors.count else { return "0" }
                    let hex = colors[selectedIndex].trimmingCharacters(in: CharacterSet(charactersIn: "#"))
                    guard hex.count >= 6 else { return "0" }
                    let start = hex.index(hex.startIndex, offsetBy: component * 2)
                    let end = hex.index(start, offsetBy: 2)
                    return String(Int(hex[start..<end], radix: 16) ?? 0)
                },
                set: { newValue in
                    guard selectedIndex < colors.count else { return }
                    let val255 = max(0, min(255, Int(newValue) ?? 0))
                    let hex = colors[selectedIndex].trimmingCharacters(in: CharacterSet(charactersIn: "#"))
                    guard hex.count >= 6 else { return }
                    var components = [0, 0, 0]
                    for c in 0..<3 {
                        let s = hex.index(hex.startIndex, offsetBy: c * 2)
                        let e = hex.index(s, offsetBy: 2)
                        components[c] = Int(hex[s..<e], radix: 16) ?? 0
                    }
                    components[component] = val255
                    let newHex = String(format: "#%02X%02X%02X", components[0], components[1], components[2])
                    colors[selectedIndex] = newHex
                    pickerColor = Color(hex: newHex)
                }
            ))
            .textFieldStyle(.roundedBorder)
            .keyboardType(.numberPad)
        }
    }
}

extension Color {
    func toHexString() -> String {
        guard let components = UIColor(self).cgColor.components else { return "#000000" }
        let r = Int((components.count > 0 ? components[0] : 0) * 255)
        let g = Int((components.count > 1 ? components[1] : 0) * 255)
        let b = Int((components.count > 2 ? components[2] : 0) * 255)
        return String(format: "#%02X%02X%02X", r, g, b)
    }
}

// MARK: - Custom Layout List View

struct CustomLayoutListView: View {
    var onBack: () -> Void

    @State private var layouts: [CustomLayout] = []
    @State private var showCreateBlank = false
    @State private var showDuplicate = false
    @State private var newLayoutName = ""
    @State private var duplicateSource: LayoutType = .logical
    @State private var editingLayout: CustomLayout? = nil
    @State private var deleteTarget: CustomLayout? = nil

    private func manager() -> CustomLayoutManager {
        let m = CustomLayoutManager(storage: IOSCustomLayoutStorage())
        m.loadAll()
        return m
    }

    private func reloadLayouts() {
        layouts = manager().getAll()
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button(action: onBack) {
                    Image(systemName: "arrow.left")
                        .font(.title3)
                        .padding()
                }
                Text("Custom Layouts")
                    .font(.headline)
                Spacer()
                Menu {
                    Button("Create Blank") { newLayoutName = ""; showCreateBlank = true }
                    Button("Duplicate Built-in") { newLayoutName = ""; showDuplicate = true }
                } label: {
                    Image(systemName: "plus.circle")
                        .font(.title3)
                        .padding()
                }
            }
            .background(Color(UIColor.systemGray6))

            if let editing = editingLayout {
                CustomLayoutEditorView(
                    layout: editing,
                    onSave: { updated in
                        let m = manager()
                        let _ = m.save(layout: updated)
                        reloadLayouts()
                        editingLayout = nil
                    },
                    onBack: { editingLayout = nil }
                )
            } else if layouts.isEmpty {
                Spacer()
                Text("No custom layouts yet.\nTap + to create one.")
                    .multilineTextAlignment(.center)
                    .foregroundColor(.secondary)
                Spacer()
            } else {
                List {
                    ForEach(Array(layouts.enumerated()), id: \.element.id) { _, cl in
                        Button(action: { editingLayout = cl }) {
                            VStack(alignment: .leading) {
                                Text(cl.name).font(.body)
                                let count = cl.normalChordMap.values.flatMap { ($0 as! [String]) }.filter { !$0.isEmpty }.count
                                Text("\(count) characters mapped")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                        }
                        .swipeActions(edge: .trailing) {
                            Button(role: .destructive) { deleteTarget = cl } label: {
                                Label("Delete", systemImage: "trash")
                            }
                        }
                    }
                }
            }
        }
        .onAppear { reloadLayouts() }
        .alert("New Blank Layout", isPresented: $showCreateBlank) {
            TextField("Layout Name", text: $newLayoutName)
            Button("Create") {
                let m = manager()
                let layout = m.createBlank(name: newLayoutName)
                let _ = m.save(layout: layout)
                reloadLayouts()
                editingLayout = layout
            }
            Button("Cancel", role: .cancel) {}
        }
        .alert("Duplicate Built-in", isPresented: $showDuplicate) {
            TextField("New Layout Name", text: $newLayoutName)
            Button("Logical") {
                let m = manager()
                let layout = m.duplicateFromBuiltIn(sourceLayout: .logical, customName: newLayoutName)
                let _ = m.save(layout: layout)
                reloadLayouts()
                editingLayout = layout
            }
            Button("Efficiency") {
                let m = manager()
                let layout = m.duplicateFromBuiltIn(sourceLayout: .efficiency, customName: newLayoutName)
                let _ = m.save(layout: layout)
                reloadLayouts()
                editingLayout = layout
            }
            Button("Cancel", role: .cancel) {}
        }
        .alert("Delete Layout?", isPresented: Binding(
            get: { deleteTarget != nil },
            set: { if !$0 { deleteTarget = nil } }
        )) {
            Button("Delete", role: .destructive) {
                if let t = deleteTarget {
                    let m = manager()
                    m.delete(id: t.id)
                    reloadLayouts()
                    deleteTarget = nil
                }
            }
            Button("Cancel", role: .cancel) { deleteTarget = nil }
        } message: {
            Text("Delete \"\(deleteTarget?.name ?? "")\"? This cannot be undone.")
        }
    }
}

// MARK: - Custom Layout Editor View

struct CustomLayoutEditorView: View {
    let layout: CustomLayout
    var onSave: (CustomLayout) -> Void
    var onBack: () -> Void

    @AppStorage("colorblind_mode", store: SettingsView.appGroupDefaults) private var colorblindMode: Bool = false
    @AppStorage("color_palette", store: SettingsView.appGroupDefaults) private var colorPalette: String = "okabe_ito"

    @State private var name: String = ""
    @State private var selectedTab = 0

    // Chord editing state — stored as dictionaries matching the KMP data model
    @State private var normalChords: [String: [String]] = [:]
    @State private var shiftedChords: [String: [String]] = [:]
    @State private var singleSwipeNormal: [String: String] = [:]
    @State private var singleSwipeShifted: [String: String] = [:]

    private var currentPalette: [ColorPaletteEntry] {
        if colorblindMode {
            return ColorPaletteDefinitions.palette(for: colorPalette)
        } else {
            return ColorPaletteDefinitions.defaultPalette
        }
    }

    private let allDirections = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"]
    private let dirLabels = ["N (Up)", "NE", "E (Right)", "SE", "S (Down)", "SW", "W (Left)", "NW"]

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button(action: onBack) {
                    Image(systemName: "arrow.left")
                        .font(.title3)
                        .padding()
                }
                Text("Edit Layout")
                    .font(.headline)
                Spacer()
                Button("Save") { saveLayout() }
                    .padding()
            }
            .background(Color(UIColor.systemGray6))

            TextField("Layout Name", text: $name)
                .textFieldStyle(.roundedBorder)
                .padding(.horizontal)
                .padding(.vertical, 8)

            Picker("Section", selection: $selectedTab) {
                Text("Normal").tag(0)
                Text("Shifted").tag(1)
                Text("Single Swipe").tag(2)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal)

            switch selectedTab {
            case 0: chordEditor(chords: $normalChords, label: "Normal")
            case 1: chordEditor(chords: $shiftedChords, label: "Shifted")
            case 2: singleSwipeEditor
            default: EmptyView()
            }
        }
        .onAppear { loadFromLayout() }
    }

    private func loadFromLayout() {
        name = layout.name

        // Convert KMP Direction-keyed maps to String-keyed for simpler SwiftUI binding
        for dirStr in allDirections {
            let dir = wheelDirection(from: dirStr)
            normalChords[dirStr] = (layout.normalChordMap[dir] as? [String]) ?? Array(repeating: "", count: 8)
            shiftedChords[dirStr] = (layout.shiftedChordMap[dir] as? [String]) ?? Array(repeating: "", count: 8)

            if let binding = layout.singleSwipeNormalMap[dir] as? SingleSwipeBinding {
                singleSwipeNormal[dirStr] = serializeBinding(binding)
            }
            if let binding = layout.singleSwipeShiftedMap[dir] as? SingleSwipeBinding {
                singleSwipeShifted[dirStr] = serializeBinding(binding)
            }
        }
    }

    private func saveLayout() {
        let normalMap = NSMutableDictionary()
        let shiftedMap = NSMutableDictionary()
        let singleNormalMap = NSMutableDictionary()
        let singleShiftedMap = NSMutableDictionary()

        for dirStr in allDirections {
            let dir = wheelDirection(from: dirStr)
            normalMap[dir] = normalChords[dirStr] ?? Array(repeating: "", count: 8)
            shiftedMap[dir] = shiftedChords[dirStr] ?? Array(repeating: "", count: 8)

            if let bindStr = singleSwipeNormal[dirStr], let bind = deserializeBinding(bindStr) {
                singleNormalMap[dir] = bind
            }
            if let bindStr = singleSwipeShifted[dirStr], let bind = deserializeBinding(bindStr) {
                singleShiftedMap[dir] = bind
            }
        }

        let updated = CustomLayout(
            id: layout.id,
            name: name.trimmingCharacters(in: .whitespaces).isEmpty ? "Custom Layout" : name.trimmingCharacters(in: .whitespaces),
            normalChordMap: normalMap as! [Direction : [String]],
            shiftedChordMap: shiftedMap as! [Direction : [String]],
            singleSwipeNormalMap: singleNormalMap as! [Direction : SingleSwipeBinding],
            singleSwipeShiftedMap: singleShiftedMap as! [Direction : SingleSwipeBinding]
        )
        onSave(updated)
    }

    private func wheelDirection(from str: String) -> Direction {
        switch str {
        case "N": return .n
        case "NE": return .ne
        case "E": return .e
        case "SE": return .se
        case "S": return .s
        case "SW": return .sw
        case "W": return .w
        case "NW": return .nw
        default: return .none
        }
    }

    private func serializeBinding(_ b: SingleSwipeBinding) -> String {
        return b.toSerializable()
    }

    private func deserializeBinding(_ s: String) -> SingleSwipeBinding? {
        return SingleSwipeBinding.companion.fromSerializable(s: s)
    }

    // MARK: - Chord Editor

    private func chordEditor(chords: Binding<[String: [String]]>, label: String) -> some View {
        let pal = currentPalette
        return List {
            ForEach(Array(allDirections.enumerated()), id: \.offset) { idx, dirStr in
                DisclosureGroup {
                    ForEach(0..<8, id: \.self) { i in
                        HStack {
                            Circle()
                                .fill(i < pal.count ? Color(hex: pal[i].hex) : Color.gray)
                                .frame(width: 14, height: 14)
                            Text("\(allDirections[i]) (\(i < pal.count ? pal[i].name : ""))")
                                .frame(width: 100, alignment: .leading)
                                .font(.caption)
                            TextField("", text: Binding(
                                get: { chords.wrappedValue[dirStr]?[i] ?? "" },
                                set: { newVal in
                                    var arr = chords.wrappedValue[dirStr] ?? Array(repeating: "", count: 8)
                                    arr[i] = String(newVal.prefix(1))
                                    chords.wrappedValue[dirStr] = arr
                                }
                            ))
                            .textFieldStyle(.roundedBorder)
                        }
                    }
                } label: {
                    HStack {
                        Text(dirLabels[idx]).font(.body)
                        Spacer()
                        let chars = (chords.wrappedValue[dirStr] ?? []).filter { !$0.isEmpty }.joined(separator: " ")
                        Text(chars).font(.caption).foregroundColor(.secondary)
                    }
                }
            }
        }
    }

    // MARK: - Single Swipe Editor

    private var singleSwipeEditor: some View {
        List {
            Section("Normal Mode") {
                ForEach(Array(allDirections.enumerated()), id: \.offset) { idx, dirStr in
                    HStack {
                        Text(dirLabels[idx]).frame(width: 80, alignment: .leading)
                        Text(singleSwipeNormal[dirStr] ?? "(none)")
                            .foregroundColor(.secondary)
                    }
                }
            }
            Section("Shifted Mode") {
                ForEach(Array(allDirections.enumerated()), id: \.offset) { idx, dirStr in
                    HStack {
                        Text(dirLabels[idx]).frame(width: 80, alignment: .leading)
                        Text(singleSwipeShifted[dirStr] ?? "(none)")
                            .foregroundColor(.secondary)
                    }
                }
            }
        }
    }
}
