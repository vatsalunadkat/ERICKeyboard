import SwiftUI

struct HelpView: View {
    @State private var expandedSections: Set<HelpSectionID> = [.chords, .utility]

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                helpSection(title: "Start Here") {
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Use this order so you only learn the next thing you need.")
                        HelpBullet(text: "Open Quickstart for the core dial model.")
                        HelpBullet(text: "Use Practice Lessons for guided drills instead of memorizing rules here.")
                        HelpBullet(text: "Open controller diagnostics only when you plan to type with a gamepad.")
                        NavigationLink(destination: PracticeHubView()) {
                            Text("Open Practice Lessons")
                                .font(.headline)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                        }
                        .buttonStyle(.borderedProminent)
                    }
                }

                ExpandableHelpSection(
                    title: "Chord Mechanics",
                    summary: "Left picks the row. Right picks the letter. Release both to type.",
                    isExpanded: binding(for: .chords)
                ) {
                    HelpBullet(text: "Move the left dial first to reveal a row.")
                    HelpBullet(text: "Move the right dial to the character you want.")
                    HelpBullet(text: "Release both dials to commit the chord.")
                    HelpBullet(text: "Watch the preview bar instead of trying to memorize every row.")
                }

                ExpandableHelpSection(
                    title: "6-Section Utility Wheel",
                    summary: "N Symbols, NE Shift, SE Space, S Period, SW Enter, NW Backspace.",
                    isExpanded: binding(for: .utility)
                ) {
                    HelpMappingRow(direction: "N", action: "Symbols")
                    HelpMappingRow(direction: "NE", action: "Shift")
                    HelpMappingRow(direction: "SE", action: "Space")
                    HelpMappingRow(direction: "S", action: "Period")
                    HelpMappingRow(direction: "SW", action: "Enter")
                    HelpMappingRow(direction: "NW", action: "Backspace")
                    Text("In 8-section mode, the right dial exposes the full 8-direction utility wheel.")
                        .font(.footnote)
                        .foregroundColor(.secondary)
                }

                ExpandableHelpSection(
                    title: "Input Modes",
                    summary: "Instant is fastest. Confirm is cautious. Assisted is the one-handed path.",
                    isExpanded: binding(for: .modes)
                ) {
                    HelpBullet(text: "Instant commits as soon as both dials release.")
                    HelpBullet(text: "Confirm lets you preview before committing.")
                    HelpBullet(text: "Assisted keeps the left-side row locked so you can finish from the letter side.")
                }

                ExpandableHelpSection(
                    title: "Layouts and Predictions",
                    summary: "Logical is easiest to learn. Efficiency is faster later. Predictions appear at rest.",
                    isExpanded: binding(for: .layouts)
                ) {
                    HelpBullet(text: "Logical keeps the alphabet easy to learn.")
                    HelpBullet(text: "Efficiency optimizes common English letters.")
                    HelpBullet(text: "Custom layouts stay available in 8-section mode.")
                    HelpBullet(text: "When both dials rest at center, ERICK shows up to three predictions.")
                }

                ExpandableHelpSection(
                    title: "Controller Typing",
                    summary: "A controller mirrors the two dials with both analog sticks.",
                    isExpanded: binding(for: .controller)
                ) {
                    HelpBullet(text: "Use both analog sticks the same way you use the touch dials.")
                    HelpBullet(text: "Calibrate dead zone and Y-axis inversion in Controller Diagnostics before drills.")
                    HelpBullet(text: "Start controller drills only after the touch version feels comfortable.")
                }
            }
            .padding()
        }
        .navigationTitle("How to Type")
        .navigationBarTitleDisplayMode(.inline)
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
