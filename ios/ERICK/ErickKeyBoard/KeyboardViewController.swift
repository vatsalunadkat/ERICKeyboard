import UIKit
import SwiftUI
import Combine
import SharedKeyboard // 🚨 引入我们刚刚打包好的 KMP 大脑！

struct KeyboardPreviewItem: Identifiable {
    let id: Int
    let text: String
    let color: Color
}

// 1. 状态桥梁：由于 SwiftUI 是响应式的，我们需要一个 ViewModel 来实时更新预览文字
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
    @Published var isDarkMode: Bool = false
    @Published var fontPreference: String = "system"
    @Published var customNormalSections: [[String]]? = nil  // 8 directions × 8 chars each
    @Published var customShiftedSections: [[String]]? = nil

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

// 2. SwiftUI 键盘容器：把左右两个摇杆横向排列
struct KeyboardContainerView: View {
    @ObservedObject var viewModel: KeyboardViewModel
    // 闭包回调参数：dx, dy, isLeft, isDownOrMove, isUp
    var onTouch: (Float, Float, Bool, Bool, Bool) -> Void
    
    @State private var showSettings = false // 控制设置页面的显示

    var body: some View {
        ZStack(alignment: .top) {
            (viewModel.isDarkMode ? Color(hex: "#1E1E1E") : Color(hex: "#ECEFF1"))
                .ignoresSafeArea()

            GeometryReader { geometry in
                let horizontalPadding: CGFloat = 16
                let controlSpacing: CGFloat = 18
                let topInset: CGFloat = 52
                let bottomInset: CGFloat = 8
                let availableWidth = geometry.size.width - (horizontalPadding * 2) - controlSpacing
                let availableHeight = geometry.size.height - topInset - bottomInset
                let rightSize = min(availableHeight, availableWidth / 2.08)
                let leftSize = rightSize * 1.08
                let totalControlsWidth = leftSize + rightSize + controlSpacing

                HStack(spacing: controlSpacing) {
                    JoystickView(
                        isRightSide: viewModel.isLeftHanded,
                        activeDirection: viewModel.leftDirection,
                        keyboardMode: viewModel.keyboardMode,
                        isEfficiency: viewModel.isEfficiency,
                        colorPaletteKey: viewModel.colorPaletteKey,
                        fontPreference: viewModel.fontPreference,
                        customNormalSections: viewModel.customNormalSections,
                        customShiftedSections: viewModel.customShiftedSections
                    ) { dx, dy, isDownOrMove, isUp in
                        onTouch(dx, dy, true, isDownOrMove, isUp)
                    }
                    .frame(width: leftSize, height: leftSize)

                    JoystickView(
                        isRightSide: !viewModel.isLeftHanded,
                        activeDirection: viewModel.rightDirection,
                        keyboardMode: viewModel.keyboardMode,
                        isEfficiency: viewModel.isEfficiency,
                        colorPaletteKey: viewModel.colorPaletteKey,
                        fontPreference: viewModel.fontPreference,
                        customNormalSections: viewModel.customNormalSections,
                        customShiftedSections: viewModel.customShiftedSections
                    ) { dx, dy, isDownOrMove, isUp in
                        onTouch(dx, dy, false, isDownOrMove, isUp)
                    }
                    .frame(width: rightSize, height: rightSize)
                    .offset(x: -6)
                }
                .frame(width: totalControlsWidth, height: availableHeight, alignment: .center)
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
                .padding(.top, topInset)
                .padding(.bottom, bottomInset)
                .padding(.horizontal, horizontalPadding)
            }
            .allowsHitTesting(!showSettings)

            if !viewModel.previewItems.isEmpty {
                KeyboardPreviewBar(
                    items: viewModel.previewItems,
                    highlightedIndex: viewModel.highlightedPreviewIndex,
                    isDarkMode: viewModel.isDarkMode,
                    fontPreference: viewModel.fontPreference
                )
                    .padding(.top, 8)
            }

            HStack {
                // Shift / Caps Lock indicator
                if viewModel.keyboardMode == .shifted {
                    Text("⬆ Shift")
                        .font(.system(size: 13, weight: .semibold))
                        .foregroundColor(viewModel.isDarkMode ? .white : Color(hex: "#333333"))
                        .padding(.horizontal, 6)
                        .padding(.top, 4)
                        .transition(.opacity)
                        .accessibilityLabel("Shift mode active")
                } else if viewModel.keyboardMode == .capsLocked {
                    Text("⬆⬆ CAPS")
                        .font(.system(size: 13, weight: .bold))
                        .foregroundColor(.white)
                        .padding(.horizontal, 6)
                        .padding(.vertical, 2)
                        .background(
                            RoundedRectangle(cornerRadius: 4)
                                .fill(Color.red)
                        )
                        .padding(.top, 4)
                        .transition(.opacity)
                        .accessibilityLabel("Caps Lock active")
                }

                Spacer()
                Button(action: {
                    withAnimation {
                        showSettings = true
                    }
                }) {
                    Image(systemName: "gear")
                        .font(.title2)
                        .foregroundColor(.gray)
                        .padding(.horizontal)
                        .padding(.top, 4)
                }
            }
            .padding(.leading, 8)
            .animation(.easeInOut(duration: 0.15), value: viewModel.keyboardMode)

            // 覆盖设置页面
            if showSettings {
                SettingsView(onClose: {
                    withAnimation {
                        showSettings = false
                    }
                })
                .transition(.move(edge: .bottom))
                .zIndex(1)
            }
        }
    }
}

private struct KeyboardPreviewBar: View {
    let items: [KeyboardPreviewItem]
    let highlightedIndex: Int?
    var isDarkMode: Bool = false
    var fontPreference: String = "system"

    private func resolvedPreviewFont(size: CGFloat, weight: Font.Weight) -> Font {
        switch fontPreference {
        case "verdana":
            return .custom("Verdana", size: size).weight(weight)
        case "georgia":
            return .custom("Georgia", size: size).weight(weight)
        case "opendyslexic":
            return .custom("OpenDyslexic", size: size).weight(weight)
        default:
            return .system(size: size, weight: weight, design: .rounded)
        }
    }

    var body: some View {
        HStack(spacing: 8) {
            ForEach(Array(items.enumerated()), id: \.offset) { index, item in
                Text(item.text)
                    .font(
                        resolvedPreviewFont(
                            size: highlightedIndex == index ? 27 : 22,
                            weight: highlightedIndex == index ? .heavy : .bold
                        )
                    )
                    .foregroundColor(item.color)
                    .frame(minWidth: 20)
                    .scaleEffect(highlightedIndex == index ? 1.08 : 1.0)
                    .shadow(color: .white.opacity(0.65), radius: 0.6)
                    .animation(.easeInOut(duration: 0.12), value: highlightedIndex)
            }
        }
        .padding(.horizontal, 18)
        .padding(.vertical, 10)
        .background(
            Capsule()
                .fill(isDarkMode ? Color(hex: "#323232").opacity(0.96) : Color.white.opacity(0.96))
                .shadow(color: .black.opacity(0.08), radius: 6, y: 2)
        )
        .frame(maxWidth: .infinity, alignment: .center)
    }
}

// 3. iOS 输入法核心控制器
class KeyboardViewController: UIInputViewController, KeyboardActionDelegate {

    var stateMachine: KeyboardStateMachine!
    var viewModel = KeyboardViewModel()
    private let deadzoneRadius: Float = 40
    private var mirroredLeftDirection: WheelDirection = .none
    private var mirroredRightDirection: WheelDirection = .none
    private var mirroredMode: WheelMode = .normal
    private var mirroredChordExecuted = false

    override func viewDidLoad() {
        super.viewDidLoad()
        
        // --- 唤醒跨平台大脑 ---
        // Kotlin 的全局函数在 Swift 里会自动被放到带 'Kt' 后缀的命名空间下
//        stateMachine = // Swift 现在会极其自然地调用我们刚刚写的那个次级构造函数！
        // 使用我们在 Kotlin 里建好的工厂 (KeyboardFactory) 去拿货
        stateMachine = KeyboardFactory.shared.createEngine(
            delegate: self,
            layoutType: LayoutType.logical
        )
        
        // 读取布局偏好并设置到状态机
        applyLayoutPreference()
        
        // --- UI 挂载与闭包打通 ---
        let containerView = KeyboardContainerView(viewModel: viewModel) { [weak self] dx, dy, isLeft, isDown, isUp in
            self?.handleTouch(dx: dx, dy: dy, isLeft: isLeft, isDown: isDown, isUp: isUp)
        }
        
        // 使用 UIHostingController 把 SwiftUI 包装成传统的 UIKit View
        let hostingController = UIHostingController(rootView: containerView)
        hostingController.view.translatesAutoresizingMaskIntoConstraints = false
        hostingController.view.backgroundColor = .clear
        
        self.addChild(hostingController)
        self.view.addSubview(hostingController.view)
        hostingController.didMove(toParent: self)
        
        // 设置 iOS 的自动布局约束（撑满全屏，高度定为盲打舒适的 280）
        let heightConstraint = self.view.heightAnchor.constraint(equalToConstant: 280)
        heightConstraint.priority = .init(999)
        
        // 2. 激活所有约束
        NSLayoutConstraint.activate([
            hostingController.view.leftAnchor.constraint(equalTo: self.view.leftAnchor),
            hostingController.view.rightAnchor.constraint(equalTo: self.view.rightAnchor),
            hostingController.view.topAnchor.constraint(equalTo: self.view.topAnchor),
            hostingController.view.bottomAnchor.constraint(equalTo: self.view.bottomAnchor),
            heightConstraint
        ])
    }
    
    // --- 核心分发：将 iOS 触摸数据喂给 Kotlin 大脑 ---
    func handleTouch(dx: Float, dy: Float, isLeft: Bool, isDown: Bool, isUp: Bool) {
        syncVisualState(dx: dx, dy: dy, isLeft: isLeft, isDown: isDown, isUp: isUp)
        stateMachine.handleTouch(x: dx, y: dy, isLeft: isLeft, actionDownOrMove: isDown, actionUp: isUp)
        
        // 从大脑获取最新预览并更新 UI
        viewModel.previewText = stateMachine.getPreviewText()
        viewModel.leftDirection = mirroredLeftDirection
        viewModel.rightDirection = mirroredRightDirection
        viewModel.keyboardMode = mirroredMode
        viewModel.isEfficiency = isEfficiencyLayout
        updatePreviewState()
    }

    // ==========================================
    // 实现 Kotlin 大脑的代理方法 (Action Delegate)
    // ==========================================

    func commitText(text: String) {
        self.textDocumentProxy.insertText(text)
    }

    func sendInputAction(action: InputAction) {
        // Kotlin 的枚举在 Swift 中会自动变成小写驼峰式
        switch action {
        case .space:
            self.textDocumentProxy.insertText(" ")
        case .enter:
            self.textDocumentProxy.insertText("\n")
        case .backspace, .deleteForward:
            // iOS 键盘 API 没有向后删除，统一使用系统的 deleteBackward
            self.textDocumentProxy.deleteBackward()
        case .moveHome:
            if let before = self.textDocumentProxy.documentContextBeforeInput {
                self.textDocumentProxy.adjustTextPosition(byCharacterOffset: -before.count)
            }
        case .moveEnd:
            if let after = self.textDocumentProxy.documentContextAfterInput {
                self.textDocumentProxy.adjustTextPosition(byCharacterOffset: after.count)
            }
        case .tab:
            self.textDocumentProxy.insertText("\t")
            
        // --- 移动光标逻辑 ---
        case .dpadLeft:
            self.textDocumentProxy.adjustTextPosition(byCharacterOffset: -1)
        case .dpadRight:
            self.textDocumentProxy.adjustTextPosition(byCharacterOffset: 1)
        default:
            // 提示：iOS 第三方键盘由于系统安全限制，无法完美模拟上下键 (不知道上一行有多少字)
            // 所以 DPAD_UP / DOWN 等在这里暂时忽略
            break
        }
    }

    private static let appGroupDefaults = UserDefaults(suiteName: "group.com.vatoo.erick") ?? .standard

    private var isEfficiencyLayout: Bool {
        return Self.appGroupDefaults.string(forKey: "layout_type") == "efficiency"
    }

    private var isCustomLayout: Bool {
        return Self.appGroupDefaults.string(forKey: "layout_type") == "custom"
    }

    private var activeCustomLayoutId: String {
        return Self.appGroupDefaults.string(forKey: "custom_layout_id") ?? ""
    }

    private var currentColorPaletteKey: String {
        let enabled = Self.appGroupDefaults.bool(forKey: "colorblind_mode")
        guard enabled else { return "default" }
        return Self.appGroupDefaults.string(forKey: "color_palette") ?? "okabe_ito"
    }

    private var isLeftHandedMode: Bool {
        return Self.appGroupDefaults.bool(forKey: "left_handed_mode")
    }

    private func applyLayoutPreference() {
        let layoutType: LayoutType
        if isCustomLayout {
            layoutType = .custom
        } else if isEfficiencyLayout {
            layoutType = .efficiency
        } else {
            layoutType = .logical
        }
        stateMachine.setLayoutType(layout: layoutType)
        viewModel.isEfficiency = isEfficiencyLayout

        // Load custom layout if applicable
        if layoutType == LayoutType.custom {
            let storage = IOSCustomLayoutStorage()
            let manager = CustomLayoutManager(storage: storage)
            manager.loadAll()
            let customId = activeCustomLayoutId
            if !customId.isEmpty {
                let cl = manager.getById(id: customId)
                stateMachine.activeCustomLayout = cl
                if let cl = cl {
                    viewModel.customNormalSections = Self.customLayoutToSections(cl.normalChordMap)
                    viewModel.customShiftedSections = Self.customLayoutToSections(cl.shiftedChordMap)
                } else {
                    viewModel.customNormalSections = nil
                    viewModel.customShiftedSections = nil
                }
            } else {
                stateMachine.activeCustomLayout = nil
                viewModel.customNormalSections = nil
                viewModel.customShiftedSections = nil
            }
        } else {
            stateMachine.activeCustomLayout = nil
            viewModel.customNormalSections = nil
            viewModel.customShiftedSections = nil
        }

        viewModel.colorPaletteKey = currentColorPaletteKey

        let leftHanded = isLeftHandedMode
        stateMachine.setLeftHandedMode(enabled: leftHanded)
        viewModel.isLeftHanded = leftHanded

        // Apply theme mode
        let themeMode = Self.appGroupDefaults.string(forKey: "theme_mode") ?? "system"
        switch themeMode {
        case "dark":
            viewModel.isDarkMode = true
        case "light":
            viewModel.isDarkMode = false
        default:
            viewModel.isDarkMode = self.traitCollection.userInterfaceStyle == .dark
        }

        // Apply font preference
        viewModel.fontPreference = Self.appGroupDefaults.string(forKey: "font_preference") ?? "system"
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        applyLayoutPreference()
    }

    private func syncVisualState(dx: Float, dy: Float, isLeft: Bool, isDown: Bool, isUp: Bool) {
        let currentDirection = direction(forX: dx, y: dy)
        // In left-handed mode, swap which physical side is the "letter" vs "action" dial
        let effectiveIsLeft = viewModel.isLeftHanded ? !isLeft : isLeft

        if isDown {
            if effectiveIsLeft {
                mirroredLeftDirection = currentDirection
            } else {
                mirroredRightDirection = currentDirection
            }
            return
        }

        guard isUp else { return }

        if effectiveIsLeft {
            if mirroredRightDirection != .none && !mirroredChordExecuted {
                mirroredChordExecuted = true
                if mirroredMode == .shifted {
                    mirroredMode = .normal
                }
            }
            mirroredLeftDirection = .none
        } else {
            if mirroredLeftDirection != .none && !mirroredChordExecuted {
                mirroredChordExecuted = true
                if mirroredMode == .shifted {
                    mirroredMode = .normal
                }
            } else if mirroredLeftDirection == .none && !mirroredChordExecuted {
                applyRightOnlyVisualAction(for: mirroredRightDirection)
            }
            mirroredRightDirection = .none
        }

        if mirroredLeftDirection == .none && mirroredRightDirection == .none {
            mirroredChordExecuted = false
        }
    }

    private func applyRightOnlyVisualAction(for direction: WheelDirection) {
        switch direction {
        case .sw:
            mirroredMode = mirroredMode == .normal ? .shifted : .normal
        case .nw:
            mirroredMode = mirroredMode == .capsLocked ? .normal : .capsLocked
        default:
            break
        }
    }

    /// Converts a KMP Direction-keyed chord map to an ordered [[String]] array for the SwiftUI JoystickView.
    private static func customLayoutToSections(_ chordMap: [Direction: [String]]) -> [[String]] {
        let dirOrder: [Direction] = [.n, .ne, .e, .se, .s, .sw, .w, .nw]
        return dirOrder.map { dir in
            let chars = chordMap[dir] ?? []
            // Pad to 8 entries if shorter
            var result = chars.map { $0 as String }
            while result.count < 8 { result.append("") }
            return result
        }
    }

    private func wheelDirectionIndex(_ dir: WheelDirection) -> Int {
        switch dir {
        case .n: return 0; case .ne: return 1; case .e: return 2; case .se: return 3
        case .s: return 4; case .sw: return 5; case .w: return 6; case .nw: return 7
        case .none: return -1
        }
    }

    private func direction(forX x: Float, y: Float) -> WheelDirection {
        let distance = hypot(x, y)
        guard distance > deadzoneRadius else {
            return .none
        }

        var degrees = atan2(Double(y), Double(x)) * 180 / .pi
        if degrees < 0 {
            degrees += 360
        }

        switch degrees {
        case 337.5..., ..<22.5:
            return .e
        case 22.5..<67.5:
            return .se
        case 67.5..<112.5:
            return .s
        case 112.5..<157.5:
            return .sw
        case 157.5..<202.5:
            return .w
        case 202.5..<247.5:
            return .nw
        case 247.5..<292.5:
            return .n
        case 292.5..<337.5:
            return .ne
        default:
            return .none
        }
    }

    private func updatePreviewState() {
        if mirroredLeftDirection != .none {
            // Left-dial hold: show characters for that group
            guard let items = previewItems(for: mirroredLeftDirection, mode: mirroredMode) else {
                viewModel.previewItems = []
                viewModel.highlightedPreviewIndex = nil
                return
            }
            viewModel.previewItems = items
            if let index = previewIndex(for: mirroredRightDirection), index < items.count {
                viewModel.highlightedPreviewIndex = index
            } else {
                viewModel.highlightedPreviewIndex = nil
            }
        } else if mirroredRightDirection != .none {
            // Right-dial-only hold: show character at this position across all left-dial groups
            let items = rightDialPreviewItems(for: mirroredRightDirection, mode: mirroredMode)
            viewModel.previewItems = items
            viewModel.highlightedPreviewIndex = nil
        } else {
            viewModel.previewItems = []
            viewModel.highlightedPreviewIndex = nil
        }
    }

    private func previewIndex(for direction: WheelDirection) -> Int? {
        switch direction {
        case .n:  return 0
        case .ne: return 1
        case .e:  return 2
        case .se: return 3
        case .s:  return 4
        case .sw: return 5
        case .w:  return 6
        case .nw: return 7
        default:  return nil
        }
    }

    private func previewItems(for direction: WheelDirection, mode: WheelMode) -> [KeyboardPreviewItem]? {
        let palette = ColorPaletteDefinitions.palette(for: currentColorPaletteKey)
        // Palette positions: 0=N, 1=NE, 2=E, 3=SE, 4=S, 5=SW, 6=W, 7=NW
        // Left dial layout: outer=[0,1,2], middle=[3,4,5], inner=[6,7]
        let outerColors = (0..<3).map { Color(hex: palette[$0].hex) }
        let middleColors = (3..<6).map { Color(hex: palette[$0].hex) }
        let innerColors = (6..<8).map { Color(hex: palette[$0].hex) }

        let items: [String]?

        // Use custom layout data if available
        let customSections = mode.usesShiftedSymbols ? viewModel.customShiftedSections : viewModel.customNormalSections
        if let sections = customSections {
            let dirIndex = wheelDirectionIndex(direction)
            guard dirIndex >= 0 && dirIndex < sections.count else { return nil }
            let chars = sections[dirIndex].filter { !$0.isEmpty }
            items = chars.isEmpty ? nil : chars
        } else if isEfficiencyLayout {
            // Efficiency layout — frequency-optimized character placement
            switch (direction, mode.usesShiftedSymbols) {
            case (.n, false):  items = ["t", "s", "g", "7", "=", "4", "k"]
            case (.ne, false): items = ["i", "a", "n", "p", "/", "'"]
            case (.e, false):  items = ["v", "l", "e", "r", "x", ";"]
            case (.se, false): items = ["-", "y", "d", "o", "m"]
            case (.s, false):  items = ["`", "6", "b", "f", "u"]
            case (.sw, false): items = ["\\", "[", "]", "5", "q", "j"]
            case (.w, false):  items = ["2", "3", "z"]
            case (.nw, false): items = ["h", "w", "1", "8", "9", "0", "c"]
            case (.n, true):   items = ["T", "S", "G", "&", "+", "$", "K"]
            case (.ne, true):  items = ["I", "A", "N", "P", "?", "\""]
            case (.e, true):   items = ["V", "L", "E", "R", "X", ":"]
            case (.se, true):  items = ["_", "Y", "D", "O", "M"]
            case (.s, true):   items = ["~", "^", "B", "F", "U"]
            case (.sw, true):  items = ["|", "{", "}", "%", "Q", "J"]
            case (.w, true):   items = ["@", "#", "Z"]
            case (.nw, true):  items = ["H", "W", "!", "*", "(", ")", "C"]
            case (.none, _):   return nil
            }
        } else {
            // Logical layout (A–Z)
            switch (direction, mode.usesShiftedSymbols) {
            case (.n, false): items = ["a", "b", "c", "d", "e", "'"]
            case (.ne, false): items = ["f", "g", "h", "i", "j", "/"]
            case (.e, false): items = ["k", "l", "m", "n", "o", ";"]
            case (.se, false): items = ["p", "q", "r", "s", "t", "-"]
            case (.s, false): items = ["u", "v", "w", "x", "y", "="]
            case (.sw, false): items = ["z", "\\", "[", "]", "`"]
            case (.w, false): items = ["1", "2", "3", "4", "5"]
            case (.nw, false): items = ["6", "7", "8", "9", "0"]
            case (.n, true): items = ["A", "B", "C", "D", "E", "\""]
            case (.ne, true): items = ["F", "G", "H", "I", "J", "?"]
            case (.e, true): items = ["K", "L", "M", "N", "O", ":"]
            case (.se, true): items = ["P", "Q", "R", "S", "T", "_"]
            case (.s, true): items = ["U", "V", "W", "X", "Y", "+"]
            case (.sw, true): items = ["Z", "|", "{", "}", "~"]
            case (.w, true): items = ["!", "@", "#", "$", "%"]
            case (.nw, true): items = ["^", "&", "*", "(", ")"]
            case (.none, _): return nil
            }
        }

        guard let items else { return nil }
        return items.enumerated().map { index, item in
            let color: Color
            switch index {
            case 0..<3:
                color = outerColors[index]
            case 3..<5:
                color = middleColors[index - 3]
            case 5:
                color = innerColors[0]
            default:
                color = Color(hex: "#5F6368")
            }
            return KeyboardPreviewItem(id: index, text: item, color: color)
        }
    }

    /// Right-dial-only preview: returns the character at the given right-dial position
    /// across all 8 left-dial groups. Works for Logical, Efficiency layouts in all modes.
    private func rightDialPreviewItems(for direction: WheelDirection, mode: WheelMode) -> [KeyboardPreviewItem] {
        guard direction != .none else { return [] }

        guard let posIndex = previewIndex(for: direction) else { return [] }

        let palette = ColorPaletteDefinitions.palette(for: currentColorPaletteKey)
        let colorHex = colorHexForDirection(direction, palette: palette)
        let color = Color(hex: colorHex)

        let allLeftDirs: [WheelDirection] = [.n, .ne, .e, .se, .s, .sw, .w, .nw]
        var result: [KeyboardPreviewItem] = []
        var itemId = 0

        for leftDir in allLeftDirs {
            guard let groupItems = previewItems(for: leftDir, mode: mode) else { continue }
            // Find the character at posIndex in this group's full 8-slot layout
            let charAtPos = characterAtRightIndex(posIndex, leftDir: leftDir, mode: mode)
            if let ch = charAtPos, !ch.isEmpty {
                result.append(KeyboardPreviewItem(id: itemId, text: ch, color: color))
                itemId += 1
            }
        }

        return result
    }

    /// Returns the character at a specific right-dial index for a given left-dial direction.
    /// Uses the full 8-slot layout data (including empty slots) to get the correct position.
    private func characterAtRightIndex(_ index: Int, leftDir: WheelDirection, mode: WheelMode) -> String? {
        guard index >= 0 && index < 8 else { return nil }

        // Use custom layout data if available
        let customSections = mode.usesShiftedSymbols ? viewModel.customShiftedSections : viewModel.customNormalSections
        if let sections = customSections {
            let dirIndex = wheelDirectionIndex(leftDir)
            guard dirIndex >= 0 && dirIndex < sections.count else { return nil }
            let chars = sections[dirIndex]
            guard index < chars.count else { return nil }
            let ch = chars[index]
            return ch.isEmpty ? nil : ch
        }

        let fullSlots: [String]
        if isEfficiencyLayout {
            switch (leftDir, mode.usesShiftedSymbols) {
            case (.n, false):  fullSlots = ["t", "s", "g", "7", "=", "", "4", "k"]
            case (.ne, false): fullSlots = ["i", "a", "n", "p", "/", "", "", "'"]
            case (.e, false):  fullSlots = ["v", "l", "e", "r", "x", "", "", ";"]
            case (.se, false): fullSlots = ["-", "y", "d", "o", "m", "", "", ""]
            case (.s, false):  fullSlots = ["`", "6", "b", "f", "u", "", "", ""]
            case (.sw, false): fullSlots = ["\\", "[", "]", "5", "q", "j", "", ""]
            case (.w, false):  fullSlots = ["", "", "", "", "", "2", "3", "z"]
            case (.nw, false): fullSlots = ["h", "w", "1", "8", "9", "", "0", "c"]
            case (.n, true):   fullSlots = ["T", "S", "G", "&", "+", "", "$", "K"]
            case (.ne, true):  fullSlots = ["I", "A", "N", "P", "?", "", "", "\""]
            case (.e, true):   fullSlots = ["V", "L", "E", "R", "X", "", "", ":"]
            case (.se, true):  fullSlots = ["_", "Y", "D", "O", "M", "", "", ""]
            case (.s, true):   fullSlots = ["~", "^", "B", "F", "U", "", "", ""]
            case (.sw, true):  fullSlots = ["|", "{", "}", "%", "Q", "J", "", ""]
            case (.w, true):   fullSlots = ["", "", "", "", "", "@", "#", "Z"]
            case (.nw, true):  fullSlots = ["H", "W", "!", "*", "(", "", ")", "C"]
            default:           return nil
            }
        } else {
            switch (leftDir, mode.usesShiftedSymbols) {
            case (.n, false):  fullSlots = ["a", "b", "c", "d", "e", "", "", "'"]
            case (.ne, false): fullSlots = ["f", "g", "h", "i", "j", "", "", "/"]
            case (.e, false):  fullSlots = ["k", "l", "m", "n", "o", "", "", ";"]
            case (.se, false): fullSlots = ["p", "q", "r", "s", "t", "", "", "-"]
            case (.s, false):  fullSlots = ["u", "v", "w", "x", "y", "", "", "="]
            case (.sw, false): fullSlots = ["z", "\\", "[", "]", "`", "", "", ""]
            case (.w, false):  fullSlots = ["1", "2", "3", "4", "5", "", "", ""]
            case (.nw, false): fullSlots = ["6", "7", "8", "9", "0", "", "", ""]
            case (.n, true):   fullSlots = ["A", "B", "C", "D", "E", "", "", "\""]
            case (.ne, true):  fullSlots = ["F", "G", "H", "I", "J", "", "", "?"]
            case (.e, true):   fullSlots = ["K", "L", "M", "N", "O", "", "", ":"]
            case (.se, true):  fullSlots = ["P", "Q", "R", "S", "T", "", "", "_"]
            case (.s, true):   fullSlots = ["U", "V", "W", "X", "Y", "", "", "+"]
            case (.sw, true):  fullSlots = ["Z", "|", "{", "}", "~", "", "", ""]
            case (.w, true):   fullSlots = ["!", "@", "#", "$", "%", "", "", ""]
            case (.nw, true):  fullSlots = ["^", "&", "*", "(", ")", "", "", ""]
            default:           return nil
            }
        }

        let ch = fullSlots[index]
        return ch.isEmpty ? nil : ch
    }

    private func colorHexForDirection(_ direction: WheelDirection, palette: [ColorPaletteEntry]) -> String {
        switch direction {
        case .n:  return palette[0].hex
        case .ne: return palette[1].hex
        case .e:  return palette[2].hex
        case .se: return palette[3].hex
        case .s:  return palette[4].hex
        case .sw: return palette[5].hex
        case .w:  return palette[6].hex
        case .nw: return palette[7].hex
        default:  return "#5F6368"
        }
    }
}

