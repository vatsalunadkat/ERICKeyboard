import UIKit
import SwiftUI
import Combine
import GameController
import SharedKeyboard // Import the KMP shared module

struct KeyboardPreviewItem: Identifiable {
    let id: Int
    let direction: WheelDirection
    let text: String
    let color: Color
}

// 1. State bridge: SwiftUI is reactive, so we need a ViewModel to update the preview text in real-time
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
    @Published var suggestions: [String] = []
    @Published var bothDialsAtHome: Bool = true
    /// Physical controller stick position (-1...1), used to move on-screen thumb when controller is active
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

// 2. SwiftUI keyboard container: arranges left and right joysticks horizontally
struct KeyboardContainerView: View {
    @ObservedObject var viewModel: KeyboardViewModel
    // Closure callback parameters: dx, dy, isLeft, isDownOrMove, isUp
    var onTouch: (Float, Float, Bool, Bool, Bool) -> Void
    var onSettingsChanged: () -> Void
    var onSuggestionTapped: (Int) -> Void
    
    @State private var showSettings = false // Controls the settings page display

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
                        customShiftedSections: viewModel.customShiftedSections,
                        controllerStickNormalized: viewModel.leftControllerStickNormalized
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
                        customShiftedSections: viewModel.customShiftedSections,
                        controllerStickNormalized: viewModel.rightControllerStickNormalized
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
            } else if viewModel.bothDialsAtHome && !viewModel.suggestions.isEmpty {
                KeyboardSuggestionBar(
                    suggestions: viewModel.suggestions,
                    isDarkMode: viewModel.isDarkMode,
                    onTap: onSuggestionTapped
                )
                    .padding(.top, 8)
            }

            HStack {
                // Shift / Caps Lock indicator
                if viewModel.keyboardMode == .shifted {
                    Text("⇧ Shift")
                        .font(.system(size: 13, weight: .semibold))
                        .foregroundColor(viewModel.isDarkMode ? .white : Color(hex: "#333333"))
                        .padding(.horizontal, 6)
                        .padding(.top, 4)
                        .transition(.opacity)
                        .accessibilityLabel("Shift mode active")
                } else if viewModel.keyboardMode == .capsLocked {
                    Text("⇧⇧ CAPS")
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

            // Settings overlay
            if showSettings {
                SettingsView(onClose: {
                    withAnimation {
                        showSettings = false
                    }
                }, onSettingsChanged: {
                    onSettingsChanged()
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

private struct KeyboardSuggestionBar: View {
    let suggestions: [String]
    var isDarkMode: Bool = false
    var onTap: (Int) -> Void

    var body: some View {
        HStack(spacing: 0) {
            ForEach(Array(suggestions.enumerated()), id: \.offset) { index, word in
                if index > 0 {
                    Divider()
                        .frame(height: 24)
                        .background(isDarkMode ? Color.gray.opacity(0.5) : Color.gray.opacity(0.3))
                }
                Button(action: { onTap(index) }) {
                    Text(word)
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(isDarkMode ? .white : Color(hex: "#333333"))
                        .frame(maxWidth: .infinity)
                        .frame(height: 40)
                        .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, 12)
        .background(
            Capsule()
                .fill(isDarkMode ? Color(hex: "#323232").opacity(0.96) : Color.white.opacity(0.96))
                .shadow(color: .black.opacity(0.08), radius: 6, y: 2)
        )
        .frame(maxWidth: .infinity, alignment: .center)
        .padding(.horizontal, 24)
    }
}

// 3. iOS input method core controller
class KeyboardViewController: UIInputViewController, KeyboardActionDelegate {

    var stateMachine: KeyboardStateMachine!
    var viewModel = KeyboardViewModel()
    private let keyboardLogic = KeyboardLogic()
    private let deadzoneRadius: Float = 40
    private var mirroredLeftDirection: WheelDirection = .none
    private var mirroredRightDirection: WheelDirection = .none
    private var mirroredMode: WheelMode = .normal
    private var mirroredChordExecuted = false
    private var currentController: GCController?
    private var localControllerTimer: Timer?
    private var prevLocalLeftActive = false
    private var prevLocalRightActive = false
    private var controllerBridgeTimer: Timer?
    private var prevBridgeLeftActive = false
    private var prevBridgeRightActive = false
    private var hasRegisteredControllerObservers = false
    
    private static let appGroupId = "group.com.vatoo.erick"
    private var appGroupDefaults: UserDefaults? { UserDefaults(suiteName: Self.appGroupId) }
    private static let controllerBridgeStaleInterval: TimeInterval = 0.2

    override func viewDidLoad() {
        super.viewDidLoad()
        
        // --- Initialize the cross-platform state machine ---
        // Kotlin global functions are auto-namespaced under a 'Kt' suffix in Swift
//        stateMachine = // Swift now naturally calls the secondary constructor we wrote
        // Use the Kotlin factory (KeyboardFactory) to create the engine
        stateMachine = KeyboardFactory.shared.createEngine(delegate: self)
        
        // Read layout preference and apply to the state machine
        applyLayoutPreference()
        
        // --- UI mounting and closure wiring ---
        let containerView = KeyboardContainerView(viewModel: viewModel) { [weak self] dx, dy, isLeft, isDown, isUp in
            self?.handleTouch(dx: dx, dy: dy, isLeft: isLeft, isDown: isDown, isUp: isUp)
        } onSettingsChanged: { [weak self] in
            self?.handleSettingsChanged()
        } onSuggestionTapped: { [weak self] index in
            self?.onSuggestionTapped(index)
        }
        
        // Use UIHostingController to wrap SwiftUI into a traditional UIKit View
        let hostingController = UIHostingController(rootView: containerView)
        hostingController.view.translatesAutoresizingMaskIntoConstraints = false
        hostingController.view.backgroundColor = .clear
        
        self.addChild(hostingController)
        self.view.addSubview(hostingController.view)
        hostingController.didMove(toParent: self)
        
        // Set up iOS Auto Layout constraints (fill the screen, height fixed at 280 for comfortable touch typing)
        let heightConstraint = self.view.heightAnchor.constraint(equalToConstant: 280)
        heightConstraint.priority = .init(999)
        
        // 2. Activate all constraints
        NSLayoutConstraint.activate([
            hostingController.view.leftAnchor.constraint(equalTo: self.view.leftAnchor),
            hostingController.view.rightAnchor.constraint(equalTo: self.view.rightAnchor),
            hostingController.view.topAnchor.constraint(equalTo: self.view.topAnchor),
            hostingController.view.bottomAnchor.constraint(equalTo: self.view.bottomAnchor),
            heightConstraint
        ])
        
        // Physical controller input (DualShock 4 and other Bluetooth controllers)
        setupControllerInput()
        startControllerBridgePolling()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        localControllerTimer?.invalidate()
        localControllerTimer = nil
        controllerBridgeTimer?.invalidate()
        controllerBridgeTimer = nil
    }
    
    // MARK: - GameController (DualShock 4, etc.)
    private static let controllerDeadZone: Float = 0.25
    private static let controllerToTouchScale: Float = 80
    
    private func setupControllerInput() {
        if !hasRegisteredControllerObservers {
            NotificationCenter.default.addObserver(
                self,
                selector: #selector(controllerDidConnect),
                name: .GCControllerDidConnect,
                object: nil
            )
            NotificationCenter.default.addObserver(
                self,
                selector: #selector(controllerDidDisconnect),
                name: .GCControllerDidDisconnect,
                object: nil
            )
            hasRegisteredControllerObservers = true
        }
        GCController.startWirelessControllerDiscovery {}
        setupCurrentController()
    }
    
    @objc private func controllerDidConnect(_ note: Notification) {
        DispatchQueue.main.async { [weak self] in
            self?.setupCurrentController()
        }
    }
    
    @objc private func controllerDidDisconnect(_ note: Notification) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            self.currentController = nil
            self.localControllerTimer?.invalidate()
            self.localControllerTimer = nil
            self.prevLocalLeftActive = false
            self.prevLocalRightActive = false
            self.viewModel.leftControllerStickNormalized = (0, 0)
            self.viewModel.rightControllerStickNormalized = (0, 0)
            self.handleTouch(dx: 0, dy: 0, isLeft: true, isDown: false, isUp: true)
            self.handleTouch(dx: 0, dy: 0, isLeft: false, isDown: false, isUp: true)
        }
    }
    
    private func setupCurrentController() {
        guard let controller = GCController.controllers().first,
              let extended = controller.extendedGamepad else { return }

        if currentController !== controller {
            currentController = controller
        }

        startLocalControllerPolling()
        
        extended.leftThumbstick.valueChangedHandler = { [weak self] _, xValue, yValue in
            self?.handleControllerStick(x: xValue, y: yValue, isLeft: true)
        }
        extended.rightThumbstick.valueChangedHandler = { [weak self] _, xValue, yValue in
            self?.handleControllerStick(x: xValue, y: yValue, isLeft: false)
        }
    }

    private func startLocalControllerPolling() {
        localControllerTimer?.invalidate()
        localControllerTimer = Timer.scheduledTimer(withTimeInterval: 1.0 / 60.0, repeats: true) { [weak self] _ in
            self?.pollLocalController()
        }
        RunLoop.main.add(localControllerTimer!, forMode: .common)
    }

    private func pollLocalController() {
        guard let extended = currentController?.extendedGamepad else { return }

        let left = normalizedControllerStick(x: extended.leftThumbstick.xAxis.value, y: extended.leftThumbstick.yAxis.value)
        let right = normalizedControllerStick(x: extended.rightThumbstick.xAxis.value, y: extended.rightThumbstick.yAxis.value)

        let leftActive = abs(left.x) > 0.01 || abs(left.y) > 0.01
        let rightActive = abs(right.x) > 0.01 || abs(right.y) > 0.01

        if leftActive || prevLocalLeftActive {
            processControllerState(normalized: left, isLeft: true, wasActive: prevLocalLeftActive)
        }
        if rightActive || prevLocalRightActive {
            processControllerState(normalized: right, isLeft: false, wasActive: prevLocalRightActive)
        }

        prevLocalLeftActive = leftActive
        prevLocalRightActive = rightActive
    }
    
    private func handleControllerStick(x: Float, y: Float, isLeft: Bool) {
        let normalized = normalizedControllerStick(x: x, y: y)
        
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            let wasActive = isLeft ? self.prevLocalLeftActive : self.prevLocalRightActive
            self.processControllerState(normalized: normalized, isLeft: isLeft, wasActive: wasActive)
            if isLeft {
                self.prevLocalLeftActive = abs(normalized.x) > 0.01 || abs(normalized.y) > 0.01
            } else {
                self.prevLocalRightActive = abs(normalized.x) > 0.01 || abs(normalized.y) > 0.01
            }
        }
    }

    private func normalizedControllerStick(x: Float, y: Float) -> (x: Float, y: Float) {
        let dead = Self.controllerDeadZone
        var nx = x
        var ny = y
        let mag = sqrt(nx * nx + ny * ny)
        if mag > dead {
            let scale = (mag - dead) / (1 - dead)
            nx = (nx / mag) * scale
            ny = (ny / mag) * scale
        } else {
            nx = 0
            ny = 0
        }
        return (nx, ny)
    }

    private func processControllerState(normalized: (x: Float, y: Float), isLeft: Bool, wasActive: Bool) {
        let isActive = abs(normalized.x) > 0.01 || abs(normalized.y) > 0.01
        let dx = normalized.x * Self.controllerToTouchScale
        let dy = -normalized.y * Self.controllerToTouchScale

        if isLeft {
            viewModel.leftControllerStickNormalized = normalized
        } else {
            viewModel.rightControllerStickNormalized = normalized
        }

        if isActive {
            handleTouch(dx: dx, dy: dy, isLeft: isLeft, isDown: true, isUp: false)
        } else if wasActive {
            handleTouch(dx: 0, dy: 0, isLeft: isLeft, isDown: false, isUp: true)
        }
    }
    
    // MARK: - App Group bridge (host app reads controller, keyboard extension reads)
    private func startControllerBridgePolling() {
        controllerBridgeTimer?.invalidate()
        controllerBridgeTimer = Timer.scheduledTimer(withTimeInterval: 1.0 / 60.0, repeats: true) { [weak self] _ in
            self?.pollControllerBridge()
        }
        RunLoop.main.add(controllerBridgeTimer!, forMode: .common)
    }
    
    private func pollControllerBridge() {
        if currentController?.extendedGamepad != nil {
            return
        }

        guard let defaults = appGroupDefaults else { return }
        let now = Date().timeIntervalSince1970
        guard let ts = defaults.object(forKey: "controller_timestamp") as? TimeInterval else { return }
        
        let isStale = now - ts >= Self.controllerBridgeStaleInterval
        if isStale {
            viewModel.leftControllerStickNormalized = (0, 0)
            viewModel.rightControllerStickNormalized = (0, 0)
            if prevBridgeLeftActive || prevBridgeRightActive {
                handleTouch(dx: 0, dy: 0, isLeft: true, isDown: false, isUp: true)
                handleTouch(dx: 0, dy: 0, isLeft: false, isDown: false, isUp: true)
            }
            prevBridgeLeftActive = false
            prevBridgeRightActive = false
            return
        }
        
        let lnx = defaults.object(forKey: "controller_left_x") as? Float ?? 0
        let lny = defaults.object(forKey: "controller_left_y") as? Float ?? 0
        let rnx = defaults.object(forKey: "controller_right_x") as? Float ?? 0
        let rny = defaults.object(forKey: "controller_right_y") as? Float ?? 0
        
        let leftActive = abs(lnx) > 0.01 || abs(lny) > 0.01
        let rightActive = abs(rnx) > 0.01 || abs(rny) > 0.01
        
        let scale = Self.controllerToTouchScale
        viewModel.leftControllerStickNormalized = (lnx, lny)
        viewModel.rightControllerStickNormalized = (rnx, rny)
        
        // Only send isUp when the stick transitions from active to inactive; otherwise it would be misinterpreted as a chord, causing the right stick solo actions (e.g. NW to toggle caps) not to update the UI
        let leftRelease = !leftActive && prevBridgeLeftActive
        let rightRelease = !rightActive && prevBridgeRightActive
        if leftActive {
            handleTouch(dx: lnx * scale, dy: -lny * scale, isLeft: true, isDown: true, isUp: false)
        } else if leftRelease {
            handleTouch(dx: 0, dy: 0, isLeft: true, isDown: false, isUp: true)
        }
        if rightActive {
            handleTouch(dx: rnx * scale, dy: -rny * scale, isLeft: false, isDown: true, isUp: false)
        } else if rightRelease {
            handleTouch(dx: 0, dy: 0, isLeft: false, isDown: false, isUp: true)
        }
        prevBridgeLeftActive = leftActive
        prevBridgeRightActive = rightActive
    }
    
    // --- Core dispatch: feed iOS touch data to the Kotlin state machine ---
    func handleTouch(dx: Float, dy: Float, isLeft: Bool, isDown: Bool, isUp: Bool) {
        syncVisualState(dx: dx, dy: dy, isLeft: isLeft, isDown: isDown, isUp: isUp)
        stateMachine.handleTouch(x: dx, y: dy, isLeft: isLeft, actionDownOrMove: isDown, actionUp: isUp)
        
        // Fetch the latest preview from the state machine and update the UI (explicit notification ensures caps/shift mode changes are immediately reflected on the wheel)
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            self.refreshViewState()
        }
    }

    // ==========================================
    // Kotlin state machine delegate methods (Action Delegate)
    // ==========================================

    func commitText(text: String) {
        self.textDocumentProxy.insertText(text)
    }

    func onModeChanged(mode: KeyboardMode) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            self.mirroredMode = self.wheelMode(for: mode)
            self.refreshViewState()
        }
    }

    func sendInputAction(action: InputAction) {
        switch action {
        case .space:
            self.textDocumentProxy.insertText(" ")
        case .enter:
            self.textDocumentProxy.insertText("\n")
        case .backspace, .deleteForward:
            self.textDocumentProxy.deleteBackward()
        case .deleteWord:
            deleteWordBackward()
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
        case .dpadLeft:
            self.textDocumentProxy.adjustTextPosition(byCharacterOffset: -1)
        case .dpadRight:
            self.textDocumentProxy.adjustTextPosition(byCharacterOffset: 1)
        default:
            break
        }
    }

    private func deleteWordBackward() {
        guard let before = self.textDocumentProxy.documentContextBeforeInput, !before.isEmpty else {
            return
        }
        var i = before.endIndex
        // Skip trailing whitespace
        while i > before.startIndex && before[before.index(before: i)].isWhitespace {
            i = before.index(before: i)
        }
        // Skip word characters
        while i > before.startIndex && !before[before.index(before: i)].isWhitespace {
            i = before.index(before: i)
        }
        let charsToDelete = before.distance(from: i, to: before.endIndex)
        for _ in 0..<charsToDelete {
            self.textDocumentProxy.deleteBackward()
        }
    }

    func onSuggestionsUpdated(suggestions: [String]) {
        DispatchQueue.main.async { [weak self] in
            self?.viewModel.suggestions = suggestions
        }
    }

    func getCurrentWordPrefix() -> String {
        guard let before = self.textDocumentProxy.documentContextBeforeInput, !before.isEmpty else {
            return ""
        }
        var i = before.endIndex
        while i > before.startIndex {
            let prev = before.index(before: i)
            let ch = before[prev]
            if ch.isLetter || ch.isNumber || ch == "'" {
                i = prev
            } else {
                break
            }
        }
        return String(before[i...])
    }

    private func onSuggestionTapped(_ index: Int) {
        let suggestions = viewModel.suggestions
        guard index < suggestions.count else { return }
        let suggestion = suggestions[index]
        let wasNextWordMode = stateMachine.isNextWordMode
        let result = stateMachine.acceptSuggestion(suggestion: suggestion)
        let charsToDelete = result.first!.intValue
        let word = result.second! as String
        // Delete the partial word
        for _ in 0..<charsToDelete {
            self.textDocumentProxy.deleteBackward()
        }
        // In next-word mode, prepend a space if text before cursor doesn't already end with one
        if wasNextWordMode && charsToDelete == 0 {
            let before = self.textDocumentProxy.documentContextBeforeInput ?? ""
            if !before.isEmpty && !before.hasSuffix(" ") {
                self.textDocumentProxy.insertText(" ")
            }
        }
        // Insert the full suggestion
        self.textDocumentProxy.insertText(word)
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
        setupControllerInput()
        startControllerBridgePolling()
        refreshViewState()
    }

    private func handleSettingsChanged() {
        applyLayoutPreference()
        refreshViewState()
    }

    private func refreshViewState() {
        viewModel.objectWillChange.send()
        viewModel.previewText = stateMachine.getPreviewText()
        viewModel.leftDirection = mirroredLeftDirection
        viewModel.rightDirection = mirroredRightDirection
        viewModel.keyboardMode = mirroredMode
        viewModel.isEfficiency = isEfficiencyLayout
        viewModel.colorPaletteKey = currentColorPaletteKey
        viewModel.bothDialsAtHome = stateMachine.areBothDialsAtHome()
        updatePreviewState()
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
            viewModel.highlightedPreviewIndex = items.firstIndex(where: { $0.direction == mirroredRightDirection })
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

    private func previewItems(for direction: WheelDirection, mode: WheelMode) -> [KeyboardPreviewItem]? {
        let palette = ColorPaletteDefinitions.palette(for: currentColorPaletteKey)
        guard direction != .none else { return nil }

        let leftDir = sharedDirection(for: direction)
        let sharedMode = sharedMode(for: mode)
        let layoutType: LayoutType
        if isCustomLayout {
            layoutType = .custom
        } else if isEfficiencyLayout {
            layoutType = .efficiency
        } else {
            layoutType = .logical
        }

        let items = WheelDirection.orderedDirections.enumerated().compactMap { index, rightDirection -> KeyboardPreviewItem? in
            let text = keyboardLogic.getChordResult(
                leftDir: leftDir,
                rightDir: sharedDirection(for: rightDirection),
                mode: sharedMode,
                layout: layoutType
            )

            guard !text.isEmpty else { return nil }
            return KeyboardPreviewItem(
                id: index,
                direction: rightDirection,
                text: text,
                color: Color(hex: palette[index].hex)
            )
        }

        return items.isEmpty ? nil : items
    }

    /// Right-dial-only preview: returns the character at the given right-dial position
    /// across all 8 left-dial groups.
    private func rightDialPreviewItems(for direction: WheelDirection, mode: WheelMode) -> [KeyboardPreviewItem] {
        guard direction != .none else { return [] }

        let palette = ColorPaletteDefinitions.palette(for: currentColorPaletteKey)
        let dirIndex = wheelDirectionIndex(direction)
        guard dirIndex >= 0 && dirIndex < palette.count else { return [] }
        let color = Color(hex: palette[dirIndex].hex)

        let sharedRightDir = sharedDirection(for: direction)
        let sharedM = sharedMode(for: mode)
        let layoutType: LayoutType
        if isCustomLayout {
            layoutType = .custom
        } else if isEfficiencyLayout {
            layoutType = .efficiency
        } else {
            layoutType = .logical
        }

        let allLeftDirs: [WheelDirection] = [.n, .ne, .e, .se, .s, .sw, .w, .nw]
        var result: [KeyboardPreviewItem] = []
        var itemId = 0

        for leftDir in allLeftDirs {
            let text = keyboardLogic.getChordResult(
                leftDir: sharedDirection(for: leftDir),
                rightDir: sharedRightDir,
                mode: sharedM,
                layout: layoutType
            )
            if !text.isEmpty {
                result.append(KeyboardPreviewItem(id: itemId, direction: direction, text: text, color: color))
                itemId += 1
            }
        }

        return result
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

    private func sharedDirection(for direction: WheelDirection) -> Direction {
        switch direction {
        case .none: return .none
        case .n: return .n
        case .ne: return .ne
        case .e: return .e
        case .se: return .se
        case .s: return .s
        case .sw: return .sw
        case .w: return .w
        case .nw: return .nw
        }
    }

    private func sharedMode(for mode: WheelMode) -> KeyboardMode {
        switch mode {
        case .normal: return .normal
        case .shifted: return .shifted
        case .capsLocked: return .capsLocked
        }
    }

    private func wheelMode(for mode: KeyboardMode) -> WheelMode {
        switch mode {
        case .normal: return .normal
        case .shifted: return .shifted
        case .capsLocked: return .capsLocked
        default: return .normal
        }
    }
}

