import SwiftUI

struct HelpView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                helpSection(title: "Chord Mechanics") {
                    Text("""
                    ERICK uses a two-joystick chording system:

                    1. Swipe the LEFT dial in one of 8 directions to select a character group
                    2. Swipe the RIGHT dial to select a specific character within the group
                    3. Release both dials — the character is typed

                    The preview bar above the dials shows which characters are available as you swipe.
                    """)
                }

                helpSection(title: "Right Dial Shortcuts") {
                    Text("""
                    When only the right dial is swiped (left dial at center):

                    • East → Space
                    • West → Backspace
                    • North → Enter
                    • South → Home (move cursor to start)
                    • NE → Period (.)
                    • SE → Comma (,)
                    • NW → Toggle Caps Lock
                    • SW → End (move cursor to end)
                    """)
                }

                helpSection(title: "Shift & Caps Lock") {
                    Text("""
                    • Swipe NW on the right dial to toggle Caps Lock
                    • Shift activates automatically after certain punctuation
                    • A shift indicator appears below the suggestion bar when active
                    """)
                }

                helpSection(title: "Word Predictions") {
                    Text("""
                    When both dials are at the center position, a suggestion bar appears with up to 3 word predictions. Tap any suggestion to insert it.

                    Predictions update as you type and learn from common English words.
                    """)
                }

                helpSection(title: "Logical vs. Efficiency Layout") {
                    Text("""
                    • Logical (A–Z): Letters arranged alphabetically — easy to learn
                    • Efficiency: Optimized for English letter frequency — faster for experienced users
                    • Custom: Create your own chord-to-character mappings in Settings
                    """)
                }

                helpSection(title: "Physical Controller") {
                    Text("""
                    Connect a Bluetooth or USB game controller. The left and right analog sticks map directly to the left and right dials for hands-free typing.
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
