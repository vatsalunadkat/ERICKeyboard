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
    @AppStorage("prediction_domain", store: SettingsView.appGroupDefaults) private var predictionDomain: String = "general"
    @AppStorage("six_section_dial", store: SettingsView.appGroupDefaults) private var sixSectionDial: Bool = false
    
    // Action closure when the user wants to dismiss settings from Keyboard Extension
    var onClose: (() -> Void)? = nil
    var onSettingsChanged: (() -> Void)? = nil

    @State private var showCustomLayoutList = false
    @State private var showCustomPaletteEditor = false
    @State private var showSetupWizard = false

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
        .sheet(isPresented: $showSetupWizard) {
            SetupWizardSheet(
                currentColorPalette: colorPalette,
                onApply: { recommendation in
                    applySetupWizardRecommendation(recommendation)
                    showSetupWizard = false
                },
                onDismiss: {
                    showSetupWizard = false
                }
            )
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
        .onChange(of: predictionDomain) { _ in
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
                VStack(alignment: .leading, spacing: 8) {
                    Text("Start with the essentials")
                        .font(.headline)
                        .frame(maxWidth: .infinity, alignment: .leading)
                    Text("Most people only need Dial Mode, Input Mode, Prediction, and Accessibility. The rest is optional customization.")
                        .font(.caption)
                        .foregroundColor(.secondary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
                .padding(12)
                .background(Color(UIColor.secondarySystemGroupedBackground))
                .cornerRadius(10)

                Button(action: { showSetupWizard = true }) {
                    Text("Setup Wizard")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                }
                .buttonStyle(.bordered)

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

                CollapsibleSettingsSection(
                    title: "Prediction",
                    isExpanded: expandedSection == "prediction",
                    onToggle: { expandedSection = expandedSection == "prediction" ? nil : "prediction" }
                ) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Predictions stay on-device. Choose a domain pack if you want ERICK to favor a particular vocabulary family.")
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 12)
                            .padding(.bottom, 4)

                        PredictionDomainRadioOption(
                            title: "General",
                            description: "Balanced everyday English suggestions.",
                            selected: predictionDomain == "general",
                            action: { predictionDomain = "general" }
                        )
                        PredictionDomainRadioOption(
                            title: "Conversation",
                            description: "Favor quick texting and casual chat vocabulary.",
                            selected: predictionDomain == "conversation",
                            action: { predictionDomain = "conversation" }
                        )
                        PredictionDomainRadioOption(
                            title: "Productivity",
                            description: "Favor work, planning, and follow-up vocabulary.",
                            selected: predictionDomain == "productivity",
                            action: { predictionDomain = "productivity" }
                        )
                        PredictionDomainRadioOption(
                            title: "Accessibility",
                            description: "Favor supportive and assistive-communication vocabulary.",
                            selected: predictionDomain == "accessibility",
                            action: { predictionDomain = "accessibility" }
                        )
                        PredictionDomainRadioOption(
                            title: "Gaming",
                            description: "Favor game, party, match, and controller-related terms.",
                            selected: predictionDomain == "gaming",
                            action: { predictionDomain = "gaming" }
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

    private func applySetupWizardRecommendation(_ recommendation: SetupWizardRecommendation) {
        sixSectionDial = recommendation.sixSectionDial
        inputMode = recommendation.inputMode
        leftHandedMode = recommendation.leftHandedMode
        colorblindMode = recommendation.colorblindMode
        colorPalette = recommendation.colorPalette
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

private struct PredictionDomainRadioOption: View {
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

private enum SetupWizardHardware {
    case touch
    case controller
    case both
}

private enum SetupWizardTargetPreference {
    case largerTargets
    case fullEight
}

private enum SetupWizardTypingPreference {
    case fastest
    case steadiest
    case oneHanded
}

private enum SetupWizardHandPreference {
    case right
    case left
}

private enum SetupWizardAccessibilityPreference {
    case standard
    case colorblindSafe
}

private struct SetupWizardRecommendation {
    let sixSectionDial: Bool
    let inputMode: String
    let leftHandedMode: Bool
    let colorblindMode: Bool
    let colorPalette: String
    let summaryLines: [String]
}

private struct SetupWizardSheet: View {
    let currentColorPalette: String
    let onApply: (SetupWizardRecommendation) -> Void
    let onDismiss: () -> Void

    @State private var hardware: SetupWizardHardware = .touch
    @State private var targetPreference: SetupWizardTargetPreference = .largerTargets
    @State private var typingPreference: SetupWizardTypingPreference = .fastest
    @State private var handPreference: SetupWizardHandPreference = .right
    @State private var accessibilityPreference: SetupWizardAccessibilityPreference = .standard

    private var recommendation: SetupWizardRecommendation {
        buildSetupWizardRecommendation(
            hardware: hardware,
            targetPreference: targetPreference,
            typingPreference: typingPreference,
            handPreference: handPreference,
            accessibilityPreference: accessibilityPreference,
            currentColorPalette: currentColorPalette
        )
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("Answer a few questions and ERICK will apply a recommended starting bundle. You can still adjust every setting manually later.")
                        .font(.caption)
                        .foregroundColor(.secondary)

                    SetupWizardQuestion(
                        title: "Main typing setup",
                        options: [
                            SetupWizardOption(title: "Touch first", selected: hardware == .touch) { hardware = .touch },
                            SetupWizardOption(title: "Controller first", selected: hardware == .controller) { hardware = .controller },
                            SetupWizardOption(title: "Both", selected: hardware == .both) { hardware = .both }
                        ]
                    )

                    SetupWizardQuestion(
                        title: "Dial preference",
                        options: [
                            SetupWizardOption(title: "Larger targets", selected: targetPreference == .largerTargets) { targetPreference = .largerTargets },
                            SetupWizardOption(title: "Full 8-direction layout", selected: targetPreference == .fullEight) { targetPreference = .fullEight }
                        ]
                    )

                    SetupWizardQuestion(
                        title: "Typing style",
                        options: [
                            SetupWizardOption(title: "Fastest path", selected: typingPreference == .fastest) { typingPreference = .fastest },
                            SetupWizardOption(title: "Steadier confirmation", selected: typingPreference == .steadiest) { typingPreference = .steadiest },
                            SetupWizardOption(title: "One-handed", selected: typingPreference == .oneHanded) { typingPreference = .oneHanded }
                        ]
                    )

                    SetupWizardQuestion(
                        title: "Handedness",
                        options: [
                            SetupWizardOption(title: "Right-handed", selected: handPreference == .right) { handPreference = .right },
                            SetupWizardOption(title: "Left-handed", selected: handPreference == .left) { handPreference = .left }
                        ]
                    )

                    SetupWizardQuestion(
                        title: "Accessibility default",
                        options: [
                            SetupWizardOption(title: "Standard", selected: accessibilityPreference == .standard) { accessibilityPreference = .standard },
                            SetupWizardOption(title: "Colorblind-safe palette", selected: accessibilityPreference == .colorblindSafe) { accessibilityPreference = .colorblindSafe }
                        ]
                    )

                    VStack(alignment: .leading, spacing: 6) {
                        Text("Recommended bundle")
                            .font(.headline)
                        ForEach(recommendation.summaryLines, id: \.self) { line in
                            Text(line)
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                    .padding(12)
                    .background(Color(UIColor.secondarySystemGroupedBackground))
                    .cornerRadius(10)
                }
                .padding()
            }
            .navigationTitle("Setup Wizard")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel", action: onDismiss)
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Apply") {
                        onApply(recommendation)
                    }
                }
            }
        }
    }
}

private struct SetupWizardOption {
    let title: String
    let selected: Bool
    let onSelect: () -> Void
}

private struct SetupWizardQuestion: View {
    let title: String
    let options: [SetupWizardOption]

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.headline)
            ForEach(Array(options.enumerated()), id: \.offset) { _, option in
                Button(action: option.onSelect) {
                    HStack(spacing: 8) {
                        Image(systemName: option.selected ? "largecircle.fill.circle" : "circle")
                            .foregroundColor(option.selected ? .accentColor : .secondary)
                        Text(option.title)
                            .foregroundColor(.primary)
                        Spacer()
                    }
                    .padding(.vertical, 6)
                }
                .buttonStyle(.plain)
            }
        }
    }
}

private func buildSetupWizardRecommendation(
    hardware: SetupWizardHardware,
    targetPreference: SetupWizardTargetPreference,
    typingPreference: SetupWizardTypingPreference,
    handPreference: SetupWizardHandPreference,
    accessibilityPreference: SetupWizardAccessibilityPreference,
    currentColorPalette: String
) -> SetupWizardRecommendation {
    let sixSectionDial: Bool
    if hardware == .controller {
        sixSectionDial = false
    } else if typingPreference == .oneHanded {
        sixSectionDial = true
    } else {
        sixSectionDial = targetPreference == .largerTargets
    }

    let inputMode: String
    switch typingPreference {
    case .steadiest:
        inputMode = "confirm"
    case .oneHanded:
        inputMode = "assisted"
    case .fastest:
        inputMode = "instant"
    }

    let leftHandedMode = handPreference == .left
    let colorblindMode = accessibilityPreference == .colorblindSafe
    let colorPalette: String
    if colorblindMode {
        colorPalette = "okabe_ito"
    } else if currentColorPalette == "custom" {
        colorPalette = "custom"
    } else if currentColorPalette == "pastel" {
        colorPalette = "pastel"
    } else {
        colorPalette = "okabe_ito"
    }

    var summaryLines = [sixSectionDial ? "6-section dial" : "8-section dial"]
    switch inputMode {
    case "confirm":
        summaryLines.append("Steady Type")
    case "assisted":
        summaryLines.append("One-Handed")
    default:
        summaryLines.append("Quick Type")
    }
    summaryLines.append(leftHandedMode ? "Left-handed mode on" : "Right-handed default")
    summaryLines.append(colorblindMode ? "Colorblind-safe palette enabled" : "Standard accessibility palette")
    if hardware == .controller || hardware == .both {
        summaryLines.append("Controller-first starting bundle")
    }

    return SetupWizardRecommendation(
        sixSectionDial: sixSectionDial,
        inputMode: inputMode,
        leftHandedMode: leftHandedMode,
        colorblindMode: colorblindMode,
        colorPalette: colorPalette,
        summaryLines: summaryLines
    )
}

struct SettingsView_Previews: PreviewProvider {
    static var previews: some View {
        SettingsView()
    }
}
