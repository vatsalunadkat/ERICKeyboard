import SwiftUI

private struct EmojiGridCellModel: Identifiable, Hashable {
    let id: String
    let displayText: String
    let committedText: String
    let baseGlyph: String
    let toneVariants: [String]
    let accessibilityLabel: String
    let isEmoticon: Bool

    var supportsTone: Bool { !toneVariants.isEmpty }
}

private struct ActiveTonePicker {
    let cell: EmojiGridCellModel
    let frame: CGRect
}

private struct EmojiActionButton: View {
    let title: String
    let accessibilityLabel: String
    let isDarkMode: Bool
    var action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.system(size: 13, weight: .bold, design: .rounded))
                .foregroundColor(isDarkMode ? .white : Color(hex: "#1E1E1E"))
                .frame(minWidth: 48, minHeight: 34)
                .padding(.horizontal, 10)
                .background(
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .fill(isDarkMode ? Color(hex: "#323232").opacity(0.96) : Color.white.opacity(0.96))
                )
        }
        .buttonStyle(.plain)
        .accessibilityLabel(Text(accessibilityLabel))
    }
}

private struct HoldEmojiActionButton: View {
    let title: String
    let accessibilityLabel: String
    let isDarkMode: Bool
    var onPressStarted: () -> Void
    var onPressEnded: () -> Void

    @State private var isPressed = false

    var body: some View {
        Text(title)
            .font(.system(size: 20, weight: .semibold, design: .rounded))
            .foregroundColor(isDarkMode ? .white : Color(hex: "#1E1E1E"))
            .frame(minWidth: 48, minHeight: 34)
            .padding(.horizontal, 10)
            .background(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .fill(isDarkMode ? Color(hex: "#323232").opacity(0.96) : Color.white.opacity(0.96))
            )
            .contentShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
            .gesture(
                DragGesture(minimumDistance: 0)
                    .onChanged { _ in
                        if !isPressed {
                            isPressed = true
                            onPressStarted()
                        }
                    }
                    .onEnded { _ in
                        if isPressed {
                            isPressed = false
                            onPressEnded()
                        }
                    }
            )
            .accessibilityElement(children: .ignore)
            .accessibilityLabel(Text(accessibilityLabel))
            .accessibilityAddTraits(.isButton)
    }
}

struct EmojiPanelView: View {
    let recentEmojis: [String]
    let isDarkMode: Bool
    let languageKey: String
    var onCommitText: (String) -> Void
    var onReturnToKeyboard: () -> Void
    var onBackspacePressStarted: () -> Void
    var onBackspacePressEnded: () -> Void

    @State private var currentTabId: String
    @State private var preferredToneByBase: [String: String] = [:]
    @State private var activeTonePicker: ActiveTonePicker?

    init(
        recentEmojis: [String],
        isDarkMode: Bool,
        languageKey: String,
        onCommitText: @escaping (String) -> Void,
        onReturnToKeyboard: @escaping () -> Void,
        onBackspacePressStarted: @escaping () -> Void,
        onBackspacePressEnded: @escaping () -> Void
    ) {
        self.recentEmojis = recentEmojis
        self.isDarkMode = isDarkMode
        self.languageKey = languageKey
        self.onCommitText = onCommitText
        self.onReturnToKeyboard = onReturnToKeyboard
        self.onBackspacePressStarted = onBackspacePressStarted
        self.onBackspacePressEnded = onBackspacePressEnded
        _currentTabId = State(initialValue: recentEmojis.isEmpty ? "smileys" : EmojiCatalog.recentTabId)
    }

    var body: some View {
        GeometryReader { proxy in
            let usableWidth = max(200, proxy.size.width - 16)
            let columnCount = max(6, min(10, Int(usableWidth / 42)))
            let gridColumns = Array(repeating: GridItem(.flexible(minimum: 34, maximum: 52), spacing: 6), count: columnCount)

            ZStack(alignment: .topLeading) {
                VStack(spacing: 8) {
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 4) {
                            ForEach(orderedTabs, id: \.self) { tabId in
                                Button(action: {
                                    activeTonePicker = nil
                                    currentTabId = tabId
                                }) {
                                    Text(tabLabel(for: tabId))
                                        .font(.system(size: 13, weight: .semibold, design: .rounded))
                                        .foregroundColor(tabTextColor(for: tabId))
                                        .padding(.horizontal, 12)
                                        .frame(minHeight: 32)
                                        .background(
                                            Capsule()
                                                .fill(tabBackgroundColor(for: tabId))
                                        )
                                }
                                .buttonStyle(.plain)
                            }
                        }
                        .padding(.horizontal, 2)
                    }

                    ScrollView(.vertical, showsIndicators: true) {
                        LazyVGrid(columns: gridColumns, spacing: 6) {
                            ForEach(currentCells) { cell in
                                EmojiCellView(
                                    displayText: cell.displayText,
                                    accessibilityLabel: cell.accessibilityLabel,
                                    isEmoticon: cell.isEmoticon,
                                    isDarkMode: isDarkMode,
                                    onTap: {
                                        activeTonePicker = nil
                                        onCommitText(cell.committedText)
                                    },
                                    onLongPress: cell.supportsTone ? { frame in
                                        activeTonePicker = ActiveTonePicker(cell: cell, frame: frame)
                                    } : nil
                                )
                            }
                        }
                        .padding(.vertical, 2)
                    }

                    HStack(spacing: 8) {
                        EmojiActionButton(
                            title: "ABC",
                            accessibilityLabel: erickText("emoji_button_back_abc", languageKey: languageKey),
                            isDarkMode: isDarkMode,
                            action: onReturnToKeyboard
                        )

                        Spacer(minLength: 0)

                        HoldEmojiActionButton(
                            title: "⌫",
                            accessibilityLabel: erickText("Backspace", languageKey: languageKey),
                            isDarkMode: isDarkMode,
                            onPressStarted: onBackspacePressStarted,
                            onPressEnded: onBackspacePressEnded
                        )
                    }
                }
                .padding(.horizontal, 8)
                .padding(.top, 4)
                .padding(.bottom, 6)
                .frame(width: proxy.size.width, height: proxy.size.height, alignment: .top)

                if let activeTonePicker {
                    tonePickerOverlay(for: activeTonePicker)
                }
            }
        }
        .coordinateSpace(name: "EmojiPanelSpace")
        .onChange(of: recentEmojis) { items in
            if currentTabId == EmojiCatalog.recentTabId && items.isEmpty {
                currentTabId = "smileys"
            }
        }
        .onChange(of: currentTabId) { _ in
            activeTonePicker = nil
        }
    }

    private var orderedTabs: [String] {
        [EmojiCatalog.recentTabId] + EmojiCatalog.orderedCategoryIds.filter { EmojiCatalog.categoryById[$0] != nil }
    }

    private var currentCells: [EmojiGridCellModel] {
        if currentTabId == EmojiCatalog.recentTabId {
            return recentEmojis.map(buildRecentCell)
        }

        return EmojiCatalog.categoryById[currentTabId]?.items.map { item in
            let committedText = preferredToneByBase[item.baseGlyph] ?? item.baseGlyph
            return EmojiGridCellModel(
                id: "\(currentTabId):\(item.baseGlyph)",
                displayText: committedText,
                committedText: committedText,
                baseGlyph: item.baseGlyph,
                toneVariants: item.toneVariants,
                accessibilityLabel: item.name.isEmpty ? committedText : item.name,
                isEmoticon: currentTabId == "emoticons"
            )
        } ?? []
    }

    private func buildRecentCell(_ recent: String) -> EmojiGridCellModel {
        guard let lookup = EmojiCatalog.itemLookupByGlyph[recent] else {
            return EmojiGridCellModel(
                id: "recent:\(recent)",
                displayText: recent,
                committedText: recent,
                baseGlyph: recent,
                toneVariants: [],
                accessibilityLabel: recent,
                isEmoticon: true
            )
        }

        let item = lookup.item
        return EmojiGridCellModel(
            id: "recent:\(recent)",
            displayText: recent,
            committedText: recent,
            baseGlyph: item.baseGlyph,
            toneVariants: item.toneVariants,
            accessibilityLabel: item.name.isEmpty ? recent : item.name,
            isEmoticon: lookup.categoryId == "emoticons"
        )
    }

    private func tabLabel(for tabId: String) -> String {
        if tabId == EmojiCatalog.recentTabId {
            return erickText("emoji_tab_recent", languageKey: languageKey)
        }
        let displayKey = EmojiCatalog.categoryById[tabId]?.displayKey ?? tabId
        return erickText(displayKey, languageKey: languageKey)
    }

    private func tabBackgroundColor(for tabId: String) -> Color {
        let isSelected = currentTabId == tabId
        if isSelected && isDarkMode {
            return Color(hex: "#3C4043")
        }
        if isSelected {
            return .white
        }
        return isDarkMode ? Color(hex: "#242628") : Color(hex: "#DDE4E8")
    }

    private func tabTextColor(for tabId: String) -> Color {
        let isSelected = currentTabId == tabId
        if isSelected && isDarkMode {
            return .white
        }
        if isSelected {
            return Color(hex: "#1E1E1E")
        }
        return isDarkMode ? Color(hex: "#D6D9DC") : Color(hex: "#3A3D40")
    }

    @ViewBuilder
    private func tonePickerOverlay(for picker: ActiveTonePicker) -> some View {
        GeometryReader { proxy in
            let options = [picker.cell.baseGlyph] + picker.cell.toneVariants
            let buttonWidth: CGFloat = 40
            let pickerWidth = min(proxy.size.width - 16, CGFloat(options.count) * buttonWidth + CGFloat(max(0, options.count - 1)) * 4 + 16)
            let unclampedX = picker.frame.midX - (pickerWidth / 2)
            let x = min(max(8, unclampedX), proxy.size.width - pickerWidth - 8)
            let y = max(4, picker.frame.minY - 52)

            ZStack(alignment: .topLeading) {
                Color.clear
                    .contentShape(Rectangle())
                    .onTapGesture {
                        activeTonePicker = nil
                    }

                HStack(spacing: 4) {
                    ForEach(options, id: \.self) { option in
                        Button(action: {
                            if option == picker.cell.baseGlyph {
                                preferredToneByBase.removeValue(forKey: picker.cell.baseGlyph)
                            } else {
                                preferredToneByBase[picker.cell.baseGlyph] = option
                            }
                            activeTonePicker = nil
                            onCommitText(option)
                        }) {
                            Text(option)
                                .font(.system(size: 26))
                                .frame(width: buttonWidth, height: 34)
                                .background(
                                    RoundedRectangle(cornerRadius: 10, style: .continuous)
                                        .fill(isDarkMode ? Color.white.opacity(0.06) : Color.black.opacity(0.04))
                                )
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(8)
                .background(
                    RoundedRectangle(cornerRadius: 14, style: .continuous)
                        .fill(isDarkMode ? Color(hex: "#323232").opacity(0.98) : Color.white.opacity(0.98))
                        .shadow(color: .black.opacity(0.08), radius: 6, y: 2)
                )
                .offset(x: x, y: y)
            }
        }
    }
}