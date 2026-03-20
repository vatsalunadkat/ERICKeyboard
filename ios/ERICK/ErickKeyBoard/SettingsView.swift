//
//  SettingsView.swift
//  ErickKeyBoard
//
//  Created by ERICK on 2026/3/9.
//

import SwiftUI
import SharedKeyboard

struct SettingsView: View {
    private static let appGroupDefaults = UserDefaults(suiteName: "group.com.vatoo.erick") ?? .standard

    @AppStorage("layout_type", store: SettingsView.appGroupDefaults) private var layoutType: String = "logical"
    @AppStorage("dark_theme", store: SettingsView.appGroupDefaults) private var darkTheme: Bool = false
    @AppStorage("colorblind_mode", store: SettingsView.appGroupDefaults) private var colorblindMode: Bool = false
    @AppStorage("color_palette", store: SettingsView.appGroupDefaults) private var colorPalette: String = "okabe_ito"
    @AppStorage("left_handed_mode", store: SettingsView.appGroupDefaults) private var leftHandedMode: Bool = false
    @AppStorage("custom_layout_id", store: SettingsView.appGroupDefaults) private var customLayoutId: String = ""
    
    // Action closure when the user wants to dismiss settings from Keyboard Extension
    var onClose: (() -> Void)? = nil

    @State private var showCustomLayoutList = false

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
            
            if showCustomLayoutList {
                CustomLayoutListView(onBack: { showCustomLayoutList = false })
            } else {
                mainSettingsForm
            }
        }
    }

    private var mainSettingsForm: some View {
            // Content
            Form {
                // Layout Section
                Section(header: Text("Keyboard Layout")) {
                    Picker("Layout Type", selection: $layoutType) {
                        Text("Logical (A–Z)").tag("logical")
                        Text("Efficiency").tag("efficiency")
                    }
                    .pickerStyle(.inline)

                    // Custom layouts
                    let storage = IOSCustomLayoutStorage()
                    let manager = CustomLayoutManager(storage: storage)
                    let _ = manager.loadAll()
                    let customs = manager.getAll()
                    ForEach(Array(customs.enumerated()), id: \.element.id) { _, cl in
                        Button(action: {
                            customLayoutId = cl.id
                            layoutType = "custom"
                        }) {
                            HStack {
                                Image(systemName: layoutType == "custom" && customLayoutId == cl.id
                                      ? "largecircle.fill.circle" : "circle")
                                    .foregroundColor(layoutType == "custom" && customLayoutId == cl.id ? .accentColor : .secondary)
                                VStack(alignment: .leading) {
                                    Text(cl.name).foregroundColor(.primary)
                                    Text("Custom layout").font(.caption).foregroundColor(.secondary)
                                }
                            }
                        }
                    }

                    Button(action: { showCustomLayoutList = true }) {
                        HStack {
                            Image(systemName: "pencil.circle")
                            Text("Manage Custom Layouts")
                        }
                    }
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
