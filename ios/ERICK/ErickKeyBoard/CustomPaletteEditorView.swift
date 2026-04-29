import SwiftUI

struct CustomPaletteEditorView: View {
    @AppStorage("keyboard_language", store: SettingsView.appGroupDefaults) private var keyboardLanguage: String = "english"
    @Binding var customColors: String
    var onBack: () -> Void

    private let directionLabels = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"]

    @State private var colors: [String] = []
    @State private var selectedIndex: Int = 0
    @State private var pickerColor: Color = .red

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button(action: onBack) {
                    Image(systemName: "arrow.left")
                        .font(.title3)
                        .padding()
                }
                Text(erickText("Custom Palette", languageKey: keyboardLanguage))
                    .font(.headline)
                Spacer()
                Button(erickText("Save", languageKey: keyboardLanguage)) {
                    customColors = colors.joined(separator: ",")
                    onBack()
                }
                .padding()
            }
            .background(Color(UIColor.systemGray6))

            ScrollView {
                VStack(spacing: 16) {
                    Text(erickText("Tap a slot to edit its color:", languageKey: keyboardLanguage))
                        .font(.subheadline)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    HStack(spacing: 8) {
                        ForEach(0..<8, id: \.self) { index in
                            VStack(spacing: 2) {
                                RoundedRectangle(cornerRadius: 6)
                                    .fill(index < colors.count ? Color(hex: colors[index]) : Color.gray)
                                    .frame(width: 36, height: 36)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 6)
                                            .stroke(
                                                index == selectedIndex ? Color.accentColor : Color.secondary.opacity(0.3),
                                                lineWidth: index == selectedIndex ? 3 : 1
                                            )
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

                    RoundedRectangle(cornerRadius: 8)
                        .fill(selectedIndex < colors.count ? Color(hex: colors[selectedIndex]) : Color.gray)
                        .frame(height: 48)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.secondary.opacity(0.3), lineWidth: 1)
                        )

                    ColorPicker(erickText("Pick Color", languageKey: keyboardLanguage), selection: $pickerColor, supportsOpacity: false)
                        .onChange(of: pickerColor) { newColor in
                            if selectedIndex < colors.count {
                                colors[selectedIndex] = newColor.toHexString()
                            }
                        }

                    HStack {
                        Text("#")
                        TextField(erickText("Hex", languageKey: keyboardLanguage), text: Binding(
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