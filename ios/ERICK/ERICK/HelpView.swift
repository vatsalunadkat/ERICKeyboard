import SwiftUI

struct HelpView: View {
    @Environment(\.erickLanguageKey) private var keyboardLanguage
    @AppStorage(LearningProgressStore.quickstartCompletedKey) private var quickstartCompleted = false
    @AppStorage(LearningProgressStore.quickstartDismissedKey) private var quickstartDismissed = false
    @State private var expandedSections: Set<HelpSectionID> = [.chords, .utility]
    @State private var replayQuickstartStep = 0
    @State private var showQuickstart = false

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                helpSection(title: erickText("Start Here", languageKey: keyboardLanguage)) {
                    VStack(alignment: .leading, spacing: 12) {
                        Text(erickText("Use this order so you only learn the next thing you need.", languageKey: keyboardLanguage))
                        HelpBullet(text: erickText("Open Quickstart for the core dial model.", languageKey: keyboardLanguage))
                        HelpBullet(text: erickText("Use Practice Lessons for guided drills instead of memorizing rules here.", languageKey: keyboardLanguage))
                        HelpBullet(text: erickText("Open Controller Diagnostics only when you plan to type with a gamepad.", languageKey: keyboardLanguage))
                        Button {
                            replayQuickstartStep = 0
                            showQuickstart = true
                        } label: {
                            Text(erickText("Replay Quickstart", languageKey: keyboardLanguage))
                                .font(.headline)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                        }
                        .buttonStyle(.borderedProminent)

                        NavigationLink(destination: PracticeHubView()) {
                            Text(erickText("Open Practice Lessons", languageKey: keyboardLanguage))
                                .font(.headline)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                        }
                        .buttonStyle(.borderedProminent)
                    }
                }

                ExpandableHelpSection(
                    title: erickText("Who ERICK Can Help", languageKey: keyboardLanguage),
                    summary: erickText("Examples across physical access, cognitive support, and everyday use.", languageKey: keyboardLanguage),
                    isExpanded: binding(for: .benefits)
                ) {
                    BenefitsOverviewContent()
                }

                ExpandableHelpSection(
                    title: erickText("Chord Mechanics", languageKey: keyboardLanguage),
                    summary: erickText("Left picks the row. Right picks the letter. Release both to type.", languageKey: keyboardLanguage),
                    isExpanded: binding(for: .chords)
                ) {
                    HelpBullet(text: erickText("Move the left dial first to reveal a row.", languageKey: keyboardLanguage))
                    HelpBullet(text: erickText("Move the right dial to the character you want.", languageKey: keyboardLanguage))
                    HelpBullet(text: erickText("Release both dials to commit the chord.", languageKey: keyboardLanguage))
                    HelpBullet(text: erickText("Watch the preview bar instead of trying to memorize every row.", languageKey: keyboardLanguage))
                }

                ExpandableHelpSection(
                    title: erickText("6-Section Utility Wheel", languageKey: keyboardLanguage),
                    summary: erickText("N Symbols, NE Shift, SE Space, S Period, SW Enter, NW Backspace.", languageKey: keyboardLanguage),
                    isExpanded: binding(for: .utility)
                ) {
                    HelpMappingRow(direction: "N", action: erickText("Symbols", languageKey: keyboardLanguage))
                    HelpMappingRow(direction: "NE", action: erickText("Shift", languageKey: keyboardLanguage))
                    HelpMappingRow(direction: "SE", action: erickText("Space", languageKey: keyboardLanguage))
                    HelpMappingRow(direction: "S", action: erickText("Period", languageKey: keyboardLanguage))
                    HelpMappingRow(direction: "SW", action: erickText("Enter", languageKey: keyboardLanguage))
                    HelpMappingRow(direction: "NW", action: erickText("Backspace", languageKey: keyboardLanguage))
                    Text(erickText("In 8-section mode, the right dial exposes the full 8-direction utility wheel.", languageKey: keyboardLanguage))
                        .font(.footnote)
                        .foregroundColor(.secondary)
                }

                ExpandableHelpSection(
                    title: erickText("Input Modes", languageKey: keyboardLanguage),
                    summary: erickText("Instant is fastest. Confirm is cautious. Assisted is the one-handed path.", languageKey: keyboardLanguage),
                    isExpanded: binding(for: .modes)
                ) {
                    HelpBullet(text: erickText("Instant commits as soon as both dials release.", languageKey: keyboardLanguage))
                    HelpBullet(text: erickText("Confirm lets you preview before committing.", languageKey: keyboardLanguage))
                    HelpBullet(text: erickText("Assisted keeps the left-side row locked so you can finish from the letter side.", languageKey: keyboardLanguage))
                }

                ExpandableHelpSection(
                    title: erickText("Layouts and Predictions", languageKey: keyboardLanguage),
                    summary: erickText("Logical is easiest to learn. Efficiency is faster later. Predictions appear at rest.", languageKey: keyboardLanguage),
                    isExpanded: binding(for: .layouts)
                ) {
                    HelpBullet(text: erickText("Logical keeps the alphabet easy to learn.", languageKey: keyboardLanguage))
                    HelpBullet(text: erickText("Efficiency optimizes common English letters.", languageKey: keyboardLanguage))
                    HelpBullet(text: erickText("Custom layouts stay available in 8-section mode.", languageKey: keyboardLanguage))
                    HelpBullet(text: erickText("When both dials rest at center, ERICK shows up to three predictions.", languageKey: keyboardLanguage))
                }

                ExpandableHelpSection(
                    title: erickText("Controller Typing", languageKey: keyboardLanguage),
                    summary: erickText("A controller mirrors the two dials with both analog sticks.", languageKey: keyboardLanguage),
                    isExpanded: binding(for: .controller)
                ) {
                    HelpBullet(text: erickText("Use both analog sticks the same way you use the touch dials.", languageKey: keyboardLanguage))
                    HelpBullet(text: erickText("Calibrate dead zone and Y-axis inversion in Controller Diagnostics before drills.", languageKey: keyboardLanguage))
                    HelpBullet(text: erickText("Start controller drills only after the touch version feels comfortable.", languageKey: keyboardLanguage))
                }
            }
            .padding()
        }
        .navigationTitle(erickText("How to Type", languageKey: keyboardLanguage))
        .navigationBarTitleDisplayMode(.inline)
        .sheet(isPresented: $showQuickstart) {
            QuickstartView(
                currentStep: $replayQuickstartStep,
                onComplete: {
                    quickstartCompleted = true
                    quickstartDismissed = false
                    replayQuickstartStep = 0
                    showQuickstart = false
                },
                onSkip: {
                    quickstartDismissed = true
                    showQuickstart = false
                }
            )
        }
    }

    private func binding(for section: HelpSectionID) -> Binding<Bool> {
        Binding(
            get: { expandedSections.contains(section) },
            set: { isExpanded in
                if isExpanded {
                    expandedSections.insert(section)
                } else {
                    expandedSections.remove(section)
                }
            }
        )
    }

    @ViewBuilder
    private func helpSection(title: String, @ViewBuilder content: () -> some View) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title)
                .font(.headline)
            content()
                .font(.body)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding()
        .background(Color(uiColor: .secondarySystemBackground))
        .cornerRadius(12)
    }
}

private enum HelpSectionID: Hashable {
    case benefits
    case chords
    case utility
    case modes
    case layouts
    case controller
}

private struct ExpandableHelpSection<Content: View>: View {
    let title: String
    let summary: String
    @Binding var isExpanded: Bool
    let content: Content

    init(
        title: String,
        summary: String,
        isExpanded: Binding<Bool>,
        @ViewBuilder content: () -> Content
    ) {
        self.title = title
        self.summary = summary
        self._isExpanded = isExpanded
        self.content = content()
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Button {
                isExpanded.toggle()
            } label: {
                HStack(alignment: .top, spacing: 12) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(title)
                            .font(.headline)
                            .foregroundColor(.primary)
                        Text(summary)
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.leading)
                    }
                    Spacer()
                    Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                        .foregroundColor(.secondary)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .buttonStyle(.plain)

            if isExpanded {
                VStack(alignment: .leading, spacing: 8) {
                    content
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding()
        .background(Color(uiColor: .secondarySystemBackground))
        .cornerRadius(12)
    }
}

private struct HelpBullet: View {
    let text: String

    var body: some View {
        HStack(alignment: .top, spacing: 10) {
            Text("•")
                .fontWeight(.bold)
            Text(text)
                .fixedSize(horizontal: false, vertical: true)
        }
        .foregroundColor(.secondary)
    }
}

private struct HelpMappingRow: View {
    let direction: String
    let action: String

    var body: some View {
        HStack(spacing: 12) {
            Text(direction)
                .font(.subheadline)
                .fontWeight(.bold)
                .frame(width: 40, alignment: .leading)
            Text(action)
                .foregroundColor(.secondary)
            Spacer(minLength: 0)
        }
    }
}

