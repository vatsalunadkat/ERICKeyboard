import SwiftUI
import SharedKeyboard

struct SettingsView: View {
    static let appGroupDefaults = UserDefaults(suiteName: "group.com.vatoo.erick") ?? .standard

    @Environment(\.dismiss) private var dismiss

    @AppStorage("layout_type", store: SettingsView.appGroupDefaults) private var layoutType: String = "logical"
    @AppStorage("dark_theme", store: SettingsView.appGroupDefaults) private var darkTheme: Bool = false
    @AppStorage("theme_mode", store: SettingsView.appGroupDefaults) private var themeMode: String = "system"
    @AppStorage("colorblind_mode", store: SettingsView.appGroupDefaults) private var colorblindMode: Bool = false
    @AppStorage("color_palette", store: SettingsView.appGroupDefaults) private var colorPalette: String = "okabe_ito"
    @AppStorage("left_handed_mode", store: SettingsView.appGroupDefaults) private var leftHandedMode: Bool = false
    @AppStorage("custom_layout_id", store: SettingsView.appGroupDefaults) private var customLayoutId: String = ""
    @AppStorage("font_preference", store: SettingsView.appGroupDefaults) private var fontPreference: String = "system"
    @AppStorage("keyboard_language", store: SettingsView.appGroupDefaults) private var keyboardLanguage: String = "english"
    @AppStorage("input_mode", store: SettingsView.appGroupDefaults) private var inputMode: String = "instant"

    @State private var customLayouts: [CustomLayout] = []
    @State private var infoSheet: SettingsInfoSheet?

    var body: some View {
        Form {
            Section(header: Text(erickText("Start Here", languageKey: keyboardLanguage))) {
                SettingsBulletRow(text: erickText("Most people only need Dial Mode, Input Mode, and Accessibility.", languageKey: keyboardLanguage))
            }

            Section(
                header: Text(erickText("Language", languageKey: keyboardLanguage)),
                footer: Text(erickText("English keeps the dedicated efficiency layout. The other supported languages currently use language-aware logical maps and symbol overlays.", languageKey: keyboardLanguage))
            ) {
                Picker(erickText("Language", languageKey: keyboardLanguage), selection: $keyboardLanguage) {
                    Text(settingsLanguageSelfDisplayName(for: "english")).tag("english")
                    Text(settingsLanguageSelfDisplayName(for: "spanish")).tag("spanish")
                    Text(settingsLanguageSelfDisplayName(for: "portuguese")).tag("portuguese")
                    Text(settingsLanguageSelfDisplayName(for: "french")).tag("french")
                    Text(settingsLanguageSelfDisplayName(for: "german")).tag("german")
                    Text(settingsLanguageSelfDisplayName(for: "italian")).tag("italian")
                    Text(settingsLanguageSelfDisplayName(for: "norwegian_bokmal")).tag("norwegian_bokmal")
                    Text(settingsLanguageSelfDisplayName(for: "danish")).tag("danish")
                    Text(settingsLanguageSelfDisplayName(for: "swedish")).tag("swedish")
                    Text(settingsLanguageSelfDisplayName(for: "finnish")).tag("finnish")
                }
            }

            Section(
                header: Text(erickText("Keyboard Layout", languageKey: keyboardLanguage)),
                footer: Text(erickText("Logical is easiest to learn. Efficiency is tuned for common English letters, and other languages currently fall back to the language-aware logical layout.", languageKey: keyboardLanguage))
            ) {
                Picker(erickText("Layout Type", languageKey: keyboardLanguage), selection: $layoutType) {
                    Text(erickText("Logical (A-Z)", languageKey: keyboardLanguage)).tag("logical")
                    Text(erickText("Efficiency", languageKey: keyboardLanguage)).tag("efficiency")
                }
                .pickerStyle(.inline)

                ForEach(Array(customLayouts.enumerated()), id: \.element.id) { _, layout in
                    Button(action: {
                        customLayoutId = layout.id
                        layoutType = "custom"
                    }) {
                        HStack {
                            Image(systemName: layoutType == "custom" && customLayoutId == layout.id ? "largecircle.fill.circle" : "circle")
                                .foregroundColor(layoutType == "custom" && customLayoutId == layout.id ? .accentColor : .secondary)
                            VStack(alignment: .leading, spacing: 2) {
                                Text(layout.name)
                                    .foregroundColor(.primary)
                                Text(erickText("Custom layout", languageKey: keyboardLanguage))
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                        }
                    }
                }

                NavigationLink(erickText("Manage Custom Layouts", languageKey: keyboardLanguage)) {
                    AppCustomLayoutListView(onLayoutsChanged: reloadCustomLayouts)
                }
            }

            Section(header: Text(erickText("Appearance", languageKey: keyboardLanguage))) {
                Picker(erickText("Theme", languageKey: keyboardLanguage), selection: $themeMode) {
                    Text(erickText("System Default", languageKey: keyboardLanguage)).tag("system")
                    Text(erickText("Light", languageKey: keyboardLanguage)).tag("light")
                    Text(erickText("Dark", languageKey: keyboardLanguage)).tag("dark")
                }
                .pickerStyle(.segmented)
            }

            Section(
                header: Text(erickText("Font", languageKey: keyboardLanguage)),
                footer: Text(erickText("Use a custom font only if readability improves for you.", languageKey: keyboardLanguage))
            ) {
                appFontOption(key: "system", label: erickText("System Default", languageKey: keyboardLanguage), font: .body)
                appFontOption(key: "verdana", label: "Verdana", font: .custom("Verdana", size: 17))
                appFontOption(key: "georgia", label: "Georgia", font: .custom("Georgia", size: 17))
                appFontOption(key: "opendyslexic", label: "OpenDyslexic", font: .custom("OpenDyslexic", size: 17))
            }

            Section(
                header: Text(erickText("Custom Colors", languageKey: keyboardLanguage)),
                footer: Text(erickText("Pastel and custom palettes disable the colorblind presets.", languageKey: keyboardLanguage))
            ) {
                Button(action: { colorPalette = "okabe_ito" }) {
                    HStack {
                        Image(systemName: (colorPalette != "pastel" && colorPalette != "custom") ? "largecircle.fill.circle" : "circle")
                            .foregroundColor((colorPalette != "pastel" && colorPalette != "custom") ? .accentColor : .secondary)
                        Text(erickText("None", languageKey: keyboardLanguage))
                            .foregroundColor(.primary)
                    }
                }
                AppColorPaletteOption(
                    title: erickText("Pastel", languageKey: keyboardLanguage),
                    subtitle: erickText("Softer colors that are easier on the eyes", languageKey: keyboardLanguage),
                    palette: AppColorPaletteDefinitions.pastel,
                    selected: colorPalette == "pastel",
                    onSelect: {
                        colorPalette = "pastel"
                        if colorblindMode {
                            colorblindMode = false
                        }
                    }
                )
            }

            Section(header: Text(erickText("Accessibility", languageKey: keyboardLanguage))) {
                Toggle(
                    erickText("Enable Colorblind Mode", languageKey: keyboardLanguage),
                    isOn: Binding(
                        get: { colorblindMode },
                        set: { newValue in
                            colorblindMode = newValue
                            if newValue && (colorPalette == "pastel" || colorPalette == "custom") {
                                colorPalette = "okabe_ito"
                            }
                        }
                    )
                )

                if colorblindMode {
                    AppColorPaletteOption(
                        title: erickText("Okabe-Ito (Universal)", languageKey: keyboardLanguage),
                        subtitle: erickText("Recommended for all types of color vision deficiency", languageKey: keyboardLanguage),
                        palette: AppColorPaletteDefinitions.okabeIto,
                        selected: colorPalette == "okabe_ito",
                        onSelect: { colorPalette = "okabe_ito" }
                    )
                    AppColorPaletteOption(
                        title: erickText("Deuteranopia (Green-blind)", languageKey: keyboardLanguage),
                        subtitle: erickText("Optimized for green-blind users", languageKey: keyboardLanguage),
                        palette: AppColorPaletteDefinitions.deuteranopia,
                        selected: colorPalette == "deuteranopia",
                        onSelect: { colorPalette = "deuteranopia" }
                    )
                    AppColorPaletteOption(
                        title: erickText("Protanopia (Red-blind)", languageKey: keyboardLanguage),
                        subtitle: erickText("Optimized for red-blind users", languageKey: keyboardLanguage),
                        palette: AppColorPaletteDefinitions.protanopia,
                        selected: colorPalette == "protanopia",
                        onSelect: { colorPalette = "protanopia" }
                    )
                    AppColorPaletteOption(
                        title: erickText("Tritanopia (Blue-blind)", languageKey: keyboardLanguage),
                        subtitle: erickText("Optimized for blue-blind users", languageKey: keyboardLanguage),
                        palette: AppColorPaletteDefinitions.tritanopia,
                        selected: colorPalette == "tritanopia",
                        onSelect: { colorPalette = "tritanopia" }
                    )
                }

                Toggle(erickText("Left-Handed Mode", languageKey: keyboardLanguage), isOn: $leftHandedMode)
            }

            Section(
                header: Text(erickText("Input Mode", languageKey: keyboardLanguage)),
                footer: Text(erickText("Quick Type is fastest. Steady Type is more deliberate. One-Handed locks the left-side row.", languageKey: keyboardLanguage))
            ) {
                Button(action: { inputMode = "instant" }) {
                    HStack(alignment: .top, spacing: 8) {
                        Image(systemName: inputMode == "instant" ? "largecircle.fill.circle" : "circle")
                            .foregroundColor(inputMode == "instant" ? .accentColor : .secondary)
                            .padding(.top, 2)
                        VStack(alignment: .leading, spacing: 2) {
                            Text(erickText("Quick Type", languageKey: keyboardLanguage))
                                .foregroundColor(.primary)
                            Text(erickText("Type at full speed. Characters appear as soon as you release either dial.", languageKey: keyboardLanguage))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                    }
                }
                Button(action: { inputMode = "confirm" }) {
                    HStack(alignment: .top, spacing: 8) {
                        Image(systemName: inputMode == "confirm" ? "largecircle.fill.circle" : "circle")
                            .foregroundColor(inputMode == "confirm" ? .accentColor : .secondary)
                            .padding(.top, 2)
                        VStack(alignment: .leading, spacing: 2) {
                            Text(erickText("Steady Type", languageKey: keyboardLanguage))
                                .foregroundColor(.primary)
                            Text(erickText("Take your time. Characters appear only after both dials return to center.", languageKey: keyboardLanguage))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                    }
                }
                Button(action: { inputMode = "assisted" }) {
                    HStack(alignment: .top, spacing: 8) {
                        Image(systemName: inputMode == "assisted" ? "largecircle.fill.circle" : "circle")
                            .foregroundColor(inputMode == "assisted" ? .accentColor : .secondary)
                            .padding(.top, 2)
                        VStack(alignment: .leading, spacing: 2) {
                            Text(erickText("One-Handed", languageKey: keyboardLanguage))
                                .foregroundColor(.primary)
                            Text(erickText("Type with one hand. Lock a direction on the left dial, then swipe the right dial to type.", languageKey: keyboardLanguage))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                    }
                }
            }

            Section(header: Text(erickText("Privacy & Security", languageKey: keyboardLanguage))) {
                Button {
                    infoSheet = .privacy
                } label: {
                    HStack {
                        Text(erickText("View Privacy Details", languageKey: keyboardLanguage))
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
            }
        }
        .navigationTitle(erickText("Keyboard Settings", languageKey: keyboardLanguage))
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(erickText("Back", languageKey: keyboardLanguage)) {
                    dismiss()
                }
            }
        }
        .onAppear {
            reloadCustomLayouts()
            darkTheme = themeMode == "dark"
        }
        .onChange(of: themeMode) { newValue in
            darkTheme = newValue == "dark"
        }
        .sheet(item: $infoSheet) { sheet in
            SettingsInfoSheetView(sheet: sheet, languageKey: keyboardLanguage)
        }
    }

    private func reloadCustomLayouts() {
        let manager = CustomLayoutManager(storage: IOSCustomLayoutStorage())
        manager.loadAll()
        customLayouts = manager.getAll()
    }

    @ViewBuilder
    private func appFontOption(key: String, label: String, font: Font) -> some View {
        Button(action: { fontPreference = key }) {
            HStack {
                Image(systemName: fontPreference == key ? "largecircle.fill.circle" : "circle")
                    .foregroundColor(fontPreference == key ? .accentColor : .secondary)
                VStack(alignment: .leading, spacing: 4) {
                    Text(label)
                        .foregroundColor(.primary)
                    Text(erickText("The quick brown fox", languageKey: keyboardLanguage))
                        .font(font)
                        .foregroundColor(.secondary)
                }
            }
        }
    }
}

private enum SettingsInfoSheet: String, Identifiable {
    case privacy

    var id: String { rawValue }
}

private struct SettingsBulletRow: View {
    let text: String

    var body: some View {
        HStack(alignment: .top, spacing: 10) {
            Text("•")
                .fontWeight(.bold)
            Text(text)
                .fixedSize(horizontal: false, vertical: true)
        }
        .foregroundColor(.secondary)
    }
}

private struct SettingsInfoSheetView: View {
    let sheet: SettingsInfoSheet
    let languageKey: String

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    switch sheet {
                    case .privacy:
                        Text(erickText("ERICKeyboard keeps your typing on your device.", languageKey: languageKey))
                            .font(.body)
                        SettingsBulletRow(text: erickText("It does not collect the text you type", languageKey: languageKey))
                        SettingsBulletRow(text: erickText("It does not store passwords or personal data", languageKey: languageKey))
                        SettingsBulletRow(text: erickText("It does not transmit typing data from your device", languageKey: languageKey))
                        SettingsBulletRow(text: erickText("It stores only keyboard preferences locally", languageKey: languageKey))
                        SettingsBulletRow(text: erickText("It requests no internet permission for typing data", languageKey: languageKey))
                        SettingsBulletRow(text: erickText("The project is open source for inspection", languageKey: languageKey))
                    }
                }
                .padding()
            }
            .navigationTitle(erickText("Privacy & Security", languageKey: languageKey))
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

struct AppSettingsView_Previews: PreviewProvider {
    static var previews: some View {
        SettingsView()
    }
}

private struct AppColorPaletteEntry {
    let name: String
    let hex: String
}

private struct AppColorPaletteDefinitions {
    static let defaultPalette: [AppColorPaletteEntry] = [
        .init(name: "Red", hex: "#E60012"),
        .init(name: "Orange", hex: "#F39800"),
        .init(name: "Yellow", hex: "#FFF100"),
        .init(name: "Green", hex: "#009944"),
        .init(name: "Blue", hex: "#0068B7"),
        .init(name: "Indigo", hex: "#1D2088"),
        .init(name: "Violet", hex: "#920783"),
        .init(name: "Black", hex: "#000000")
    ]

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

    static func palette(for key: String) -> [AppColorPaletteEntry] {
        switch key {
        case "okabe_ito":
            return okabeIto
        case "deuteranopia":
            return deuteranopia
        case "protanopia":
            return protanopia
        case "tritanopia":
            return tritanopia
        case "pastel":
            return pastel
        default:
            return defaultPalette
        }
    }
}

private struct AppColorPaletteOption: View {
    @AppStorage("keyboard_language", store: SettingsView.appGroupDefaults) private var keyboardLanguage: String = "english"

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
                        Text(title)
                            .foregroundColor(.primary)
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
                                Text(erickText(entry.name, languageKey: keyboardLanguage))
                                    .font(.system(size: 8))
                                    .foregroundColor(.secondary)
                            }
                        }
                    }
                    .padding(.leading, 28)
                }
            }
        }
        .buttonStyle(.plain)
    }
}

struct AppCustomLayoutListView: View {
    @AppStorage("keyboard_language", store: SettingsView.appGroupDefaults) private var keyboardLanguage: String = "english"

    var onLayoutsChanged: () -> Void

    @State private var layouts: [CustomLayout] = []
    @State private var showCreateBlank = false
    @State private var showDuplicate = false
    @State private var newLayoutName = ""
    @State private var deleteTarget: CustomLayout? = nil

    private func manager() -> CustomLayoutManager {
        let manager = CustomLayoutManager(storage: IOSCustomLayoutStorage())
        manager.loadAll()
        return manager
    }

    private func reloadLayouts() {
        layouts = manager().getAll()
        onLayoutsChanged()
    }

    var body: some View {
        List {
            if layouts.isEmpty {
                Text(erickText("No custom layouts yet.\nTap + to create one.", languageKey: keyboardLanguage))
                    .foregroundColor(.secondary)
                    .padding()
            }

            ForEach(Array(layouts.enumerated()), id: \.element.id) { _, layout in
                NavigationLink {
                    AppCustomLayoutEditorView(layout: layout, onSave: { updated in
                        let manager = manager()
                        let _ = manager.save(layout: updated)
                        reloadLayouts()
                    })
                } label: {
                    VStack(alignment: .leading) {
                        Text(layout.name)
                            .font(.body)
                        let count = layout.normalChordMap.values.flatMap { ($0 as! [String]) }.filter { !$0.isEmpty }.count
                        Text("\(count) \(erickText("characters mapped", languageKey: keyboardLanguage))")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .swipeActions(edge: .trailing) {
                    Button(role: .destructive) { deleteTarget = layout } label: {
                        Label(erickText("Delete", languageKey: keyboardLanguage), systemImage: "trash")
                    }
                }
            }
        }
        .navigationTitle(erickText("Custom Layouts", languageKey: keyboardLanguage))
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Menu {
                    Button(erickText("Create Blank", languageKey: keyboardLanguage)) {
                        newLayoutName = ""
                        showCreateBlank = true
                    }
                    Button(erickText("Duplicate Built-in", languageKey: keyboardLanguage)) {
                        newLayoutName = ""
                        showDuplicate = true
                    }
                } label: {
                    Image(systemName: "plus")
                }
            }
        }
        .onAppear { reloadLayouts() }
        .alert(erickText("New Blank Layout", languageKey: keyboardLanguage), isPresented: $showCreateBlank) {
            TextField(erickText("Layout Name", languageKey: keyboardLanguage), text: $newLayoutName)
            Button(erickText("Create", languageKey: keyboardLanguage)) {
                let manager = manager()
                let layout = manager.createBlank(name: newLayoutName)
                let _ = manager.save(layout: layout)
                reloadLayouts()
            }
            Button(erickText("Cancel", languageKey: keyboardLanguage), role: .cancel) {}
        }
        .alert(erickText("Duplicate Built-in", languageKey: keyboardLanguage), isPresented: $showDuplicate) {
            TextField(erickText("New Layout Name", languageKey: keyboardLanguage), text: $newLayoutName)
            Button(erickText("Logical", languageKey: keyboardLanguage)) {
                let manager = manager()
                let layout = manager.duplicateFromBuiltIn(sourceLayout: .logical, customName: newLayoutName)
                let _ = manager.save(layout: layout)
                reloadLayouts()
            }
            Button(erickText("Efficiency", languageKey: keyboardLanguage)) {
                let manager = manager()
                let layout = manager.duplicateFromBuiltIn(sourceLayout: .efficiency, customName: newLayoutName)
                let _ = manager.save(layout: layout)
                reloadLayouts()
            }
            Button(erickText("Cancel", languageKey: keyboardLanguage), role: .cancel) {}
        }
        .alert(erickText("Delete Layout?", languageKey: keyboardLanguage), isPresented: Binding(
            get: { deleteTarget != nil },
            set: { if !$0 { deleteTarget = nil } }
        )) {
            Button(erickText("Delete", languageKey: keyboardLanguage), role: .destructive) {
                if let target = deleteTarget {
                    let manager = manager()
                    manager.delete(id: target.id)
                    reloadLayouts()
                    deleteTarget = nil
                }
            }
            Button(erickText("Cancel", languageKey: keyboardLanguage), role: .cancel) {
                deleteTarget = nil
            }
        } message: {
            Text("\(erickText("Delete", languageKey: keyboardLanguage)) \"\(deleteTarget?.name ?? "")\"? \(erickText("This cannot be undone.", languageKey: keyboardLanguage))")
        }
    }
}

struct AppCustomLayoutEditorView: View {
    @AppStorage("colorblind_mode", store: SettingsView.appGroupDefaults) private var colorblindMode: Bool = false
    @AppStorage("color_palette", store: SettingsView.appGroupDefaults) private var colorPalette: String = "okabe_ito"
    @AppStorage("keyboard_language", store: SettingsView.appGroupDefaults) private var keyboardLanguage: String = "english"

    let layout: CustomLayout
    var onSave: (CustomLayout) -> Void

    @State private var name: String = ""
    @State private var selectedTab = 0
    @State private var normalChords: [String: [String]] = [:]
    @State private var shiftedChords: [String: [String]] = [:]

    private var currentPalette: [AppColorPaletteEntry] {
        colorblindMode ? AppColorPaletteDefinitions.palette(for: colorPalette) : AppColorPaletteDefinitions.defaultPalette
    }

    private let allDirections = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"]

    private var directionLabels: [String] {
        [
            "N (\(erickText("Up", languageKey: keyboardLanguage)))",
            "NE",
            "E (\(erickText("Right", languageKey: keyboardLanguage)))",
            "SE",
            "S (\(erickText("Down", languageKey: keyboardLanguage)))",
            "SW",
            "W (\(erickText("Left", languageKey: keyboardLanguage)))",
            "NW"
        ]
    }

    var body: some View {
        VStack(spacing: 0) {
            TextField(erickText("Layout Name", languageKey: keyboardLanguage), text: $name)
                .textFieldStyle(.roundedBorder)
                .padding(.horizontal)
                .padding(.vertical, 8)

            VStack(alignment: .leading, spacing: 6) {
                Text(erickText("Edit one layer at a time", languageKey: keyboardLanguage))
                    .font(.headline)
                Text(selectedTab == 0 ? erickText("Normal is the everyday map. Start here first.", languageKey: keyboardLanguage) : erickText("Shifted is only for shifted typing. Change it after the normal layer feels right.", languageKey: keyboardLanguage))
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding()
            .background(Color(uiColor: .secondarySystemBackground))
            .cornerRadius(12)
            .padding(.horizontal)
            .padding(.bottom, 8)

            Picker(erickText("Map", languageKey: keyboardLanguage), selection: $selectedTab) {
                Text(erickText("Normal", languageKey: keyboardLanguage)).tag(0)
                Text(erickText("Shifted", languageKey: keyboardLanguage)).tag(1)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal)

            switch selectedTab {
            case 0:
                appChordEditor(chords: $normalChords)
            case 1:
                appChordEditor(chords: $shiftedChords)
            default:
                EmptyView()
            }
        }
        .navigationTitle(erickText("Edit Layout", languageKey: keyboardLanguage))
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(erickText("Save", languageKey: keyboardLanguage)) {
                    saveLayout()
                }
            }
        }
        .onAppear { loadFromLayout() }
    }

    private func loadFromLayout() {
        name = layout.name
        for direction in allDirections {
            let mappedDirection = kmpDirection(from: direction)
            normalChords[direction] = (layout.normalChordMap[mappedDirection] as? [String]) ?? Array(repeating: "", count: 8)
            shiftedChords[direction] = (layout.shiftedChordMap[mappedDirection] as? [String]) ?? Array(repeating: "", count: 8)
        }
    }

    private func saveLayout() {
        let normalMap = NSMutableDictionary()
        let shiftedMap = NSMutableDictionary()

        for direction in allDirections {
            let mappedDirection = kmpDirection(from: direction)
            normalMap[mappedDirection] = normalChords[direction] ?? Array(repeating: "", count: 8)
            shiftedMap[mappedDirection] = shiftedChords[direction] ?? Array(repeating: "", count: 8)
        }

        let trimmedName = name.trimmingCharacters(in: .whitespacesAndNewlines)
        let updated = CustomLayout(
            id: layout.id,
            name: trimmedName.isEmpty ? erickText("Custom Layout", languageKey: keyboardLanguage) : trimmedName,
            normalChordMap: normalMap as! [Direction: [String]],
            shiftedChordMap: shiftedMap as! [Direction: [String]],
            singleSwipeNormalMap: layout.singleSwipeNormalMap,
            singleSwipeShiftedMap: layout.singleSwipeShiftedMap
        )
        onSave(updated)
    }

    private func kmpDirection(from direction: String) -> Direction {
        switch direction {
        case "N":
            return .n
        case "NE":
            return .ne
        case "E":
            return .e
        case "SE":
            return .se
        case "S":
            return .s
        case "SW":
            return .sw
        case "W":
            return .w
        case "NW":
            return .nw
        default:
            return .none
        }
    }

    private func appChordEditor(chords: Binding<[String: [String]]>) -> some View {
        let palette = currentPalette
        return List {
            ForEach(Array(allDirections.enumerated()), id: \.offset) { index, direction in
                DisclosureGroup {
                    ForEach(0..<8, id: \.self) { entryIndex in
                        HStack {
                            Circle()
                                .fill(entryIndex < palette.count ? Color(hex: palette[entryIndex].hex) : Color.gray)
                                .frame(width: 14, height: 14)
                            Text("\(allDirections[entryIndex]) (\(entryIndex < palette.count ? erickText(palette[entryIndex].name, languageKey: keyboardLanguage) : ""))")
                                .frame(width: 100, alignment: .leading)
                                .font(.caption)
                            TextField("", text: Binding(
                                get: { chords.wrappedValue[direction]?[entryIndex] ?? "" },
                                set: { newValue in
                                    var values = chords.wrappedValue[direction] ?? Array(repeating: "", count: 8)
                                    values[entryIndex] = String(newValue.prefix(1))
                                    chords.wrappedValue[direction] = values
                                }
                            ))
                            .textFieldStyle(.roundedBorder)
                        }
                    }
                } label: {
                    HStack {
                        Text(directionLabels[index])
                            .font(.body)
                        Spacer()
                        let characters = (chords.wrappedValue[direction] ?? []).filter { !$0.isEmpty }.joined(separator: " ")
                        Text(characters)
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
            }
        }
    }
}

private func settingsLanguageSelfDisplayName(for keyboardLanguage: String) -> String {
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

extension Color {
    init(hex: String) {
        let hexValue = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hexValue).scanHexInt64(&int)
        let a: UInt64
        let r: UInt64
        let g: UInt64
        let b: UInt64

        switch hexValue.count {
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
