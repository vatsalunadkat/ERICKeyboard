import SwiftUI

struct TypingGameView: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.colorScheme) private var colorScheme
    
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
    @State private var quoteOrder: [Int] = Array(0..<TypingGameView.quotes.count).shuffled()
    @FocusState private var isInputFocused: Bool
    
    @State private var wpmTimer: Timer? = nil
    
    private static let quotes: [String] = [
        // Motivational
        "Be yourself; everyone else is already taken.",
        "It's not the load that breaks you down, it's the way you carry it.",
        "For every minute you are angry, you lose sixty seconds of happiness.",
        "The only way to do great work is to love what you do.",
        "In the middle of difficulty lies opportunity.",
        "Believe you can and you're halfway there.",
        "The best time to plant a tree was twenty years ago. The second best time is now.",
        "You are never too old to set another goal or to dream a new dream.",
        "Happiness is not something ready made. It comes from your own actions.",
        "Every moment is a fresh beginning.",
        "Good decisions come from experience. Experience comes from making bad decisions.",
        "Somewhere, something incredible is waiting to be known.",
        "Never memorize something you can look up.",
        "Reality is broken. Game designers can probably fix it.",
        "The secret of getting ahead is getting started.",
        "It always seems impossible until it's done.",
        "Do what you can, with what you have, where you are.",
        "Everything you can imagine is real.",
        "If you want to lift yourself up, lift up someone else.",
        "What we achieve inwardly will change outer reality.",
        // Relaxing / Calm
        "Breathe in deeply to bring your mind home to your body.",
        "Almost everything will work again if you unplug it for a few minutes, including you.",
        "The quieter you become, the more you can hear.",
        "Nature does not hurry, yet everything is accomplished.",
        "Slow down and everything you are chasing will come around and catch you.",
        "The greatest weapon against stress is our ability to choose one thought over another.",
        "Your calm mind is the ultimate weapon against your challenges.",
        "Calm mind brings inner strength and self-confidence.",
        "Life is ten percent what happens to you and ninety percent how you respond to it.",
        "I think I'm supposed to be sleeping right now.",
        "Sometimes the most productive thing you can do is relax.",
        "You don't always need a plan. Sometimes you just need to breathe and let go.",
        "Not all storms come to disrupt your life. Some come to clear your path.",
        "Be gentle with yourself. You're doing the best you can.",
        // Humorous / Witty
        "Call your parents once in a while.",
        "The road to success is always under construction.",
        "If at first you don't succeed, redefine success.",
        "Sure, honey, let's buy 86 throw pillows.",
        "Adulting is soup, and I'm a fork.",
        "I've got 99 problems and 86 of them are completely made up in my head.",
        "I don't stalk people on Instagram. I deeply research their lives.",
        "You can't rush perfection, especially when you're avoiding it.",
        "I don't always have patience, but when I do, it's very short-lived.",
        "I invented a new word today: Plagiarism.",
        "Age is an issue of mind over matter. If you don't mind, it doesn't matter.",
        "If you can't convince them, confuse them.",
        "The best way to remember your wife's birthday is to forget it once.",
        "Being an adult is like folding a fitted sheet. No one really knows how.",
        "Some things are better left unsaid. Which I generally realize right after I have said them.",
        "The odds of going to the store for a loaf of bread and coming out with only a loaf of bread are three billion to one.",
        "A giraffe's coffee would be cold by the time it reached the bottom of its throat. Ever think about that? No. You only think about yourself.",
        "I never make the same mistake twice. I make it five or six times, you know, just to be sure.",
        "The worst part about online shopping is having to get up and get your card out of your handbag.",
        "If you read a lot of books, you're considered well-read. But if you watch a lot of TV, you're not considered well-viewed.",
        "When I say I won't tell anyone, my sister doesn't count.",
        "Always forgive your enemies; nothing annoys them so much.",
        "Life is short. Smile while you still have teeth.",
        "I would like to apologize to anyone I have not yet offended. Please be patient. I will get to you shortly.",
        "I used to think I was indecisive, but now I'm not so sure.",
        "Age is of no importance, unless you are cheese.",
        "A clear conscience is usually the sign of a bad memory.",
        "I'm not arguing, I'm just explaining why I'm right.",
        "Behind every great man is a woman rolling her eyes.",
        "I'm on a whiskey diet. I've lost three days already.",
        "I don't need a hair stylist. My pillow gives me a new hairstyle every morning.",
        "My wallet is like an onion. Opening it makes me cry.",
        "I'm not lazy. I'm just on energy saving mode.",
        "Common sense is like deodorant. The people who need it most never use it.",
        "If there's no chocolate in heaven, I'm not going.",
        "My bed is a magical place where I suddenly remember everything I forgot to do.",
        "I followed my heart, and it led me to the fridge.",
        "I don't have a bucket list, but my to-do list is long enough to qualify.",
        "I finally realized that people are prisoners of their phones. That's why they're called cell phones.",
        "I told my wife she should embrace her mistakes. She gave me a hug.",
        "I'm reading a book about anti-gravity. It's impossible to put down.",
        "I used to hate facial hair, but then it grew on me.",
        "My therapist says I have a preoccupation with vengeance. We'll see about that.",
        "I'm not superstitious, but I am a little stitious.",
        "I have an inferiority complex, but it's not a very good one.",
        "The elevator to success is out of order. You'll have to take the stairs.",
        "People say nothing is impossible, but I do nothing every day.",
        "I didn't fall. The floor just needed a hug.",
        "Life is too short to remove the USB safely.",
        "I put my phone on airplane mode, but it's not flying.",
        "If we shouldn't eat at night, why is there a light in the fridge?",
        "My favorite exercise is a cross between a lunge and a crunch. I call it lunch.",
        "I don't go crazy. I am crazy. I just go normal from time to time.",
        "I need six months of vacation, twice a year.",
        // Positive / Uplifting
        "You are enough just as you are.",
        "Every day may not be good, but there is something good in every day.",
        "Stars can't shine without darkness.",
        "You are braver than you believe, stronger than you seem, and smarter than you think.",
        "Difficult roads often lead to beautiful destinations.",
        "Be the reason someone smiles today.",
        "The sun will rise and we will try again.",
        "You make the world a better place just by being in it.",
        "One small positive thought can change your whole day.",
        "You don't have to be perfect to be amazing.",
        "You are capable of amazing things.",
        "Your only limit is your mind.",
        "Fall seven times, stand up eight.",
        "What lies behind us and what lies before us are tiny matters compared to what lies within us.",
        "The best is yet to come.",
        "You were born to be real, not to be perfect.",
        "In a world where you can be anything, be kind.",
        "Small steps in the right direction can turn out to be the biggest step of your life.",
        // Short & Sweet
        "Keep going.",
        "This too shall pass.",
        "You've got this.",
        "Dream big.",
        "Stay curious.",
        "Be kind.",
        "Just breathe.",
        "Make it happen.",
        "Choose joy.",
        "Progress, not perfection."
    ]
    
    private var currentQuote: String {
        Self.quotes[quoteOrder[currentQuoteIndex % Self.quotes.count]]
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
                
                Text("Start typing with your keyboard!")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .padding(.bottom, 24)
            }
            .padding(.horizontal, 20)
        }
        .navigationTitle("Typing Practice")
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(false)
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
            statItem(value: "\(Int(wpm.rounded()))", label: "WPM")
            Spacer()
            statItem(value: "\(Int(accuracy.rounded()))%", label: "Accuracy")
            Spacer()
            statItem(value: "\(streak)", label: "Streak")
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
        let hasError = typedChars.indices.contains(where: { i in
            i < chars.count && typedChars[i] != chars[i]
        })
        
        return WrappingHStack(quote: currentQuote, typedText: typedText)
            .modifier(ShakeModifier(isShaking: hasError))
    }
    
    // MARK: - Celebration
    
    private var celebrationOverlay: some View {
        VStack(spacing: 8) {
            Text("✨")
                .font(.system(size: 48))
            Text("Well done!")
                .font(.title2)
                .fontWeight(.bold)
            Text("Next quote loading...")
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
        .padding(32)
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(Color(uiColor: .secondarySystemGroupedBackground))
                .shadow(color: .black.opacity(0.2), radius: 8, y: 4)
        )
    }
    
    // MARK: - Input Processing
    
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
            } else {
                streak = 0
            }
            
            currentQuoteIndex += 1
            if currentQuoteIndex >= quoteOrder.count {
                quoteOrder = Array(0..<Self.quotes.count).shuffled()
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

// MARK: - Wrapping Text Display

struct WrappingHStack: View {
    let quote: String
    let typedText: String
    
    var body: some View {
        let quoteChars = Array(quote)
        let typedChars = Array(typedText)
        
        let attributed = quoteChars.enumerated().map { (i, char) -> (Character, Color, FontWeight, Bool, Bool) in
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
    
    private func buildAttributedText(chars: [(Character, Color, FontWeight, Bool, Bool)]) -> AttributedString {
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
