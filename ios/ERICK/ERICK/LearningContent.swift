import Foundation

struct QuickstartStepData: Identifiable {
    let id: String
    let title: String
    let summary: String
    let details: String
    let tryNext: String
}

struct PracticeLessonData: Identifiable {
    let id: String
    let title: String
    let focus: String
    let instructions: [String]
    let targetText: String?
    let successHint: String
    let isFreeform: Bool
}

enum LearningProgressStore {
    static let quickstartCompletedKey = "learning.quickstartCompleted"
    static let quickstartDismissedKey = "learning.quickstartDismissed"
    static let quickstartStepKey = "learning.quickstartStep"
    static let attemptedLessonsKey = "learning.attemptedLessons"
    static let completedLessonsKey = "learning.completedLessons"

    static func decodeSet(_ rawValue: String) -> Set<String> {
        Set(rawValue.split(separator: ",").map { String($0) }.filter { !$0.isEmpty })
    }

    static func encodeSet(_ values: Set<String>) -> String {
        values.sorted().joined(separator: ",")
    }
}

let quotePracticeLessonId = "quote_practice"

let erickQuickstartSteps: [QuickstartStepData] = [
    QuickstartStepData(
        id: "dials_and_preview",
        title: "Dials and Preview",
        summary: "The left dial picks a row and the right dial picks the character.",
        details: "Hold the left dial in a direction to expose a letter row. Then move the right dial to choose the specific character in that row. Release both dials to commit the letter.",
        tryNext: "Try this next: open Practice Lessons and start with 6-Section Basics."
    ),
    QuickstartStepData(
        id: "utility_swipes",
        title: "Utility Swipes",
        summary: "The right dial alone handles actions like space, shift, period, enter, backspace, and symbols.",
        details: "In 6-section mode the rotated utility wheel is N = Symbols, NE = Shift, SE = Space, S = Period, SW = Enter, NW = Backspace. In 8-section mode the right dial exposes the full 8-direction utility wheel.",
        tryNext: "Try this next: run the Utility Swipes drill and type go."
    ),
    QuickstartStepData(
        id: "input_modes",
        title: "Input Modes and One-Handed Typing",
        summary: "Instant, Confirm, and Assisted modes change how and when a chord commits.",
        details: "Assisted mode is the one-handed path. It locks the left-side row selection so you can finish the chord from the letter side. Pair it with Left-Handed Mode if you prefer the physical dials swapped.",
        tryNext: "Try this next: switch to Assisted mode and open the Assisted One-Handed drill."
    ),
    QuickstartStepData(
        id: "controllers",
        title: "Controller Typing",
        summary: "A physical controller mirrors the two dial inputs with both analog sticks.",
        details: "Use Controller Diagnostics on Android to tune dead zone and Y-axis inversion, then practice typing short targets with both sticks. On iOS, controller support follows the same two-stick model in the keyboard extension.",
        tryNext: "Try this next: connect a controller and start the Controller Drill."
    )
]

let erickPracticeLessons: [PracticeLessonData] = [
    PracticeLessonData(
        id: "eight_section_basics",
        title: "8-Section Basics",
        focus: "Learn the classic eight-direction chord flow.",
        instructions: [
            "Switch Dial Section Mode to 8-section.",
            "Use the Logical layout so the first drills match the alphabet.",
            "Type the target text below using the keyboard itself."
        ],
        targetText: "a",
        successHint: "Success condition: type the target letter exactly once.",
        isFreeform: false
    ),
    PracticeLessonData(
        id: "six_section_basics",
        title: "6-Section Basics",
        focus: "Practice the shipped 6-section geometry and the logical preview order.",
        instructions: [
            "Enable 6-section mode in Settings.",
            "Notice that the first left-dial row reads a, b, c, d, e, f in the preview.",
            "Type the target letter below to complete the drill."
        ],
        targetText: "a",
        successHint: "Success condition: type the first 6-section target correctly.",
        isFreeform: false
    ),
    PracticeLessonData(
        id: "utility_swipes",
        title: "Utility Swipes",
        focus: "Practice the right-dial utility actions in your current dial mode.",
        instructions: [
            "Type the letters g and o using your normal chord flow.",
            "Finish the target with your current dial mode's period utility swipe.",
            "If you are in 6-section mode, period is the south utility swipe."
        ],
        targetText: "go.",
        successHint: "Success condition: finish the phrase with a utility period.",
        isFreeform: false
    ),
    PracticeLessonData(
        id: "assisted_one_handed",
        title: "Assisted One-Handed",
        focus: "Lock the left-side row and finish the chord from the letter side.",
        instructions: [
            "Switch Input Mode to Assisted.",
            "Turn on Left-Handed Mode if it makes the physical side routing easier for you.",
            "Type the short target below without leaving Assisted mode."
        ],
        targetText: "be",
        successHint: "Success condition: complete one short word with the assisted lock flow.",
        isFreeform: false
    ),
    PracticeLessonData(
        id: "controller_drill",
        title: "Controller Drill",
        focus: "Build dual-stick timing with the same two-stick model used by the keyboard extension.",
        instructions: [
            "Connect a controller and switch to ERICK in any text field.",
            "Use both analog sticks to type the target below.",
            "If vertical movement feels reversed, adjust the controller behavior you prefer before retrying."
        ],
        targetText: "go",
        successHint: "Success condition: type the target using the controller sticks.",
        isFreeform: false
    ),
    PracticeLessonData(
        id: quotePracticeLessonId,
        title: "Quote Practice",
        focus: "Use the existing freeform quote practice mode once the drills feel easy.",
        instructions: [
            "Launch the quote practice mode from this hub.",
            "Use it as the advanced phase after drills and quickstart.",
            "A clean run still triggers the subtle celebration effect."
        ],
        targetText: nil,
        successHint: "Advanced mode: there is no fixed target here.",
        isFreeform: true
    )
]