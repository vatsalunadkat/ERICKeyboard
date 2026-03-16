import SwiftUI

struct ContentView: View {
    @Environment(\.scenePhase) var scenePhase
    @AppStorage("hasEnabledKeyboard") private var hasEnabledKeyboard = false
    @State private var isKeyboardActuallyEnabled: Bool = false
    @State private var testText: String = ""
    
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
                        
                        Text("Welcome to ERICKeyboard")
                            .font(.title)
                            .fontWeight(.bold)
                            .multilineTextAlignment(.center)
                        
                        Text("A radial chorded keyboard for everyone")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .frame(maxWidth: .infinity)
                    
                    // Success or Instructions
                    if isStep1Completed {
                        // All good!
                        VStack(spacing: 12) {
                            Image(systemName: "checkmark.circle.fill")
                                .resizable()
                                .frame(width: 48, height: 48)
                                .foregroundColor(.green)
                            
                            Text("Keyboard is Enabled!")
                                .font(.title2)
                                .fontWeight(.bold)
                                .foregroundColor(.green)
                            
                            Text("You're ready to use ERICKeyboard")
                                .font(.body)
                                .foregroundColor(.secondary)
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.green.opacity(0.1))
                        .cornerRadius(16)
                    } else {
                        Text("Setup Instructions:")
                            .font(.title2)
                            .fontWeight(.bold)
                            .padding(.bottom, -8)
                    }
                    
                    // Step 1: Enable Keyboard
                    StepCard(
                        stepNumber: "1",
                        title: "Enable the Keyboard",
                        isCompleted: isStep1Completed,
                        activeColor: Color(red: 244/255, green: 67/255, blue: 54/255), // Red badge color
                        activeIcon: "xmark",
                        activeContainerColor: Color(red: 255/255, green: 235/255, blue: 238/255) // Light red container color
                    ) {
                        VStack(alignment: .leading, spacing: 16) {
                            Text("Go to Settings → General → Keyboard → Keyboards → Add New Keyboard → ERICKeyboard")
                                .font(.body)
                                .foregroundColor(.secondary)
                                .fixedSize(horizontal: false, vertical: true)
                            
                            // Privacy & Security Card
                            VStack(alignment: .leading, spacing: 12) {
                                Text("🔒 Privacy & Security")
                                    .font(.headline)
                                Text("Your privacy is our priority. ERICKeyboard:")
                                    .font(.subheadline)
                                    .fontWeight(.semibold)
                                
                                VStack(alignment: .leading, spacing: 6) {
                                    PrivacyRequirement(text: "We never collect or store your typed text")
                                    PrivacyRequirement(text: "No passwords or personal data are saved")
                                    PrivacyRequirement(text: "No data is transmitted — ever")
                                    PrivacyRequirement(text: "Settings are stored locally on your device only")
                                    PrivacyRequirement(text: "No internet permissions requested")
                                    PrivacyRequirement(text: "100% open-source: inspect every line of code")
                                }
                            }
                            .padding()
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .background(Color(red: 234/255, green: 221/255, blue: 255/255)) // Matches Android's tertiary container purple
                            .cornerRadius(12)
                            
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
                                .background(Color(red: 87/255, green: 99/255, blue: 128/255)) // Matches Android primary button color
                                .foregroundColor(.white)
                                .cornerRadius(12)
                            }
                            
                            Toggle("I've enabled ERICKeyboard", isOn: $hasEnabledKeyboard)
                                .toggleStyle(SwitchToggleStyle(tint: .green))
                                .padding(.top, 4)
                        }
                    }
                    
                    // Step 2: Switch to Keyboard
                    StepCard(
                        stepNumber: "2",
                        title: "Switch to ERICK",
                        isCompleted: false, // Manual only step
                        activeColor: Color(red: 244/255, green: 67/255, blue: 54/255),
                        activeIcon: "exclamationmark.triangle.fill",
                        activeContainerColor: Color(red: 255/255, green: 235/255, blue: 238/255)
                    ) {
                        Text("When typing, tap or press and hold the globe 🌐 icon on the keyboard to switch to ERICK.")
                            .font(.body)
                            .foregroundColor(.secondary)
                            .fixedSize(horizontal: false, vertical: true)
                    }
                    
                    // Test Field
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Test Your Keyboard:")
                            .font(.headline)
                        
                        TextField("Tap here to test the keyboard", text: $testText, axis: .vertical)
                            .lineLimit(4...8)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                    }
                    .padding(.top, 8)
                    
                    // Tips Section
                    VStack(alignment: .leading, spacing: 16) {
                        Text("💡 Tips:")
                            .font(.headline)
                        
                        VStack(alignment: .leading, spacing: 10) {
                            TipRow(text: "Use the left joystick to select a character group")
                            TipRow(text: "Use the right joystick to select a character within the group")
                            TipRow(text: "Swipe the right joystick alone for utility functions (space, enter, backspace)")
                        }
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(uiColor: .secondarySystemBackground))
                    .cornerRadius(16)
                    
                }
                .padding()
            }
            .navigationBarTitleDisplayMode(.inline)
        }
        .onAppear {
            checkKeyboardStatus()
        }
        .onChange(of: scenePhase) { newPhase in
            if newPhase == .active {
                checkKeyboardStatus()
            }
        }
    }
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

#Preview {
    ContentView()
}
