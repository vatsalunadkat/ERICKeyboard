import SwiftUI
import GameController

struct ContentView: View {
    @Environment(\.erickLanguageKey) private var keyboardLanguage
    @Environment(\.scenePhase) var scenePhase
    @AppStorage("hasEnabledKeyboard") private var hasEnabledKeyboard = false
    @AppStorage(LearningProgressStore.quickstartCompletedKey) private var quickstartCompleted = false
    @AppStorage(LearningProgressStore.quickstartDismissedKey) private var quickstartDismissed = false
    @AppStorage(LearningProgressStore.quickstartStepKey) private var quickstartStep = 0
    @State private var isKeyboardActuallyEnabled: Bool = false
    @State private var testText: String = ""
    @State private var showTypingGame: Bool = false
    @State private var showQuickstart: Bool = false
    @State private var infoSheet: HomeInfoSheet?
    
    private var isStep1Completed: Bool {
        hasEnabledKeyboard || isKeyboardActuallyEnabled
    }
    
    private func checkKeyboardStatus() {
        if let keyboards = UserDefaults.standard.object(forKey: "AppleKeyboards") as? [String] {
            // Check if any enabled keyboard identifier contains "erick" (case insensitive)
            let actuallyEnabled = keyboards.contains { $0.localizedCaseInsensitiveContains("erick") } 
            
            isKeyboardActuallyEnabled = actuallyEnabled
            
            // Sync the manual toggle so it unchecks if the user disables it in Settings
            if actuallyEnabled {
                hasEnabledKeyboard = true
            } else {
                hasEnabledKeyboard = false
            }
        }
    }
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 24) {
                    // Header
                    VStack(spacing: 8) {
                        Image("erick_logo")
                            .resizable()
                            .scaledToFit()
                            .frame(height: 70)
                            .padding(.top, 10)
                        
                        Text(erickText("Welcome to ERICKeyboard", languageKey: keyboardLanguage))
                            .font(.title)
                            .fontWeight(.bold)
                            .multilineTextAlignment(.center)
                        
                        Text(erickText("A radial chorded keyboard for everyone", languageKey: keyboardLanguage))
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .frame(maxWidth: .infinity)
                    
                    // Help & Settings buttons
                    HStack(spacing: 12) {
                        NavigationLink(destination: HelpView()) {
                            HStack {
                                Text("📖")
                                Text(erickText("How to Type", languageKey: keyboardLanguage))
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color.accentColor, lineWidth: 1)
                            )
                        }

                        NavigationLink(destination: SettingsView()) {
                            HStack {
                                Image(systemName: "gearshape")
                                Text(erickText("Settings", languageKey: keyboardLanguage))
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color.accentColor, lineWidth: 1)
                            )
                        }
                    }

                    // Success or Instructions
                    if isStep1Completed {
                        VStack(spacing: 12) {
                            Image(systemName: "checkmark.circle.fill")
                                .resizable()
                                .frame(width: 48, height: 48)
                                .foregroundColor(.green)
                            
                            Text(erickText("Keyboard is Enabled!", languageKey: keyboardLanguage))
                                .font(.title2)
                                .fontWeight(.bold)
                                .foregroundColor(.green)
                            
                            Text(erickText("You're ready to use ERICKeyboard", languageKey: keyboardLanguage))
                                .font(.body)
                                .foregroundColor(.secondary)

                            Text("Use the globe key in any text field if iOS shows another keyboard first.")
                                .font(.footnote)
                                .foregroundColor(.secondary)
                                .multilineTextAlignment(.center)
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.green.opacity(0.1))
                        .cornerRadius(16)
                        .environment(\.colorScheme, .light)

                        VStack(alignment: .leading, spacing: 12) {
                            HStack {
                                Text(erickText("Try ERICK", languageKey: keyboardLanguage))
                                    .font(.headline)
                                Spacer()
                                Button {
                                    infoSheet = .tryErick
                                } label: {
                                    Image(systemName: "questionmark.circle")
                                        .font(.title3)
                                }
                                .buttonStyle(.plain)
                            }

                            TextField(erickText("Type here to test ERICK", languageKey: keyboardLanguage), text: $testText, axis: .vertical)
                                .lineLimit(4...8)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                                .onChange(of: testText) { newValue in
                                    if newValue.trimmingCharacters(in: .whitespaces).caseInsensitiveCompare("start") == .orderedSame {
                                        testText = ""
                                        showTypingGame = true
                                    }
                                }

                            Text(erickText("Type start to open quote practice.", languageKey: keyboardLanguage))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        .padding()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color(uiColor: .secondarySystemBackground))
                        .cornerRadius(16)
                    } else {
                        Text(erickText("Finish setup", languageKey: keyboardLanguage))
                            .font(.title2)
                            .fontWeight(.bold)
                            .padding(.bottom, -8)
                        
                        StepCard(
                            stepNumber: "1",
                            title: erickText("Enable the Keyboard", languageKey: keyboardLanguage),
                            isCompleted: isStep1Completed,
                            activeColor: Color(red: 244/255, green: 67/255, blue: 54/255),
                            activeIcon: "xmark",
                            activeContainerColor: Color(red: 255/255, green: 235/255, blue: 238/255)
                        ) {
                            VStack(alignment: .leading, spacing: 16) {
                                Text("Open Settings and add ERICKeyboard under General → Keyboard → Keyboards.")
                                    .font(.body)
                                    .foregroundColor(.secondary)
                                    .fixedSize(horizontal: false, vertical: true)

                                Button {
                                    infoSheet = .privacy
                                } label: {
                                    Label(erickText("Privacy & Security", languageKey: keyboardLanguage), systemImage: "questionmark.circle")
                                        .frame(maxWidth: .infinity)
                                }
                                .buttonStyle(.bordered)

                                Button(action: {
                                    if let url = URL(string: UIApplication.openSettingsURLString) {
                                        UIApplication.shared.open(url)
                                    }
                                }) {
                                    HStack {
                                        Image(systemName: "gearshape.fill")
                                        Text("Open Settings")
                                    }
                                    .font(.headline)
                                    .frame(maxWidth: .infinity)
                                    .padding()
                                    .background(Color(red: 87/255, green: 99/255, blue: 128/255))
                                    .foregroundColor(.white)
                                    .cornerRadius(12)
                                }

                                Text("Return here after enabling it. ERICK will check again automatically.")
                                    .font(.footnote)
                                    .foregroundColor(.secondary)
                            }
                        }

                        StepCard(
                            stepNumber: "2",
                            title: "Switch with Globe",
                            isCompleted: false,
                            activeColor: Color(red: 244/255, green: 67/255, blue: 54/255),
                            activeIcon: "exclamationmark.triangle.fill",
                            activeContainerColor: Color(red: 255/255, green: 235/255, blue: 238/255)
                        ) {
                            Text("When you start typing, use the globe key to choose ERICK.")
                                .font(.body)
                                .foregroundColor(.secondary)
                                .fixedSize(horizontal: false, vertical: true)
                        }
                    }
                    
                    ControllerStatusCard()

                    BenefitsOverviewSection()
                    
                }
                .padding()
            }
            .navigationBarTitleDisplayMode(.inline)
            .navigationDestination(isPresented: $showTypingGame) {
                TypingGameView()
            }
        }
        .onAppear {
            checkKeyboardStatus()
            ControllerBridge.shared.start()
            if !quickstartCompleted && !quickstartDismissed {
                quickstartDismissed = true
                quickstartStep = 0
                showQuickstart = true
            }
        }
        .onChange(of: scenePhase) { newPhase in
            switch newPhase {
            case .active:
                checkKeyboardStatus()
                ControllerBridge.shared.start()
            case .inactive:
                // Keep the bridge running when switching apps so the keyboard
                // extension can still read controller data via App Group.
                break
            case .background:
                // iOS may suspend the host app shortly after backgrounding.
                // Keep the bridge alive so its CADisplayLink continues as long
                // as the process is active.
                break
            @unknown default:
                break
            }
        }
        .onReceive(NotificationCenter.default.publisher(for: .GCControllerDidConnect)) { _ in
            // Controller Status card will refresh via @State when it next appears
        }
        .onReceive(NotificationCenter.default.publisher(for: .GCControllerDidDisconnect)) { _ in
            // Controller Status card will refresh via @State when it next appears
        }
        .sheet(isPresented: $showQuickstart) {
            QuickstartView(
                currentStep: $quickstartStep,
                onComplete: {
                    quickstartCompleted = true
                    quickstartDismissed = false
                    quickstartStep = 0
                    showQuickstart = false
                },
                onSkip: {
                    quickstartDismissed = true
                    showQuickstart = false
                }
            )
        }
        .sheet(item: $infoSheet) { sheet in
            HomeInfoSheetView(sheet: sheet)
        }
    }
}

private enum HomeInfoSheet: String, Identifiable {
    case privacy
    case tryErick

    var id: String { rawValue }
}

struct StepCard<Content: View>: View {
    let stepNumber: String
    let title: String
    let isCompleted: Bool
    let activeColor: Color
    let activeIcon: String
    let activeContainerColor: Color?
    let content: Content
    
    init(stepNumber: String, title: String, isCompleted: Bool, activeColor: Color = Color(red: 244/255, green: 67/255, blue: 54/255), activeIcon: String = "xmark", activeContainerColor: Color? = Color(red: 255/255, green: 235/255, blue: 238/255), @ViewBuilder content: () -> Content) {
        self.stepNumber = stepNumber
        self.title = title
        self.isCompleted = isCompleted
        self.activeColor = activeColor
        self.activeIcon = activeIcon
        self.activeContainerColor = activeContainerColor
        self.content = content()
    }
    
    var stateColor: Color {
        isCompleted ? .green : activeColor
    }
    
    var containerColor: Color {
        isCompleted ? Color.green.opacity(0.1) : (activeContainerColor ?? stateColor.opacity(0.1))
    }
    
    var iconName: String {
        isCompleted ? "checkmark.circle.fill" : activeIcon
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(alignment: .top, spacing: 12) {
                // Number Badge
                ZStack {
                    RoundedRectangle(cornerRadius: 8)
                        .fill(stateColor)
                        .frame(width: 32, height: 32)
                    Text(stepNumber)
                        .font(.headline)
                        .foregroundColor(.white)
                }
                
                VStack(alignment: .leading, spacing: 8) {
                    Text(title)
                        .font(.headline)
                        .padding(.top, 4)
                    
                    if !isCompleted {
                        content
                    }
                }
                
                Spacer()
                
                Image(systemName: iconName)
                    .foregroundColor(stateColor)
                    .font(.title2)
                    .padding(.top, 4)
            }
        }
        .padding()
        .background(containerColor)
        .cornerRadius(16)
        .environment(\.colorScheme, .light)
    }
}

struct PrivacyRequirement: View {
    let text: String
    var body: some View {
        HStack(alignment: .top, spacing: 8) {
            Text("✓")
                .foregroundColor(.green)
                .fontWeight(.bold)
            Text(text)
                .font(.footnote)
                .foregroundColor(.primary)
                .fixedSize(horizontal: false, vertical: true)
        }
    }
}

struct TipRow: View {
    let text: String
    var body: some View {
        HStack(alignment: .top, spacing: 10) {
            Text("•")
                .fontWeight(.bold)
            Text(text)
                .font(.subheadline)
                .fixedSize(horizontal: false, vertical: true)
        }
    }
}

private struct HomeInfoSheetView: View {
    @Environment(\.erickLanguageKey) private var keyboardLanguage
    let sheet: HomeInfoSheet

    private var title: String {
        switch sheet {
        case .privacy:
            return erickText("Privacy & Security", languageKey: keyboardLanguage)
        case .tryErick:
            return erickText("Try ERICK", languageKey: keyboardLanguage)
        }
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    switch sheet {
                    case .privacy:
                        Text(erickText("ERICKeyboard keeps your typing on your device.", languageKey: keyboardLanguage))
                            .font(.body)
                        VStack(alignment: .leading, spacing: 8) {
                            PrivacyRequirement(text: erickText("We never collect or store your typed text", languageKey: keyboardLanguage))
                            PrivacyRequirement(text: erickText("Passwords and personal data stay on your device", languageKey: keyboardLanguage))
                            PrivacyRequirement(text: erickText("No text is transmitted from the keyboard", languageKey: keyboardLanguage))
                            PrivacyRequirement(text: erickText("Settings are stored locally on your device only", languageKey: keyboardLanguage))
                            PrivacyRequirement(text: erickText("No internet permissions are requested for typing data", languageKey: keyboardLanguage))
                            PrivacyRequirement(text: erickText("The project is open source for inspection", languageKey: keyboardLanguage))
                        }

                    case .tryErick:
                        Text(erickText("Use the test field to make sure the current keyboard and layout feel right.", languageKey: keyboardLanguage))
                            .font(.body)
                        VStack(alignment: .leading, spacing: 10) {
                            TipRow(text: erickText("Tap the field and type a short word or sentence.", languageKey: keyboardLanguage))
                            TipRow(text: erickText("If another keyboard appears, switch back to ERICK with the globe key", languageKey: keyboardLanguage))
                            TipRow(text: erickText("Type start to open quote practice.", languageKey: keyboardLanguage))
                            TipRow(text: erickText("Use Practice Lessons for guided drills instead of memorizing everything here.", languageKey: keyboardLanguage))
                        }
                    }
                }
                .padding()
            }
            .navigationTitle(title)
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

struct ControllerStatusCard: View {
    @Environment(\.erickLanguageKey) private var keyboardLanguage
    @State private var controllerName: String?
    
    var body: some View {
        GroupBox {
            HStack(spacing: 12) {
                Image(systemName: "gamecontroller.fill")
                    .font(.title2)
                    .foregroundColor(.secondary)
                VStack(alignment: .leading, spacing: 4) {
                    Text(erickText("Controller Status", languageKey: keyboardLanguage))
                        .font(.headline)
                    if let name = controllerName, !name.isEmpty {
                        Text("✅ \(erickText("Connected", languageKey: keyboardLanguage)): \(name)")
                            .foregroundColor(.green)
                            .font(.subheadline)
                    } else {
                        Text(erickText("No controller detected", languageKey: keyboardLanguage))
                            .foregroundColor(.secondary)
                            .font(.subheadline)
                    }
                }
                Spacer()
            }
        }
        .onAppear { checkController() }
        .onReceive(NotificationCenter.default.publisher(for: .GCControllerDidConnect)) { _ in
            checkController()
        }
        .onReceive(NotificationCenter.default.publisher(for: .GCControllerDidDisconnect)) { _ in
            checkController()
        }
    }
    
    private func checkController() {
        controllerName = GCController.controllers().first?.vendorName
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
