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
}

// 2. SwiftUI 键盘容器：把左右两个摇杆横向排列
struct KeyboardContainerView: View {
    @ObservedObject var viewModel: KeyboardViewModel
    // 闭包回调参数：dx, dy, isLeft, isDownOrMove, isUp
    var onTouch: (Float, Float, Bool, Bool, Bool) -> Void
    
    @State private var showSettings = false // 控制设置页面的显示

    var body: some View {
        ZStack(alignment: .top) {
            Color(hex: "#ECEFF1")
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
                        isRightSide: false,
                        activeDirection: viewModel.leftDirection,
                        keyboardMode: viewModel.keyboardMode,
                        isEfficiency: viewModel.isEfficiency,
                        colorPaletteKey: viewModel.colorPaletteKey
                    ) { dx, dy, isDownOrMove, isUp in
                        onTouch(dx, dy, true, isDownOrMove, isUp)
                    }
                    .frame(width: leftSize, height: leftSize)

                    JoystickView(
                        isRightSide: true,
                        activeDirection: viewModel.rightDirection,
                        keyboardMode: viewModel.keyboardMode,
                        isEfficiency: viewModel.isEfficiency,
                        colorPaletteKey: viewModel.colorPaletteKey
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
                    highlightedIndex: viewModel.highlightedPreviewIndex
                )
                    .padding(.top, 8)
            }

            HStack {
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

    var body: some View {
        HStack(spacing: 8) {
            ForEach(Array(items.enumerated()), id: \.offset) { index, item in
                Text(item.text)
                    .font(
                        .system(
                            size: highlightedIndex == index ? 27 : 22,
                            weight: highlightedIndex == index ? .heavy : .bold,
                            design: .rounded
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
                .fill(Color.white.opacity(0.96))
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
        stateMachine = KeyboardFactory.shared.createEngine(delegate: self)
        
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

    private var currentColorPaletteKey: String {
        let enabled = Self.appGroupDefaults.bool(forKey: "colorblind_mode")
        guard enabled else { return "default" }
        return Self.appGroupDefaults.string(forKey: "color_palette") ?? "okabe_ito"
    }

    private func applyLayoutPreference() {
        let layoutType: LayoutType = isEfficiencyLayout ? .efficiency : .logical
        stateMachine.setLayoutType(layout: layoutType)
        viewModel.isEfficiency = isEfficiencyLayout
        viewModel.colorPaletteKey = currentColorPaletteKey
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        applyLayoutPreference()
    }

    private func syncVisualState(dx: Float, dy: Float, isLeft: Bool, isDown: Bool, isUp: Bool) {
        let currentDirection = direction(forX: dx, y: dy)

        if isDown {
            if isLeft {
                mirroredLeftDirection = currentDirection
            } else {
                mirroredRightDirection = currentDirection
            }
            return
        }

        guard isUp else { return }

        if isLeft {
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
    }

    private func previewIndex(for direction: WheelDirection) -> Int? {
        switch direction {
        case .n: return 0
        case .ne: return 1
        case .e: return 2
        case .se: return 3
        case .s: return 4
        case .sw: return 5
        default: return nil
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

        if isEfficiencyLayout {
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
}

