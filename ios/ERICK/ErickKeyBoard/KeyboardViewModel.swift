import UIKit
import SwiftUI
import Combine

struct KeyboardPreviewItem: Identifiable {
    let id: Int
    let direction: WheelDirection
    let text: String
    let color: Color
}

class KeyboardViewModel: ObservableObject {
    @Published var previewText: String = ""
    @Published var previewItems: [KeyboardPreviewItem] = []
    @Published var highlightedPreviewIndex: Int?
    @Published var leftDirection: WheelDirection = .none
    @Published var rightDirection: WheelDirection = .none
    @Published var keyboardMode: WheelMode = .normal
    @Published var isEfficiency: Bool = false
    @Published var colorPaletteKey: String = "default"
    @Published var isLeftHanded: Bool = false
    @Published var sixSectionMode: Bool = false
    @Published var isDarkMode: Bool = false
    @Published var fontPreference: String = "system"
    @Published var customNormalSections: [[String]]? = nil
    @Published var customShiftedSections: [[String]]? = nil
    @Published var suggestions: [String] = []
    @Published var suggestionContextLabel: String = ""
    @Published var bothDialsAtHome: Bool = true
    @Published var lockedLeftDirection: WheelDirection = .none
    @Published var paletteRefreshToken: Int = 0
    @Published var leftControllerStickNormalized: (x: Float, y: Float) = (0, 0)
    @Published var rightControllerStickNormalized: (x: Float, y: Float) = (0, 0)

    var resolvedFont: Font {
        switch fontPreference {
        case "verdana": return .custom("Verdana", size: 14)
        case "georgia": return .custom("Georgia", size: 14)
        case "opendyslexic": return .custom("OpenDyslexic", size: 14)
        default: return .system(size: 14)
        }
    }

    func resolvedUIFont(size: CGFloat) -> UIFont {
        switch fontPreference {
        case "verdana": return UIFont(name: "Verdana", size: size) ?? .systemFont(ofSize: size)
        case "georgia": return UIFont(name: "Georgia", size: size) ?? .systemFont(ofSize: size)
        case "opendyslexic": return UIFont(name: "OpenDyslexic", size: size) ?? .systemFont(ofSize: size)
        default: return .systemFont(ofSize: size)
        }
    }
}