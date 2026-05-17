import SwiftUI

struct KeyboardContainerView: View {
    @AppStorage("keyboard_language", store: SettingsView.appGroupDefaults) private var keyboardLanguage: String = "english"
    @ObservedObject var viewModel: KeyboardViewModel
    var onTouch: (Float, Float, Bool, Bool, Bool) -> Void
    var onSettingsChanged: () -> Void
    var onSuggestionTapped: (Int) -> Void
    var onEmojiButtonTapped: () -> Void
    var onEmojiCommitText: (String) -> Void
    var onEmojiBackspacePressStarted: () -> Void
    var onEmojiBackspacePressEnded: () -> Void

    @State private var showSettings = false

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

                Group {
                    if viewModel.keyboardMode == .emoji {
                        EmojiPanelView(
                            recentEmojis: viewModel.recentEmojis,
                            isDarkMode: viewModel.isDarkMode,
                            languageKey: keyboardLanguage,
                            onCommitText: onEmojiCommitText,
                            onReturnToKeyboard: onEmojiButtonTapped,
                            onBackspacePressStarted: onEmojiBackspacePressStarted,
                            onBackspacePressEnded: onEmojiBackspacePressEnded
                        )
                        .frame(width: geometry.size.width - (horizontalPadding * 2), height: availableHeight)
                        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
                    } else {
                        HStack(spacing: controlSpacing) {
                            JoystickView(
                                isRightSide: viewModel.isLeftHanded,
                                activeDirection: viewModel.leftDirection != .none ? viewModel.leftDirection : viewModel.lockedLeftDirection,
                                keyboardMode: viewModel.keyboardMode,
                                isEfficiency: viewModel.isEfficiency,
                                sixSectionMode: viewModel.sixSectionMode,
                                colorPaletteKey: viewModel.colorPaletteKey,
                                fontPreference: viewModel.fontPreference,
                                customNormalSections: viewModel.customNormalSections,
                                customShiftedSections: viewModel.customShiftedSections,
                                paletteRefreshToken: viewModel.paletteRefreshToken,
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
                                sixSectionMode: viewModel.sixSectionMode,
                                colorPaletteKey: viewModel.colorPaletteKey,
                                fontPreference: viewModel.fontPreference,
                                customNormalSections: viewModel.customNormalSections,
                                customShiftedSections: viewModel.customShiftedSections,
                                paletteRefreshToken: viewModel.paletteRefreshToken,
                                controllerStickNormalized: viewModel.rightControllerStickNormalized
                            ) { dx, dy, isDownOrMove, isUp in
                                onTouch(dx, dy, false, isDownOrMove, isUp)
                            }
                            .frame(width: rightSize, height: rightSize)
                            .offset(x: -6)
                        }
                        .frame(width: totalControlsWidth, height: availableHeight, alignment: .center)
                        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
                    }
                }
                .padding(.top, topInset)
                .padding(.bottom, bottomInset)
                .padding(.horizontal, horizontalPadding)
            }
            .allowsHitTesting(!showSettings)

            HStack(spacing: 0) {
                Button(action: onEmojiButtonTapped) {
                    Text(viewModel.keyboardMode == .emoji ? "ABC" : "😀")
                        .font(
                            .system(
                                size: viewModel.keyboardMode == .emoji ? 13 : 22,
                                weight: .bold,
                                design: .rounded
                            )
                        )
                        .foregroundColor(viewModel.isDarkMode ? .white : Color(hex: "#1E1E1E"))
                        .frame(width: 36, height: 32)
                        .background(
                            Capsule()
                                .fill(viewModel.isDarkMode ? Color(hex: "#323232").opacity(0.96) : Color.white.opacity(0.96))
                        )
                }
                .buttonStyle(.plain)
                .accessibilityLabel(
                    erickText(
                        viewModel.keyboardMode == .emoji ? "emoji_button_back_abc" : "emoji_button_open",
                        languageKey: keyboardLanguage
                    )
                )
                .frame(width: 36, alignment: .center)
                .animation(.easeInOut(duration: 0.15), value: viewModel.keyboardMode)

                ZStack {
                    if viewModel.keyboardMode != .emoji && !viewModel.previewItems.isEmpty {
                        KeyboardPreviewBar(
                            items: viewModel.previewItems,
                            highlightedIndex: viewModel.highlightedPreviewIndex,
                            isDarkMode: viewModel.isDarkMode,
                            fontPreference: viewModel.fontPreference
                        )
                    } else if viewModel.keyboardMode != .emoji && viewModel.bothDialsAtHome && !viewModel.suggestions.isEmpty {
                        KeyboardSuggestionBar(
                            suggestionContextLabel: viewModel.suggestionContextLabel,
                            suggestions: viewModel.suggestions,
                            isDarkMode: viewModel.isDarkMode,
                            onTap: onSuggestionTapped
                        )
                    }
                }
                .frame(maxWidth: .infinity)

                Button(action: {
                    withAnimation {
                        showSettings = true
                    }
                }) {
                    Image(systemName: "gear")
                        .font(.system(size: 20))
                        .foregroundColor(.gray)
                }
                .frame(width: 36)
            }
            .frame(height: 40)
            .padding(.horizontal, 4)

            if let floatingBadge {
                Text(floatingBadge.text)
                    .font(.system(size: floatingBadge.text == "#↑" ? 14 : 16, weight: .bold, design: .rounded))
                    .foregroundColor(floatingBadge.color)
                    .padding(.horizontal, floatingBadge.text.count > 1 ? 12 : 14)
                    .frame(height: 28)
                    .background(
                        Capsule()
                            .fill(viewModel.isDarkMode ? Color(hex: "#323232").opacity(0.98) : Color.white.opacity(0.98))
                            .shadow(color: .black.opacity(0.08), radius: 6, y: 2)
                    )
                    .padding(.top, 44)
                    .transition(.opacity)
            }

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

    private var floatingBadge: (text: String, color: Color)? {
        switch viewModel.keyboardMode {
        case .shifted:
            return ("↑", viewModel.isDarkMode ? .white : Color(hex: "#333333"))
        case .capsLocked:
            return ("↑↑", Color(hex: "#D32F2F"))
        case .symbols:
            return ("#", Color(hex: "#FF9800"))
        case .symbolsShifted:
            return ("#↑", Color(hex: "#FF9800"))
        case .normal, .emoji:
            return nil
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

    private var strokeColor: Color {
        isDarkMode ? .white : .black
    }

    var body: some View {
        HStack(spacing: 8) {
            ForEach(Array(items.enumerated()), id: \.offset) { index, item in
                let font = resolvedPreviewFont(
                    size: highlightedIndex == index ? 21 : 17,
                    weight: highlightedIndex == index ? .heavy : .bold
                )
                ZStack {
                    ForEach(Self.strokeOffsets, id: \.0) { dx, dy in
                        Text(item.text)
                            .font(font)
                            .foregroundColor(strokeColor)
                            .offset(x: dx, y: dy)
                    }
                    Text(item.text)
                        .font(font)
                        .foregroundColor(item.color)
                }
                .frame(minWidth: 20)
                .scaleEffect(highlightedIndex == index ? 1.08 : 1.0)
                .animation(.easeInOut(duration: 0.12), value: highlightedIndex)
            }
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 4)
        .background(
            Capsule()
                .fill(isDarkMode ? Color(hex: "#323232").opacity(0.96) : Color.white.opacity(0.96))
                .shadow(color: .black.opacity(0.08), radius: 6, y: 2)
        )
        .frame(maxWidth: .infinity, alignment: .center)
    }

    private static let strokeOffsets: [(CGFloat, CGFloat)] = {
        let width: CGFloat = 0.8
        return [
            (-width, -width), (0, -width), (width, -width),
            (-width, 0),                    (width, 0),
            (-width, width),  (0, width),  (width, width)
        ]
    }()
}

private struct KeyboardSuggestionBar: View {
    let suggestionContextLabel: String
    let suggestions: [String]
    var isDarkMode: Bool = false
    var onTap: (Int) -> Void

    var body: some View {
        HStack(spacing: 0) {
            if !suggestionContextLabel.isEmpty {
                Text(suggestionContextLabel)
                    .font(.system(size: 10, weight: .bold))
                    .foregroundColor(isDarkMode ? .white : Color(hex: "#333333"))
                    .frame(width: 78, height: 40, alignment: .leading)
                    .lineLimit(2)
                    .minimumScaleFactor(0.8)

                Divider()
                    .frame(height: 24)
                    .background(isDarkMode ? Color.gray.opacity(0.5) : Color.gray.opacity(0.3))
            }
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