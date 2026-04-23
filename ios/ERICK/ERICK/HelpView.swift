import SwiftUI

struct HelpView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                helpSection(title: "Learning Path") {
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Start with the quickstart on the main screen, then work through 6-section basics, utility swipes, assisted one-handed typing, controller drills, and finally quote practice.")
                        NavigationLink(destination: PracticeHubView()) {
                            Text("Open Practice Lessons")
                                .font(.headline)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                        }
                        .buttonStyle(.borderedProminent)
                    }
                }

                helpSection(title: "Chord Mechanics") {
                    Text("""
                    ERICK uses a two-dial chording system:

                    1. Move the LEFT dial to choose a row of letters
                    2. Move the RIGHT dial to choose the specific letter in that row
                    3. Release both dials to commit the chord

                    The preview bar shows the currently available letters for the selected row.
                    """)
                }

                helpSection(title: "6-Section Utility Wheel") {
                    Text("""
                    In 6-section mode, the rotated utility wheel is:

                    • N → Symbols
                    • NE → Shift
                    • SE → Space
                    • S → Period
                    • SW → Enter
                    • NW → Backspace

                    In 8-section mode, the full 8-direction utility wheel remains available.
                    """)
                }

                helpSection(title: "Input Modes") {
                    Text("""
                    • Instant commits the chord as soon as both dials release
                    • Confirm lets you preview and confirm before committing
                    • Assisted locks the left-side row so one-handed users can finish from the letter side
                    """)
                }

                helpSection(title: "Layouts and Predictions") {
                    Text("""
                    • Logical (A–Z) keeps the alphabet easy to learn
                    • Efficiency optimizes common English letters
                    • Custom layouts stay available in 8-section mode

                    When both dials rest at center, the suggestion bar shows up to three word predictions.
                    """)
                }

                helpSection(title: "Controller Typing") {
                    Text("""
                    Bluetooth and USB game controllers mirror the left and right dials with the analog sticks. Use the same learning path for controller drills after you are comfortable with the touch version.
                    """)
                }
            }
            .padding()
        }
        .navigationTitle("How to Type")
        .navigationBarTitleDisplayMode(.inline)
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
