import SwiftUI

struct EmojiCellView: View {
    let displayText: String
    let accessibilityLabel: String
    let isEmoticon: Bool
    let isDarkMode: Bool
    var onTap: () -> Void
    var onLongPress: ((CGRect) -> Void)?

    var body: some View {
        GeometryReader { proxy in
            ZStack {
                RoundedRectangle(cornerRadius: 10, style: .continuous)
                    .fill(isDarkMode ? Color.white.opacity(0.06) : Color.black.opacity(0.04))

                Text(displayText)
                    .font(resolvedFont)
                    .foregroundColor(resolvedTextColor)
                    .minimumScaleFactor(0.3)
                    .lineLimit(1)
                    .padding(.horizontal, 3)
            }
            .contentShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
            .onTapGesture(perform: onTap)
            .onLongPressGesture(minimumDuration: 0.35) {
                onLongPress?(proxy.frame(in: .named("EmojiPanelSpace")))
            }
        }
        .frame(height: isEmoticon ? 42 : 44)
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(Text(accessibilityLabel))
        .accessibilityAddTraits(.isButton)
    }

    private var resolvedFont: Font {
        if isEmoticon {
            let size: CGFloat = displayText.count > 7 ? 11 : (displayText.count > 4 ? 12 : 14)
            return .system(size: size, weight: .semibold, design: .rounded)
        }
        return .system(size: 28)
    }

    private var resolvedTextColor: Color {
        if isEmoticon {
            return isDarkMode ? .white : Color(hex: "#1E1E1E")
        }
        return isDarkMode ? .white : .primary
    }
}