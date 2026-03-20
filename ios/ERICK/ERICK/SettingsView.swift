//
//  SettingsView.swift
//  ERICK
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
    
    @Environment(\.dismiss) var dismiss

    @State private var customLayouts: [CustomLayout] = []
    @State private var showCustomLayoutManager = false

    private func reloadCustomLayouts() {
        let m = CustomLayoutManager(storage: IOSCustomLayoutStorage())
        m.loadAll()
        customLayouts = m.getAll()
    }

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

                    // Custom layouts
                    ForEach(Array(customLayouts.enumerated()), id: \.element.id) { _, cl in
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

                    NavigationLink("Manage Custom Layouts") {
                        AppCustomLayoutListView(onLayoutsChanged: { reloadCustomLayouts() })
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
            .onAppear { reloadCustomLayouts() }
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
        case "okabe_ito": return okabeIto
        case "deuteranopia": return deuteranopia
        case "protanopia": return protanopia
        case "tritanopia": return tritanopia
        case "pastel": return pastel
        default: return defaultPalette
        }
    }
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

// MARK: - App Custom Layout List View

struct AppCustomLayoutListView: View {
    var onLayoutsChanged: () -> Void

    @State private var layouts: [CustomLayout] = []
    @State private var showCreateBlank = false
    @State private var showDuplicate = false
    @State private var newLayoutName = ""
    @State private var deleteTarget: CustomLayout? = nil

    private func manager() -> CustomLayoutManager {
        let m = CustomLayoutManager(storage: IOSCustomLayoutStorage())
        m.loadAll()
        return m
    }

    private func reloadLayouts() {
        layouts = manager().getAll()
        onLayoutsChanged()
    }

    var body: some View {
        List {
            if layouts.isEmpty {
                Text("No custom layouts yet.\nTap + to create one.")
                    .foregroundColor(.secondary)
                    .padding()
            }
            ForEach(Array(layouts.enumerated()), id: \.element.id) { _, cl in
                NavigationLink {
                    AppCustomLayoutEditorView(layout: cl, onSave: { updated in
                        let m = manager()
                        let _ = m.save(layout: updated)
                        reloadLayouts()
                    })
                } label: {
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
        .navigationTitle("Custom Layouts")
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Menu {
                    Button("Create Blank") { newLayoutName = ""; showCreateBlank = true }
                    Button("Duplicate Built-in") { newLayoutName = ""; showDuplicate = true }
                } label: {
                    Image(systemName: "plus")
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
            }
            Button("Efficiency") {
                let m = manager()
                let layout = m.duplicateFromBuiltIn(sourceLayout: .efficiency, customName: newLayoutName)
                let _ = m.save(layout: layout)
                reloadLayouts()
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

// MARK: - App Custom Layout Editor View

struct AppCustomLayoutEditorView: View {
    let layout: CustomLayout
    var onSave: (CustomLayout) -> Void

    @AppStorage("colorblind_mode", store: SettingsView.appGroupDefaults) private var colorblindMode: Bool = false
    @AppStorage("color_palette", store: SettingsView.appGroupDefaults) private var colorPalette: String = "okabe_ito"

    @State private var name: String = ""
    @State private var selectedTab = 0
    @State private var normalChords: [String: [String]] = [:]
    @State private var shiftedChords: [String: [String]] = [:]

    private var currentPalette: [AppColorPaletteEntry] {
        if colorblindMode {
            return AppColorPaletteDefinitions.palette(for: colorPalette)
        } else {
            return AppColorPaletteDefinitions.defaultPalette
        }
    }

    private let allDirections = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"]
    private let dirLabels = ["N (Up)", "NE", "E (Right)", "SE", "S (Down)", "SW", "W (Left)", "NW"]

    var body: some View {
        VStack(spacing: 0) {
            TextField("Layout Name", text: $name)
                .textFieldStyle(.roundedBorder)
                .padding(.horizontal)
                .padding(.vertical, 8)

            Picker("Section", selection: $selectedTab) {
                Text("Normal").tag(0)
                Text("Shifted").tag(1)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal)

            switch selectedTab {
            case 0: appChordEditor(chords: $normalChords)
            case 1: appChordEditor(chords: $shiftedChords)
            default: EmptyView()
            }
        }
        .navigationTitle("Edit Layout")
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button("Save") { saveLayout() }
            }
        }
        .onAppear { loadFromLayout() }
    }

    private func loadFromLayout() {
        name = layout.name
        for dirStr in allDirections {
            let dir = kmpDirection(from: dirStr)
            normalChords[dirStr] = (layout.normalChordMap[dir] as? [String]) ?? Array(repeating: "", count: 8)
            shiftedChords[dirStr] = (layout.shiftedChordMap[dir] as? [String]) ?? Array(repeating: "", count: 8)
        }
    }

    private func saveLayout() {
        let normalMap = NSMutableDictionary()
        let shiftedMap = NSMutableDictionary()

        for dirStr in allDirections {
            let dir = kmpDirection(from: dirStr)
            normalMap[dir] = normalChords[dirStr] ?? Array(repeating: "", count: 8)
            shiftedMap[dir] = shiftedChords[dirStr] ?? Array(repeating: "", count: 8)
        }

        let updated = CustomLayout(
            id: layout.id,
            name: name.trimmingCharacters(in: .whitespaces).isEmpty ? "Custom Layout" : name.trimmingCharacters(in: .whitespaces),
            normalChordMap: normalMap as! [Direction : [String]],
            shiftedChordMap: shiftedMap as! [Direction : [String]],
            singleSwipeNormalMap: layout.singleSwipeNormalMap,
            singleSwipeShiftedMap: layout.singleSwipeShiftedMap
        )
        onSave(updated)
    }

    private func kmpDirection(from str: String) -> Direction {
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

    private func appChordEditor(chords: Binding<[String: [String]]>) -> some View {
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
