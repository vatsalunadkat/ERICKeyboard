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
    @AppStorage("keyboard_language", store: SettingsView.appGroupDefaults) private var keyboardLanguage: String = "english"
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
                Text(t("Keyboard Settings"))
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
        .onChange(of: keyboardLanguage) { _ in
            onSettingsChanged?()
        }
        .onChange(of: sixSectionDial) { _ in
            onSettingsChanged?()
        }
    }

    private func t(_ english: String) -> String {
        erickText(english, languageKey: keyboardLanguage)
    }

    @State private var expandedSection: String? = nil

    private var mainSettingsForm: some View {
        ScrollView {
            VStack(spacing: 8) {
                VStack(alignment: .leading, spacing: 8) {
                    Text(t("Start with the essentials"))
                        .font(.headline)
                        .frame(maxWidth: .infinity, alignment: .leading)
                    Text(t("Most people only need Dial Mode, Input Mode, Prediction, and Accessibility. The rest is optional customization."))
                        .font(.caption)
                        .foregroundColor(.secondary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
                .padding(12)
                .background(Color(UIColor.secondarySystemGroupedBackground))
                .cornerRadius(10)

                Button(action: { showSetupWizard = true }) {
                    Text(t("Setup Wizard"))
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                }
                .buttonStyle(.bordered)

                // Dial Mode Section
                CollapsibleSettingsSection(
                    title: t("Dial Mode"),
                    isExpanded: expandedSection == "dial_mode",
                    onToggle: { expandedSection = expandedSection == "dial_mode" ? nil : "dial_mode" }
                ) {
                    VStack(alignment: .leading, spacing: 4) {
                        Toggle(t("6-Section Dial Mode"), isOn: $sixSectionDial)
                            .padding(.horizontal, 12).padding(.vertical, 4)
                        Text(t("Switches both dials from 8 segments (45° each) to 6 segments (60° each). Includes a dedicated Symbols layer via the NW single-swipe."))
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 12)
                    }
                }

                // Layout Section
                CollapsibleSettingsSection(
                    title: t("Language"),
                    isExpanded: expandedSection == "language",
                    onToggle: { expandedSection = expandedSection == "language" ? nil : "language" }
                ) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(t("Languages are currently logical-first. English keeps the dedicated efficiency layout, while the other supported languages use language-aware logical maps and symbol overlays."))
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 12)
                            .padding(.bottom, 4)

                        settingsRadioRow(label: extensionLanguageSelfDisplayName(for: "english"), selected: keyboardLanguage == "english") { keyboardLanguage = "english" }
                        settingsRadioRow(label: extensionLanguageSelfDisplayName(for: "spanish"), selected: keyboardLanguage == "spanish") { keyboardLanguage = "spanish" }
                        settingsRadioRow(label: extensionLanguageSelfDisplayName(for: "portuguese"), selected: keyboardLanguage == "portuguese") { keyboardLanguage = "portuguese" }
                        settingsRadioRow(label: extensionLanguageSelfDisplayName(for: "french"), selected: keyboardLanguage == "french") { keyboardLanguage = "french" }
                        settingsRadioRow(label: extensionLanguageSelfDisplayName(for: "german"), selected: keyboardLanguage == "german") { keyboardLanguage = "german" }
                        settingsRadioRow(label: extensionLanguageSelfDisplayName(for: "italian"), selected: keyboardLanguage == "italian") { keyboardLanguage = "italian" }
                        settingsRadioRow(label: extensionLanguageSelfDisplayName(for: "norwegian_bokmal"), selected: keyboardLanguage == "norwegian_bokmal") { keyboardLanguage = "norwegian_bokmal" }
                        settingsRadioRow(label: extensionLanguageSelfDisplayName(for: "danish"), selected: keyboardLanguage == "danish") { keyboardLanguage = "danish" }
                        settingsRadioRow(label: extensionLanguageSelfDisplayName(for: "swedish"), selected: keyboardLanguage == "swedish") { keyboardLanguage = "swedish" }
                        settingsRadioRow(label: extensionLanguageSelfDisplayName(for: "finnish"), selected: keyboardLanguage == "finnish") { keyboardLanguage = "finnish" }
                    }
                }

                CollapsibleSettingsSection(
                    title: t("Keyboard Layout"),
                    isExpanded: expandedSection == "layout",
                    onToggle: { expandedSection = expandedSection == "layout" ? nil : "layout" }
                ) {
                    VStack(spacing: 0) {
                        if keyboardLanguage != "english" {
                            Text(t("Non-English languages currently fall back to the language-aware logical layout even if Efficiency stays selected."))
                                .font(.caption)
                                .foregroundColor(.secondary)
                                .padding(.horizontal, 12)
                                .padding(.bottom, 4)
                        }

                        settingsRadioRow(label: t("Logical (A-Z)"), selected: layoutType == "logical") {
                            layoutType = "logical"
                        }
                        settingsRadioRow(label: t("Efficiency"), selected: layoutType == "efficiency") {
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
                                Text(t("Manage Custom Layouts"))
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 12).padding(.vertical, 8)
                        }
                    }
                }

                // Appearance Section
                CollapsibleSettingsSection(
                    title: t("Appearance"),
                    isExpanded: expandedSection == "appearance",
                    onToggle: { expandedSection = expandedSection == "appearance" ? nil : "appearance" }
                ) {
                    VStack(spacing: 0) {
                        // Theme toggle
                        Text(t("Theme")).font(.subheadline).fontWeight(.medium)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 12).padding(.bottom, 4)

                        Picker(t("Theme"), selection: $themeMode) {
                            Text(t("System Default")).tag("system")
                            Text(t("Light")).tag("light")
                            Text(t("Dark")).tag("dark")
                        }
                        .pickerStyle(.segmented)
                        .padding(.horizontal, 12).padding(.vertical, 4)

                        Divider().padding(.vertical, 4)

                        // Custom Font toggle
                        Toggle(t("Custom Font"), isOn: Binding(
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
                        Toggle(t("Custom Colors"), isOn: Binding(
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
                                title: t("Pastel"),
                                subtitle: t("Softer colors that are easier on the eyes"),
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
                    title: t("Accessibility"),
                    isExpanded: expandedSection == "accessibility",
                    onToggle: { expandedSection = expandedSection == "accessibility" ? nil : "accessibility" }
                ) {
                    VStack(spacing: 4) {
                        Toggle(t("Enable Colorblind Mode"), isOn: Binding(
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
                                title: t("Okabe-Ito (Universal)"),
                                subtitle: t("Recommended for all types"),
                                palette: ColorPaletteDefinitions.okabeIto,
                                selected: colorPalette == "okabe_ito",
                                onSelect: { colorPalette = "okabe_ito" }
                            )
                            ColorPaletteOption(
                                title: t("Deuteranopia (Green-blind)"),
                                subtitle: t("Optimized for green-blind users"),
                                palette: ColorPaletteDefinitions.deuteranopia,
                                selected: colorPalette == "deuteranopia",
                                onSelect: { colorPalette = "deuteranopia" }
                            )
                            ColorPaletteOption(
                                title: t("Protanopia (Red-blind)"),
                                subtitle: t("Optimized for red-blind users"),
                                palette: ColorPaletteDefinitions.protanopia,
                                selected: colorPalette == "protanopia",
                                onSelect: { colorPalette = "protanopia" }
                            )
                            ColorPaletteOption(
                                title: t("Tritanopia (Blue-blind)"),
                                subtitle: t("Optimized for blue-blind users"),
                                palette: ColorPaletteDefinitions.tritanopia,
                                selected: colorPalette == "tritanopia",
                                onSelect: { colorPalette = "tritanopia" }
                            )
                        }

                        Toggle(t("Left-Handed Mode"), isOn: $leftHandedMode)
                            .padding(.horizontal, 12).padding(.vertical, 4)
                    }
                }

                // Feedback Section
                CollapsibleSettingsSection(
                    title: t("Feedback"),
                    isExpanded: expandedSection == "feedback",
                    onToggle: { expandedSection = expandedSection == "feedback" ? nil : "feedback" }
                ) {
                    VStack(spacing: 4) {
                        Toggle(t("Haptic Feedback"), isOn: $hapticFeedback)
                            .padding(.horizontal, 12).padding(.vertical, 4)
                        if hapticFeedback {
                            Text(t("Strong vibration for utility keys, light for letters."))
                                .font(.caption)
                                .foregroundColor(.secondary)
                                .padding(.horizontal, 12)
                        }

                        Toggle(t("Typing Sounds"), isOn: $typingSounds)
                            .padding(.horizontal, 12).padding(.vertical, 4)
                    }
                }

                // Dial Mode Section
                CollapsibleSettingsSection(
                    title: t("Dial Mode"),
                    isExpanded: expandedSection == "dial_mode",
                    onToggle: { expandedSection = expandedSection == "dial_mode" ? nil : "dial_mode" }
                ) {
                    VStack(alignment: .leading, spacing: 4) {
                        Toggle(t("6-Section Dial Mode"), isOn: $sixSectionDial)
                            .padding(.horizontal, 12).padding(.vertical, 4)
                        Text(t("Switches both dials from 8 segments (45° each) to 6 segments (60° each). Includes a dedicated Symbols layer via the NW single-swipe."))
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 12)
                    }
                }

                // Input Mode Section
                CollapsibleSettingsSection(
                    title: t("Input Mode"),
                    isExpanded: expandedSection == "input_mode",
                    onToggle: { expandedSection = expandedSection == "input_mode" ? nil : "input_mode" }
                ) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(t("Choose how chords are triggered when using the dials."))
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 12).padding(.bottom, 4)

                        InputModeRadioOption(
                            title: t("Quick Type"),
                            description: t("Type at full speed. Characters appear as soon as you release either dial."),
                            selected: inputMode == "instant",
                            action: { inputMode = "instant" }
                        )
                        InputModeRadioOption(
                            title: t("Steady Type"),
                            description: t("Take your time. Characters appear only after both dials return to center."),
                            selected: inputMode == "confirm",
                            action: { inputMode = "confirm" }
                        )
                        InputModeRadioOption(
                            title: t("One-Handed"),
                            description: t("Type with one hand. Lock a direction on the left dial, then swipe the right dial to type."),
                            selected: inputMode == "assisted",
                            action: { inputMode = "assisted" }
                        )
                    }
                }

                CollapsibleSettingsSection(
                    title: t("Prediction"),
                    isExpanded: expandedSection == "prediction",
                    onToggle: { expandedSection = expandedSection == "prediction" ? nil : "prediction" }
                ) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(t("Predictions stay on-device. Choose a domain pack if you want ERICK to favor a particular vocabulary family."))
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 12)
                            .padding(.bottom, 4)

                        PredictionDomainRadioOption(
                            title: t("General"),
                            description: t("Balanced everyday English suggestions."),
                            selected: predictionDomain == "general",
                            action: { predictionDomain = "general" }
                        )
                        PredictionDomainRadioOption(
                            title: t("Conversation"),
                            description: t("Favor quick texting and casual chat vocabulary."),
                            selected: predictionDomain == "conversation",
                            action: { predictionDomain = "conversation" }
                        )
                        PredictionDomainRadioOption(
                            title: t("Productivity"),
                            description: t("Favor work, planning, and follow-up vocabulary."),
                            selected: predictionDomain == "productivity",
                            action: { predictionDomain = "productivity" }
                        )
                        PredictionDomainRadioOption(
                            title: t("Accessibility"),
                            description: t("Favor supportive and assistive-communication vocabulary."),
                            selected: predictionDomain == "accessibility",
                            action: { predictionDomain = "accessibility" }
                        )
                        PredictionDomainRadioOption(
                            title: t("Gaming"),
                            description: t("Favor game, party, match, and controller-related terms."),
                            selected: predictionDomain == "gaming",
                            action: { predictionDomain = "gaming" }
                        )
                    }
                }

                // Privacy & Security Section
                CollapsibleSettingsSection(
                    title: t("Privacy & Security"),
                    isExpanded: expandedSection == "privacy",
                    onToggle: { expandedSection = expandedSection == "privacy" ? nil : "privacy" }
                ) {
                    VStack(alignment: .leading, spacing: 6) {
                        Text(t("Your privacy is our priority. ERICKeyboard:"))
                            .font(.caption).fontWeight(.semibold)
                        Text(t("Does NOT collect any text you type\nDoes NOT store passwords\nOnly stores preferences locally"))
                            .font(.caption2).foregroundColor(.secondary)
                        Link(destination: URL(string: "https://github.com/vatsalunadkat/ERICKeyboard")!) {
                            HStack {
                                Text("\u{1F4BB} \(t("View on GitHub"))")
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
    @AppStorage("keyboard_language", store: SettingsView.appGroupDefaults) private var keyboardLanguage: String = "english"
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
            currentColorPalette: currentColorPalette,
            languageKey: keyboardLanguage
        )
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text(erickText("Answer a few questions and ERICK will apply a recommended starting bundle. You can still adjust every setting manually later.", languageKey: keyboardLanguage))
                        .font(.caption)
                        .foregroundColor(.secondary)

                    SetupWizardQuestion(
                        title: erickText("Main typing setup", languageKey: keyboardLanguage),
                        options: [
                            SetupWizardOption(title: erickText("Touch first", languageKey: keyboardLanguage), selected: hardware == .touch) { hardware = .touch },
                            SetupWizardOption(title: erickText("Controller first", languageKey: keyboardLanguage), selected: hardware == .controller) { hardware = .controller },
                            SetupWizardOption(title: erickText("Both", languageKey: keyboardLanguage), selected: hardware == .both) { hardware = .both }
                        ]
                    )

                    SetupWizardQuestion(
                        title: erickText("Dial preference", languageKey: keyboardLanguage),
                        options: [
                            SetupWizardOption(title: erickText("Larger targets", languageKey: keyboardLanguage), selected: targetPreference == .largerTargets) { targetPreference = .largerTargets },
                            SetupWizardOption(title: erickText("Full 8-direction layout", languageKey: keyboardLanguage), selected: targetPreference == .fullEight) { targetPreference = .fullEight }
                        ]
                    )

                    SetupWizardQuestion(
                        title: erickText("Typing style", languageKey: keyboardLanguage),
                        options: [
                            SetupWizardOption(title: erickText("Fastest path", languageKey: keyboardLanguage), selected: typingPreference == .fastest) { typingPreference = .fastest },
                            SetupWizardOption(title: erickText("Steadier confirmation", languageKey: keyboardLanguage), selected: typingPreference == .steadiest) { typingPreference = .steadiest },
                            SetupWizardOption(title: erickText("One-handed", languageKey: keyboardLanguage), selected: typingPreference == .oneHanded) { typingPreference = .oneHanded }
                        ]
                    )

                    SetupWizardQuestion(
                        title: erickText("Handedness", languageKey: keyboardLanguage),
                        options: [
                            SetupWizardOption(title: erickText("Right-handed", languageKey: keyboardLanguage), selected: handPreference == .right) { handPreference = .right },
                            SetupWizardOption(title: erickText("Left-handed", languageKey: keyboardLanguage), selected: handPreference == .left) { handPreference = .left }
                        ]
                    )

                    SetupWizardQuestion(
                        title: erickText("Accessibility default", languageKey: keyboardLanguage),
                        options: [
                            SetupWizardOption(title: erickText("Standard", languageKey: keyboardLanguage), selected: accessibilityPreference == .standard) { accessibilityPreference = .standard },
                            SetupWizardOption(title: erickText("Colorblind-safe palette", languageKey: keyboardLanguage), selected: accessibilityPreference == .colorblindSafe) { accessibilityPreference = .colorblindSafe }
                        ]
                    )

                    VStack(alignment: .leading, spacing: 6) {
                        Text(erickText("Recommended bundle", languageKey: keyboardLanguage))
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
            .navigationTitle(erickText("Setup Wizard", languageKey: keyboardLanguage))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(erickText("Cancel", languageKey: keyboardLanguage), action: onDismiss)
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(erickText("Apply", languageKey: keyboardLanguage)) {
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
    currentColorPalette: String,
    languageKey: String
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

    var summaryLines = [sixSectionDial ? erickText("6-section dial", languageKey: languageKey) : erickText("8-section dial", languageKey: languageKey)]
    switch inputMode {
    case "confirm":
        summaryLines.append(erickText("Steady Type", languageKey: languageKey))
    case "assisted":
        summaryLines.append(erickText("One-Handed", languageKey: languageKey))
    default:
        summaryLines.append(erickText("Quick Type", languageKey: languageKey))
    }
    summaryLines.append(leftHandedMode ? erickText("Left-handed mode on", languageKey: languageKey) : erickText("Right-handed default", languageKey: languageKey))
    summaryLines.append(colorblindMode ? erickText("Colorblind-safe palette enabled", languageKey: languageKey) : erickText("Standard accessibility palette", languageKey: languageKey))
    if hardware == .controller || hardware == .both {
        summaryLines.append(erickText("Controller-first starting bundle", languageKey: languageKey))
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
