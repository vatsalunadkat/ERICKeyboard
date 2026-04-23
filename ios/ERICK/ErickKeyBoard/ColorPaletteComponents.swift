import SwiftUI

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
        return hexList.enumerated().map { i, hex in
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

struct ColorPaletteOption: View {
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

struct CustomPaletteOption: View {
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

extension Color {
    func toHexString() -> String {
        guard let components = UIColor(self).cgColor.components else { return "#000000" }
        let r = Int((components.count > 0 ? components[0] : 0) * 255)
        let g = Int((components.count > 1 ? components[1] : 0) * 255)
        let b = Int((components.count > 2 ? components[2] : 0) * 255)
        return String(format: "#%02X%02X%02X", r, g, b)
    }
}