import SwiftUI
import SharedKeyboard

struct CustomLayoutListView: View {
    @AppStorage("keyboard_language", store: SettingsView.appGroupDefaults) private var keyboardLanguage: String = "english"
    var onBack: () -> Void

    @State private var layouts: [CustomLayout] = []
    @State private var showCreateBlank = false
    @State private var showDuplicate = false
    @State private var newLayoutName = ""
    @State private var duplicateSource: LayoutType = .logical
    @State private var editingLayout: CustomLayout? = nil
    @State private var deleteTarget: CustomLayout? = nil

    private func manager() -> CustomLayoutManager {
        let manager = CustomLayoutManager(storage: IOSCustomLayoutStorage())
        manager.loadAll()
        return manager
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
                Text(erickText("Custom Layouts", languageKey: keyboardLanguage))
                    .font(.headline)
                Spacer()
                Menu {
                    Button(erickText("Create Blank", languageKey: keyboardLanguage)) { newLayoutName = ""; showCreateBlank = true }
                    Button(erickText("Duplicate Built-in", languageKey: keyboardLanguage)) { newLayoutName = ""; showDuplicate = true }
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
                        let manager = manager()
                        let _ = manager.save(layout: updated)
                        reloadLayouts()
                        editingLayout = nil
                    },
                    onBack: { editingLayout = nil }
                )
            } else if layouts.isEmpty {
                Spacer()
                Text(erickText("No custom layouts yet.\nTap + to create one.", languageKey: keyboardLanguage))
                    .multilineTextAlignment(.center)
                    .foregroundColor(.secondary)
                Spacer()
            } else {
                List {
                    ForEach(Array(layouts.enumerated()), id: \.element.id) { _, layout in
                        Button(action: { editingLayout = layout }) {
                            VStack(alignment: .leading) {
                                Text(layout.name).font(.body)
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
            }
        }
        .onAppear { reloadLayouts() }
        .alert(erickText("New Blank Layout", languageKey: keyboardLanguage), isPresented: $showCreateBlank) {
            TextField(erickText("Layout Name", languageKey: keyboardLanguage), text: $newLayoutName)
            Button(erickText("Create", languageKey: keyboardLanguage)) {
                let manager = manager()
                let layout = manager.createBlank(name: newLayoutName, sectionCount: 8)
                let _ = manager.save(layout: layout)
                reloadLayouts()
                editingLayout = layout
            }
            Button(erickText("Cancel", languageKey: keyboardLanguage), role: .cancel) {}
        }
        .alert(erickText("Duplicate Built-in", languageKey: keyboardLanguage), isPresented: $showDuplicate) {
            TextField(erickText("New Layout Name", languageKey: keyboardLanguage), text: $newLayoutName)
            Button(erickText("Logical", languageKey: keyboardLanguage)) {
                let manager = manager()
                let layout = manager.duplicateFromBuiltIn(sourceLayout: .logical, customName: newLayoutName, sectionCount: 8)
                let _ = manager.save(layout: layout)
                reloadLayouts()
                editingLayout = layout
            }
            Button(erickText("Efficiency", languageKey: keyboardLanguage)) {
                let manager = manager()
                let layout = manager.duplicateFromBuiltIn(sourceLayout: .efficiency, customName: newLayoutName, sectionCount: 8)
                let _ = manager.save(layout: layout)
                reloadLayouts()
                editingLayout = layout
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
            Button(erickText("Cancel", languageKey: keyboardLanguage), role: .cancel) { deleteTarget = nil }
        } message: {
            Text("\(erickText("Delete", languageKey: keyboardLanguage)) \"\(deleteTarget?.name ?? "")\"? \(erickText("This cannot be undone.", languageKey: keyboardLanguage))")
        }
    }
}

struct CustomLayoutEditorView: View {
    let layout: CustomLayout
    var onSave: (CustomLayout) -> Void
    var onBack: () -> Void

    @AppStorage("colorblind_mode", store: SettingsView.appGroupDefaults) private var colorblindMode: Bool = false
    @AppStorage("color_palette", store: SettingsView.appGroupDefaults) private var colorPalette: String = "okabe_ito"
    @AppStorage("keyboard_language", store: SettingsView.appGroupDefaults) private var keyboardLanguage: String = "english"

    @State private var name: String = ""
    @State private var selectedTab = 0
    @State private var normalChords: [String: [String]] = [:]
    @State private var shiftedChords: [String: [String]] = [:]
    @State private var singleSwipeNormal: [String: String] = [:]
    @State private var singleSwipeShifted: [String: String] = [:]

    private var currentPalette: [ColorPaletteEntry] {
        if colorblindMode {
            return ColorPaletteDefinitions.palette(for: colorPalette)
        }
        return ColorPaletteDefinitions.defaultPalette
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
            HStack {
                Button(action: onBack) {
                    Image(systemName: "arrow.left")
                        .font(.title3)
                        .padding()
                }
                Text(erickText("Edit Layout", languageKey: keyboardLanguage))
                    .font(.headline)
                Spacer()
                Button(erickText("Save", languageKey: keyboardLanguage)) { saveLayout() }
                    .padding()
            }
            .background(Color(UIColor.systemGray6))

            TextField(erickText("Layout Name", languageKey: keyboardLanguage), text: $name)
                .textFieldStyle(.roundedBorder)
                .padding(.horizontal)
                .padding(.vertical, 8)

            Picker(erickText("Section", languageKey: keyboardLanguage), selection: $selectedTab) {
                Text(erickText("Normal", languageKey: keyboardLanguage)).tag(0)
                Text(erickText("Shifted", languageKey: keyboardLanguage)).tag(1)
                Text(erickText("Single Swipe", languageKey: keyboardLanguage)).tag(2)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal)

            switch selectedTab {
            case 0: chordEditor(chords: $normalChords)
            case 1: chordEditor(chords: $shiftedChords)
            case 2: singleSwipeEditor
            default: EmptyView()
            }
        }
        .onAppear { loadFromLayout() }
    }

    private func loadFromLayout() {
        name = layout.name

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
            name: name.trimmingCharacters(in: .whitespaces).isEmpty ? erickText("Custom Layout", languageKey: keyboardLanguage) : name.trimmingCharacters(in: .whitespaces),
            normalChordMap: normalMap as! [Direction: [String]],
            shiftedChordMap: shiftedMap as! [Direction: [String]],
            singleSwipeNormalMap: singleNormalMap as! [Direction: SingleSwipeBinding],
            singleSwipeShiftedMap: singleShiftedMap as! [Direction: SingleSwipeBinding],
            sectionCount: 8
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

    private func serializeBinding(_ binding: SingleSwipeBinding) -> String {
        binding.toSerializable()
    }

    private func deserializeBinding(_ serialized: String) -> SingleSwipeBinding? {
        SingleSwipeBinding.companion.fromSerializable(s: serialized)
    }

    private func chordEditor(chords: Binding<[String: [String]]>) -> some View {
        let palette = currentPalette
        return List {
            ForEach(Array(allDirections.enumerated()), id: \.offset) { idx, dirStr in
                DisclosureGroup {
                    ForEach(0..<8, id: \.self) { i in
                        HStack {
                            Circle()
                                .fill(i < palette.count ? Color(hex: palette[i].hex) : Color.gray)
                                .frame(width: 14, height: 14)
                            Text("\(allDirections[i]) (\(i < palette.count ? erickText(palette[i].name, languageKey: keyboardLanguage) : ""))")
                                .frame(width: 100, alignment: .leading)
                                .font(.caption)
                            TextField("", text: Binding(
                                get: { chords.wrappedValue[dirStr]?[i] ?? "" },
                                set: { newValue in
                                    var values = chords.wrappedValue[dirStr] ?? Array(repeating: "", count: 8)
                                    values[i] = String(newValue.prefix(1))
                                    chords.wrappedValue[dirStr] = values
                                }
                            ))
                            .textFieldStyle(.roundedBorder)
                        }
                    }
                } label: {
                    HStack {
                        Text(directionLabels[idx]).font(.body)
                        Spacer()
                        let chars = (chords.wrappedValue[dirStr] ?? []).filter { !$0.isEmpty }.joined(separator: " ")
                        Text(chars).font(.caption).foregroundColor(.secondary)
                    }
                }
            }
        }
    }

    private var singleSwipeEditor: some View {
        List {
            Section(erickText("Normal Mode", languageKey: keyboardLanguage)) {
                ForEach(Array(allDirections.enumerated()), id: \.offset) { idx, dirStr in
                    HStack {
                        Text(directionLabels[idx]).frame(width: 80, alignment: .leading)
                        Text(singleSwipeNormal[dirStr] ?? erickText("(none)", languageKey: keyboardLanguage))
                            .foregroundColor(.secondary)
                    }
                }
            }
            Section(erickText("Shifted Mode", languageKey: keyboardLanguage)) {
                ForEach(Array(allDirections.enumerated()), id: \.offset) { idx, dirStr in
                    HStack {
                        Text(directionLabels[idx]).frame(width: 80, alignment: .leading)
                        Text(singleSwipeShifted[dirStr] ?? erickText("(none)", languageKey: keyboardLanguage))
                            .foregroundColor(.secondary)
                    }
                }
            }
        }
    }
}