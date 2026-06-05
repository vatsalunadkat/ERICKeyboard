import SwiftUI

struct TypingGameView: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.erickLanguageKey) private var keyboardLanguage
    
    @State private var currentQuoteIndex: Int = 0
    @State private var typedText: String = ""
    @State private var previousLength: Int = 0
    @State private var totalKeystrokes: Int = 0
    @State private var correctKeystrokes: Int = 0
    @State private var streak: Int = 0
    @State private var currentQuoteHasError: Bool = false
    @State private var startTime: Date? = nil
    @State private var totalCharsTyped: Int = 0
    @State private var wpm: Double = 0.0
    @State private var quoteOrder: [Int] = Array(0..<TypingGameView.quoteTemplates.count).shuffled()
    @State private var showCelebration: Bool = false
    @State private var celebrationTrigger: Int = 0
    @FocusState private var isInputFocused: Bool
    @State private var showHelpSheet = false
    
    @State private var wpmTimer: Timer? = nil
    
    private static let quoteTemplates: [String] = [
        "Be yourself; everyone else is already taken.",
        "Believe you can and you're halfway there.",
        "The secret of getting ahead is getting started.",
        "Breathe in deeply to bring your mind home to your body.",
        "Sometimes the most productive thing you can do is relax.",
        "Be gentle with yourself. You're doing the best you can.",
        "Call your parents once in a while.",
        "The road to success is always under construction.",
        "Life is short. Smile while you still have teeth.",
        "You are enough just as you are.",
        "Small steps in the right direction can turn out to be the biggest step of your life.",
        "Just breathe."
    ]

    private var localizedQuotes: [String] {
        Self.quoteTemplates.map { erickText($0, languageKey: keyboardLanguage) }
    }
    
    private var currentQuote: String {
        localizedQuotes[quoteOrder[currentQuoteIndex % localizedQuotes.count]]
    }
    
    private var accuracy: Double {
        totalKeystrokes > 0 ? (Double(correctKeystrokes) / Double(totalKeystrokes) * 100.0) : 100.0
    }
    
    private var bgColor: Color {
        colorScheme == .dark ? Color(red: 0.1, green: 0.1, blue: 0.18) : Color(red: 0.94, green: 0.90, blue: 1.0)
    }
    
    var body: some View {
        ZStack {
            bgColor.ignoresSafeArea()
            
            VStack(spacing: 16) {
                statsBar
                
                quoteCard
                
                // Invisible text input to capture keyboard input
                TextField("", text: Binding(
                    get: { typedText },
                    set: { newValue in
                        processInput(newValue)
                    }
                ))
                .focused($isInputFocused)
                .frame(width: 1, height: 1)
                .opacity(0.01)

                HStack(spacing: 12) {
                    Button(action: skipQuote) {
                        Text(erickText("Skip Quote", languageKey: keyboardLanguage))
                            .font(.subheadline)
                            .foregroundColor(.accentColor)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color.accentColor, lineWidth: 1)
                            )
                    }

                    Button(action: restartSession) {
                        Text(erickText("Restart Session", languageKey: keyboardLanguage))
                            .font(.subheadline)
                            .foregroundColor(.accentColor)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color.accentColor, lineWidth: 1)
                            )
                    }
                }
                .padding(.bottom, 8)

                Text(erickText("Start typing when you are ready.", languageKey: keyboardLanguage))
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .padding(.bottom, 24)
            }
            .padding(.horizontal, 20)

            if showCelebration {
                VStack(spacing: 10) {
                    CelebrationConfettiView(trigger: celebrationTrigger)
                        .frame(width: 180, height: 70)
                        .allowsHitTesting(false)

                    celebrationOverlay
                }
                .padding(.top, 28)
                .transition(.move(edge: .top).combined(with: .opacity))
                .allowsHitTesting(false)
            }
        }
        .navigationTitle(erickText("Typing Practice", languageKey: keyboardLanguage))
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(false)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button {
                    showHelpSheet = true
                } label: {
                    Image(systemName: "questionmark.circle")
                }
            }
        }
        .sheet(isPresented: $showHelpSheet) {
            TypingPracticeHelpSheet()
        }
        .onAppear {
            isInputFocused = true
            startWPMTimer()
        }
        .onDisappear {
            wpmTimer?.invalidate()
        }
    }
    
    // MARK: - Stats Bar
    
    private var statsBar: some View {
        HStack {
            statItem(value: "\(Int(wpm.rounded()))", label: erickText("WPM", languageKey: keyboardLanguage))
            Spacer()
            statItem(value: "\(Int(accuracy.rounded()))%", label: erickText("Accuracy", languageKey: keyboardLanguage))
            Spacer()
            statItem(value: "\(streak)", label: erickText("Streak", languageKey: keyboardLanguage))
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(uiColor: .secondarySystemBackground).opacity(0.7))
        )
    }
    
    private func statItem(value: String, label: String) -> some View {
        VStack(spacing: 2) {
            Text(value)
                .font(.title2)
                .fontWeight(.bold)
                .foregroundColor(.accentColor)
            Text(label)
                .font(.caption)
                .foregroundColor(.secondary)
        }
    }
    
    // MARK: - Quote Card
    
    private var quoteCard: some View {
        VStack(spacing: 16) {
            quoteDisplay
            
            Text("\(typedText.count) / \(currentQuote.count)")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding(24)
        .frame(maxWidth: .infinity, minHeight: 200)
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(Color(uiColor: .systemBackground).opacity(0.85))
                .shadow(color: .black.opacity(0.1), radius: 4, y: 2)
        )
        .onTapGesture {
            isInputFocused = true
        }
    }

    // MARK: - Quote Display

    private var quoteDisplay: some View {
        let chars = Array(currentQuote)
        let typedChars = Array(typedText)
        let hasError = typedChars.indices.contains(where: { index in
            index < chars.count && typedChars[index] != chars[index]
        })

        return WrappingHStack(quote: currentQuote, typedText: typedText)
            .modifier(ShakeModifier(isShaking: hasError))
    }

    // MARK: - Celebration

    private var celebrationOverlay: some View {
        HStack(spacing: 10) {
            Image(systemName: "sparkles")
                .font(.headline)
                .foregroundColor(.accentColor)

            VStack(alignment: .leading, spacing: 2) {
                Text(erickText("Perfect quote", languageKey: keyboardLanguage))
                    .font(.headline)
                    .fontWeight(.semibold)
                Text(erickText("Small win. Keep the streak going.", languageKey: keyboardLanguage))
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(
            Capsule(style: .continuous)
                .fill(Color(uiColor: .secondarySystemGroupedBackground))
                .shadow(color: .black.opacity(0.12), radius: 10, y: 4)
        )
    }

    private func triggerCelebration() {
        celebrationTrigger += 1

        withAnimation(.spring(response: 0.35, dampingFraction: 0.82)) {
            showCelebration = true
        }

        DispatchQueue.main.asyncAfter(deadline: .now() + 1.2) {
            withAnimation(.easeOut(duration: 0.2)) {
                showCelebration = false
            }
        }
    }

    // MARK: - Input Processing
    
    private func skipQuote() {
        currentQuoteIndex += 1
        if currentQuoteIndex >= quoteOrder.count {
            quoteOrder = Array(localizedQuotes.indices).shuffled()
            currentQuoteIndex = 0
        }
        typedText = ""
        currentQuoteHasError = false
    }

    private func restartSession() {
        currentQuoteIndex = 0
        typedText = ""
        previousLength = 0
        totalKeystrokes = 0
        correctKeystrokes = 0
        streak = 0
        currentQuoteHasError = false
        startTime = nil
        totalCharsTyped = 0
        wpm = 0.0
        quoteOrder = Array(0..<Self.quoteTemplates.count).shuffled()
    }

    private func processInput(_ newValue: String) {
        let quote = currentQuote
        let quoteChars = Array(quote)
        
        // Only count additions for scoring
        if newValue.count > typedText.count {
            let addedCount = newValue.count - typedText.count
            
            if startTime == nil {
                startTime = Date()
            }
            
            totalKeystrokes += addedCount
            totalCharsTyped += addedCount
            
            let newChars = Array(newValue)
            for i in typedText.count..<newValue.count {
                if i < quoteChars.count && newChars[i] == quoteChars[i] {
                    correctKeystrokes += 1
                } else {
                    currentQuoteHasError = true
                }
            }
        }
        
        typedText = newValue
        
        // Check if quote is completed
        if typedText.count >= quote.count {
            let isFullyCorrect = typedText == quote
            if isFullyCorrect && !currentQuoteHasError {
                streak += 1
                triggerCelebration()
            } else {
                streak = 0
            }
            
            currentQuoteIndex += 1
            if currentQuoteIndex >= quoteOrder.count {
                quoteOrder = Array(0..<Self.quoteTemplates.count).shuffled()
                currentQuoteIndex = 0
            }
            typedText = ""
            currentQuoteHasError = false
        }
    }
    
    private func startWPMTimer() {
        wpmTimer = Timer.scheduledTimer(withTimeInterval: 0.5, repeats: true) { _ in
            updateWPM()
        }
    }
    
    private func updateWPM() {
        guard let start = startTime, totalCharsTyped > 0 else { return }
        let elapsed = Date().timeIntervalSince(start)
        if elapsed > 0 {
            wpm = (Double(totalCharsTyped) / 5.0) / (elapsed / 60.0)
        }
    }
}

private struct TypingPracticeHelpSheet: View {
    @Environment(\.erickLanguageKey) private var keyboardLanguage

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    Text(erickText("Type the highlighted character next.", languageKey: keyboardLanguage))
                    Text(erickText("Tap the quote card if you need to refocus the hidden input field.", languageKey: keyboardLanguage))
                    Text(erickText("Skip a quote if it is not useful, or restart the session to clear the running stats.", languageKey: keyboardLanguage))
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding()
            }
            .navigationTitle(erickText("How quote practice works", languageKey: keyboardLanguage))
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

// MARK: - Wrapping Text Display

struct WrappingHStack: View {
    let quote: String
    let typedText: String
    
    var body: some View {
        let quoteChars = Array(quote)
        let typedChars = Array(typedText)
        
        let attributed = quoteChars.enumerated().map { (i, char) -> (Character, Color, Font.Weight, Bool, Bool) in
            if i < typedChars.count && typedChars[i] == char {
                return (char, Color(red: 0.3, green: 0.69, blue: 0.31), .bold, false, false) // Green - correct
            } else if i < typedChars.count {
                return (char, Color(red: 0.96, green: 0.26, blue: 0.21), .bold, false, false) // Red - incorrect
            } else if i == typedChars.count {
                return (char, .primary, .bold, false, true) // Current - highlighted
            } else {
                return (char, .secondary, .regular, false, false) // Not typed
            }
        }
        
        // Build attributed string
        let text = buildAttributedText(chars: attributed)
        
        Text(text)
            .font(.title3)
            .lineSpacing(8)
            .multilineTextAlignment(.center)
            .frame(maxWidth: .infinity)
    }
    
    private func buildAttributedText(chars: [(Character, Color, Font.Weight, Bool, Bool)]) -> AttributedString {
        var result = AttributedString()
        for (char, color, weight, _, isHighlighted) in chars {
            var attrChar = AttributedString(String(char))
            if isHighlighted {
                attrChar.foregroundColor = Color(uiColor: .systemBackground)
                attrChar.backgroundColor = .accentColor.opacity(0.6)
            } else {
                attrChar.foregroundColor = color
            }
            attrChar.font = .title3.weight(weight == .bold ? .bold : .regular)
            result.append(attrChar)
        }
        return result
    }
}

// MARK: - Celebration Confetti

private struct CelebrationConfettiView: View {
    let trigger: Int

    private let particles: [(x: CGFloat, y: CGFloat, color: Color)] = [
        (-68, -26, .yellow),
        (-54, -8, .orange),
        (-38, -34, .pink),
        (-22, -18, .blue),
        (-8, -30, .mint),
        (8, -28, .purple),
        (24, -12, .green),
        (40, -32, .red),
        (56, -10, .teal),
        (70, -24, .cyan)
    ]

    @State private var animate = false

    var body: some View {
        ZStack {
            ForEach(Array(particles.enumerated()), id: \.offset) { index, particle in
                RoundedRectangle(cornerRadius: 2)
                    .fill(particle.color)
                    .frame(width: 6, height: 10)
                    .rotationEffect(.degrees(Double(index * 24) + (animate ? 120 : 0)))
                    .offset(
                        x: animate ? particle.x : 0,
                        y: animate ? particle.y : -4
                    )
                    .opacity(animate ? 0 : 1)
                    .scaleEffect(animate ? 0.85 : 1.15)
            }
        }
        .onAppear {
            animate = false
            withAnimation(.easeOut(duration: 0.7)) {
                animate = true
            }
        }
        .id(trigger)
    }
}

// MARK: - Shake Modifier

struct ShakeModifier: ViewModifier {
    let isShaking: Bool
    @State private var shakeOffset: CGFloat = 0
    @State private var shakeActive: Bool = false
    
    func body(content: Content) -> some View {
        content
            .offset(x: shakeActive ? shakeOffset : 0)
            .onChange(of: isShaking) { shaking in
                if shaking && !shakeActive {
                    shakeActive = true
                    withAnimation(
                        .linear(duration: 0.08)
                        .repeatCount(18, autoreverses: true)
                    ) {
                        shakeOffset = 2
                    }
                    // Stop after ~1.5 seconds
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                        withAnimation {
                            shakeOffset = 0
                            shakeActive = false
                        }
                    }
                }
            }
    }
}

struct TypingGameView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            TypingGameView()
        }
    }
}
