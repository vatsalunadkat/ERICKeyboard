import UIKit
import SwiftUI
import GameController
import CoreHaptics
import AudioToolbox
import SharedKeyboard // Import the KMP shared module

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
    private var controllerHapticEngine: CHHapticEngine?
    private var localControllerTimer: Timer?
    private var prevLocalLeftActive = false
    private var prevLocalRightActive = false
    private var controllerBridgeTimer: Timer?
    private var prevBridgeLeftActive = false
    private var prevBridgeRightActive = false
    private var hasRegisteredControllerObservers = false
    private var isDispatchingControllerInput = false
    
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
        stopControllerHaptics()
    }
    
    // MARK: - GameController (DualShock 4, etc.)
    private static let controllerDeadZone: Float = 0.25
    
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
            self.stopControllerHaptics()
            self.currentController = nil
            self.localControllerTimer?.invalidate()
            self.localControllerTimer = nil
            self.prevLocalLeftActive = false
            self.prevLocalRightActive = false
            self.viewModel.leftControllerStickNormalized = (0, 0)
            self.viewModel.rightControllerStickNormalized = (0, 0)
            self.dispatchControllerInput(leftX: 0, leftY: 0, rightX: 0, rightY: 0)
            self.refreshViewState()
        }
    }
    
    private func setupCurrentController() {
        guard let controller = GCController.controllers().first,
              let _ = controller.extendedGamepad else { return }

        if currentController !== controller {
            currentController = controller
            prepareControllerHaptics()
        } else if controllerHapticEngine == nil {
            prepareControllerHaptics()
        }

        startLocalControllerPolling()
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

        let lx = extended.leftThumbstick.xAxis.value
        let ly = extended.leftThumbstick.yAxis.value
        let rx = extended.rightThumbstick.xAxis.value
        let ry = extended.rightThumbstick.yAxis.value

        let leftNorm = normalizedControllerStick(x: lx, y: ly)
        let rightNorm = normalizedControllerStick(x: rx, y: ry)
        viewModel.leftControllerStickNormalized = leftNorm
        viewModel.rightControllerStickNormalized = rightNorm

        dispatchControllerInput(leftX: lx, leftY: ly, rightX: rx, rightY: ry)

        DispatchQueue.main.async { [weak self] in
            self?.refreshViewState()
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

    private func dispatchControllerInput(leftX: Float, leftY: Float, rightX: Float, rightY: Float) {
        isDispatchingControllerInput = true
        defer { isDispatchingControllerInput = false }
        stateMachine.handleControllerInput(leftX: leftX, leftY: leftY, rightX: rightX, rightY: rightY)
    }

    private func prepareControllerHaptics() {
        stopControllerHaptics()
        controllerHapticEngine = currentController?.haptics?.createEngine(withLocality: GCHapticsLocality.default)
    }

    private func stopControllerHaptics() {
        controllerHapticEngine?.stop(completionHandler: nil)
        controllerHapticEngine = nil
    }

    private func performControllerHaptic(strong: Bool) {
        if controllerHapticEngine == nil {
            prepareControllerHaptics()
        }
        guard let engine = controllerHapticEngine else { return }

        let intensity: Float = strong ? 0.85 : 0.45
        let sharpness: Float = strong ? 0.55 : 0.3
        let parameters = [
            CHHapticEventParameter(parameterID: CHHapticEvent.ParameterID.hapticIntensity, value: intensity),
            CHHapticEventParameter(parameterID: CHHapticEvent.ParameterID.hapticSharpness, value: sharpness)
        ]
        let event = CHHapticEvent(eventType: .hapticTransient, parameters: parameters, relativeTime: 0)

        do {
            try engine.start()
            let pattern = try CHHapticPattern(events: [event], parameters: [])
            let player = try engine.makePlayer(with: pattern)
            try player.start(atTime: CHHapticTimeImmediate)
        } catch {
            stopControllerHaptics()
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
                // Send zeroed input to release any held directions
                dispatchControllerInput(leftX: 0, leftY: 0, rightX: 0, rightY: 0)
                DispatchQueue.main.async { [weak self] in
                    self?.refreshViewState()
                }
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

        viewModel.leftControllerStickNormalized = (lnx, lny)
        viewModel.rightControllerStickNormalized = (rnx, rny)

        // Route through the KMP state machine's dedicated controller handler
        // Note: bridge values are already dead-zone-filtered and normalized [0..1]
        // by ControllerBridge.tick(), so pass them as raw axis values and let
        // the state machine's normalizeControllerStick() handle scaling.
        dispatchControllerInput(leftX: lnx, leftY: lny, rightX: rnx, rightY: rny)

        DispatchQueue.main.async { [weak self] in
            self?.refreshViewState()
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
        performHaptic(strong: false)
        playClickSound(soft: true)
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
        performHaptic(strong: true)
        playClickSound(soft: false)
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

    private func performHaptic(strong: Bool) {
        guard Self.appGroupDefaults.bool(forKey: "haptic_feedback") else { return }
        if isDispatchingControllerInput {
            performControllerHaptic(strong: strong)
        }
        let style: UIImpactFeedbackGenerator.FeedbackStyle = strong ? .medium : .light
        let generator = UIImpactFeedbackGenerator(style: style)
        generator.prepare()
        generator.impactOccurred()
    }

    private func playClickSound(soft: Bool) {
        guard Self.appGroupDefaults.bool(forKey: "typing_sounds") else { return }
        // 1104 = standard keyboard click for both letter and utility keys
        AudioServicesPlaySystemSound(1104)
    }

    func onSuggestionsUpdated(suggestions: [String]) {
        DispatchQueue.main.async { [weak self] in
            self?.viewModel.suggestions = suggestions
            self?.viewModel.suggestionContextLabel = self?.suggestionContextLabel(for: suggestions) ?? ""
        }
    }

    func loadPredictionProfile() -> String {
        Self.appGroupDefaults.string(forKey: "prediction_profile") ?? ""
    }

    func savePredictionProfile(serializedProfile: String) {
        Self.appGroupDefaults.set(serializedProfile, forKey: "prediction_profile")
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
        let before = self.textDocumentProxy.documentContextBeforeInput ?? ""
        let after = self.textDocumentProxy.documentContextAfterInput ?? ""
        let result = stateMachine.acceptSuggestion(
            suggestion: suggestion,
            textBeforeCursor: before,
            textAfterCursor: after
        )
        let charsToDelete = result.charsToDelete.intValue
        // Delete the partial word
        for _ in 0..<charsToDelete {
            self.textDocumentProxy.deleteBackward()
        }
        self.textDocumentProxy.insertText(result.leadingText + result.suggestion + result.trailingText)
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
        let palette = Self.appGroupDefaults.string(forKey: "color_palette") ?? "okabe_ito"
        if enabled {
            return palette
        } else {
            // When colorblind mode is off, still honor pastel and custom palette selections
            if palette == "pastel" || palette == "custom" {
                return palette
            }
            return "default"
        }
    }

    private var isLeftHandedMode: Bool {
        return Self.appGroupDefaults.bool(forKey: "left_handed_mode")
    }

    private func applyLayoutPreference() {
        let languageKey = Self.appGroupDefaults.string(forKey: "keyboard_language") ?? "english"
        let keyboardLanguage: KeyboardLanguage
        switch languageKey {
        case "spanish":
            keyboardLanguage = .spanish
        case "portuguese":
            keyboardLanguage = .portuguese
        case "french":
            keyboardLanguage = .french
        case "german":
            keyboardLanguage = .german
        case "italian":
            keyboardLanguage = .italian
        case "norwegian_bokmal":
            keyboardLanguage = .norwegian_bokmal
        case "danish":
            keyboardLanguage = .danish
        case "swedish":
            keyboardLanguage = .swedish
        case "finnish":
            keyboardLanguage = .finnish
        default:
            keyboardLanguage = .english
        }
        stateMachine.setKeyboardLanguage(language: keyboardLanguage)

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

        // Apply 6-section dial mode
        let sixSection = Self.appGroupDefaults.bool(forKey: "six_section_dial")
        let dialMode: DialSectionMode = sixSection ? .sixSection : .eightSection
        stateMachine.setDialSectionMode(mode: dialMode)
        keyboardLogic.dialSectionMode = dialMode
        viewModel.sixSectionMode = sixSection

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

        // Apply input mode
        let inputModeStr = Self.appGroupDefaults.string(forKey: "input_mode") ?? "instant"
        let inputMode: InputMode
        switch inputModeStr {
        case "confirm":
            inputMode = .confirm
        case "assisted":
            inputMode = .assisted
        default:
            inputMode = .instant
        }
        stateMachine.setInputMode(mode: inputMode)

        let predictionDomainKey = Self.appGroupDefaults.string(forKey: "prediction_domain") ?? "general"
        let predictionDomain: PredictionDomain
        switch predictionDomainKey {
        case "conversation":
            predictionDomain = .conversation
        case "productivity":
            predictionDomain = .productivity
        case "accessibility":
            predictionDomain = .accessibility
        case "gaming":
            predictionDomain = .gaming
        default:
            predictionDomain = .general
        }
        stateMachine.setPredictionDomain(domain: predictionDomain)
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
        viewModel.paletteRefreshToken += 1
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
        viewModel.lockedLeftDirection = wheelDirection(for: stateMachine.lockedLeftDir)
        viewModel.suggestionContextLabel = suggestionContextLabel(for: viewModel.suggestions)
        updatePreviewState()
    }

    private func suggestionContextLabel(for suggestions: [String]) -> String {
        guard !suggestions.isEmpty else { return "" }

        let baseLabel: String
        if stateMachine.isNextWordMode {
            baseLabel = "Next"
        } else {
            let prefix = getCurrentWordPrefix().lowercased()
            let hasCorrection = suggestions.contains { !$0.lowercased().hasPrefix(prefix) }
            baseLabel = hasCorrection ? "Complete/Correct" : "Complete"
        }

        let domainLabel: String
        switch Self.appGroupDefaults.string(forKey: "prediction_domain") ?? "general" {
        case "conversation":
            domainLabel = "Chat"
        case "productivity":
            domainLabel = "Work"
        case "accessibility":
            domainLabel = "Support"
        case "gaming":
            domainLabel = "Gaming"
        default:
            domainLabel = ""
        }

        return domainLabel.isEmpty ? baseLabel : "\(baseLabel) • \(domainLabel)"
    }

    private func syncVisualState(dx: Float, dy: Float, isLeft: Bool, isDown: Bool, isUp: Bool) {
        let currentDirection = direction(forX: dx, y: dy)
        let effectiveIsLeft = viewModel.isLeftHanded ? !isLeft : isLeft
        let currentInputMode = Self.appGroupDefaults.string(forKey: "input_mode") ?? "instant"

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
                } else if mirroredMode == .symbolsShifted {
                    mirroredMode = .symbols
                }
            }
            mirroredLeftDirection = .none
        } else {
            if mirroredLeftDirection != .none && !mirroredChordExecuted {
                mirroredChordExecuted = true
                if mirroredMode == .shifted {
                    mirroredMode = .normal
                } else if mirroredMode == .symbolsShifted {
                    mirroredMode = .symbols
                }
                // In Quick Type (instant), allow subsequent right swipes while left held
                if currentInputMode == "instant" {
                    mirroredChordExecuted = false
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
        if viewModel.sixSectionMode {
            switch direction {
            case .ne:
                if mirroredMode == .symbols || mirroredMode == .symbolsShifted {
                    mirroredMode = mirroredMode == .symbols ? .symbolsShifted : .symbols
                } else {
                    mirroredMode = mirroredMode == .normal ? .shifted : .normal
                }
            case .n:
                if mirroredMode == .symbols || mirroredMode == .symbolsShifted {
                    mirroredMode = .normal
                } else {
                    mirroredMode = .symbols
                }
            default:
                break
            }
        } else {
            switch direction {
            case .sw:
                mirroredMode = mirroredMode == .normal ? .shifted : .normal
            case .nw:
                mirroredMode = mirroredMode == .capsLocked ? .normal : .capsLocked
            default:
                break
            }
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

        if viewModel.sixSectionMode {
            // 6-section mode: 60° segments, rotated -30° so horizontal = Space/Backspace
            // SE: 330-30 (center 0°, right), S: 30-90, SW: 90-150
            // NW: 150-210 (center 180°, left), N: 210-270, NE: 270-330
            switch degrees {
            case 330..., ..<30:
                return .se
            case 30..<90:
                return .s
            case 90..<150:
                return .sw
            case 150..<210:
                return .nw
            case 210..<270:
                return .n
            case 270..<330:
                return .ne
            default:
                return .none
            }
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
        let isSix = viewModel.sixSectionMode
        let palette = isSix ? ColorPaletteDefinitions.palette6(for: currentColorPaletteKey) : ColorPaletteDefinitions.palette(for: currentColorPaletteKey)
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

        let directions = isSix ? WheelDirection.orderedDirections6 : WheelDirection.orderedDirections
        let items = directions.enumerated().compactMap { index, rightDirection -> KeyboardPreviewItem? in
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

        let isSix = viewModel.sixSectionMode
        let palette = isSix ? ColorPaletteDefinitions.palette6(for: currentColorPaletteKey) : ColorPaletteDefinitions.palette(for: currentColorPaletteKey)
        let dirIndex = isSix ? wheelDirectionIndex6(direction) : wheelDirectionIndex(direction)
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

        let allLeftDirs: [WheelDirection] = isSix ? [.n, .ne, .se, .s, .sw, .nw] : [.n, .ne, .e, .se, .s, .sw, .w, .nw]
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

    private func wheelDirectionIndex6(_ dir: WheelDirection) -> Int {
        switch dir {
        case .n: return 0; case .ne: return 1; case .se: return 2
        case .s: return 3; case .sw: return 4; case .nw: return 5
        default: return -1
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
        case .symbols: return .symbols
        case .symbolsShifted: return .symbolsShifted
        }
    }

    private func wheelMode(for mode: KeyboardMode) -> WheelMode {
        switch mode {
        case .normal: return .normal
        case .shifted: return .shifted
        case .capsLocked: return .capsLocked
        case .symbols: return .symbols
        case .symbolsShifted: return .symbolsShifted
        default: return .normal
        }
    }

    private func wheelDirection(for direction: Direction) -> WheelDirection {
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
        default: return .none
        }
    }
}

