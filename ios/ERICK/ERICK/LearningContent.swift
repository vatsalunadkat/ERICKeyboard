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
    let exercises: [PracticeExerciseData]
    let successHint: String
    let setup: PracticeLessonSetupData?
    let isFreeform: Bool
    let section: PracticeLessonSectionData
    let recommendedStep: Int?
    let setupReason: String
}

struct PracticeExerciseData: Identifiable {
    let id: String
    let title: String
    let coaching: String
    let targetText: String
}

struct PracticeLessonSetupData {
    let sixSectionDial: Bool
    let layoutType: String
    let inputMode: String
}

enum PracticeLessonSectionData {
    case startHere
    case followUp
    case advanced
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

private let erickQuickstartStepTemplates: [QuickstartStepData] = [
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
        id: "first_letters",
        title: "First Letters",
        focus: "Start with the clearest first word on the shipped 6-section logical layout.",
        instructions: [
            "This lesson automatically enables 6-section mode, the Logical layout, and Quick Type.",
            "Stay on one short word so you can learn row selection and commit timing before utilities or symbols.",
            "If ERICK is not active in the practice field yet, use the lesson actions at the bottom to focus the field, reapply the preset, or open settings."
        ],
        exercises: [
            PracticeExerciseData(
                id: "letters",
                title: "Letters",
                coaching: "Use the wider 6-section targets to type one short word from the first logical rows.",
                targetText: "face"
            )
        ],
        successHint: "Success condition: finish the short opening word cleanly at least once.",
        setup: PracticeLessonSetupData(sixSectionDial: true, layoutType: "logical", inputMode: "instant"),
        isFreeform: false,
        section: .startHere,
        recommendedStep: 1,
        setupReason: "6-section + Logical + Quick Type gives the largest targets and the easiest preview order for the first lesson."
    ),
    PracticeLessonData(
        id: "utility_swipes",
        title: "Utility Swipes",
        focus: "Practice the right-dial actions before adding more rows or modes.",
        instructions: [
            "This lesson stays in 6-section mode so the rotated utility wheel is easy to inspect while you drill.",
            "Practice space, period, and symbols in isolation before switching dial modes.",
            "If you need a reminder, the utility preview updates live while you hold the right dial."
        ],
        exercises: [
            PracticeExerciseData(
                id: "space",
                title: "Space",
                coaching: "Type two short words separated by the space utility swipe.",
                targetText: "go on"
            ),
            PracticeExerciseData(
                id: "period",
                title: "Period",
                coaching: "Repeat the phrase and finish it with the south period swipe.",
                targetText: "go."
            ),
            PracticeExerciseData(
                id: "symbols",
                title: "Symbols",
                coaching: "Toggle into Symbols and type a question mark to round out the utility lesson.",
                targetText: "?"
            )
        ],
        successHint: "Success condition: complete the space, period, and symbols drills.",
        setup: PracticeLessonSetupData(sixSectionDial: true, layoutType: "logical", inputMode: "instant"),
        isFreeform: false,
        section: .startHere,
        recommendedStep: 2,
        setupReason: "The shipped 6-section utility wheel is easiest to learn when utilities are drilled on their own."
    ),
    PracticeLessonData(
        id: "numbers_and_symbols",
        title: "Numbers And Symbols",
        focus: "Add short number targets and a dedicated symbols-layer target after the utility drill.",
        instructions: [
            "This lesson stays in 6-section mode with the Logical layout and Quick Type.",
            "Type one short number sequence first, then open the symbols layer for a single symbol target.",
            "The goal is to add one new surface at a time instead of relearning everything in one lesson."
        ],
        exercises: [
            PracticeExerciseData(
                id: "numbers",
                title: "Numbers",
                coaching: "Stay in 6-section mode and type a short number sequence without changing the preset.",
                targetText: "907"
            ),
            PracticeExerciseData(
                id: "symbols",
                title: "Symbols",
                coaching: "Open the dedicated Symbols layer and type a question mark from the preview.",
                targetText: "?"
            )
        ],
        successHint: "Success condition: complete the numbers drill and the symbols drill in sequence.",
        setup: PracticeLessonSetupData(sixSectionDial: true, layoutType: "logical", inputMode: "instant"),
        isFreeform: false,
        section: .startHere,
        recommendedStep: 3,
        setupReason: "This keeps the dial mode stable while you add numbers and the symbols layer one step at a time."
    ),
    PracticeLessonData(
        id: "eight_section_transition",
        title: "8-Section Transition",
        focus: "Move to the classic eight-direction dial only after the first 6-section lessons feel stable.",
        instructions: [
            "This lesson automatically applies 8-section mode, the Logical layout, and Quick Type.",
            "Work through one short word, one number target, and one punctuation target so you can feel the tighter segment spacing in context.",
            "If 8-section still feels noisy, return to the earlier 6-section lessons and come back later."
        ],
        exercises: [
            PracticeExerciseData(
                id: "letters",
                title: "Letters",
                coaching: "Start with a short word so you can compare the 8-section feel against the 6-section lessons you already finished.",
                targetText: "cat"
            ),
            PracticeExerciseData(
                id: "numbers",
                title: "Numbers",
                coaching: "Stay in 8-section mode and type a short number sequence without changing the preset.",
                targetText: "120"
            ),
            PracticeExerciseData(
                id: "punctuation",
                title: "Punctuation",
                coaching: "Finish with a period so you end the transition lesson with a utility symbol in context.",
                targetText: "go."
            )
        ],
        successHint: "Success condition: finish all three drills and decide whether 8-section feels ready for regular practice.",
        setup: PracticeLessonSetupData(sixSectionDial: false, layoutType: "logical", inputMode: "instant"),
        isFreeform: false,
        section: .startHere,
        recommendedStep: 4,
        setupReason: "8-section is easier to evaluate once the row-selection and utility basics already feel familiar."
    ),
    PracticeLessonData(
        id: "assisted_one_handed",
        title: "Assisted One-Handed",
        focus: "Lock the left-side row and finish the chord from the letter side.",
        instructions: [
            "This lesson automatically switches ERICK to Assisted mode while keeping the Logical layout visible.",
            "Use the lesson settings button if you also want to inspect Left-Handed Mode for your physical setup.",
            "Work through a letter drill, a number drill, and a punctuation drill without leaving Assisted mode."
        ],
        exercises: [
            PracticeExerciseData(
                id: "letters",
                title: "Letters",
                coaching: "Lock a row with the left dial, then finish the word from the right side.",
                targetText: "be"
            ),
            PracticeExerciseData(
                id: "numbers",
                title: "Numbers",
                coaching: "Keep the same assisted flow while you type a simple number sequence.",
                targetText: "12"
            ),
            PracticeExerciseData(
                id: "punctuation",
                title: "Punctuation",
                coaching: "Finish with a period so the lesson still covers a utility symbol.",
                targetText: "go."
            )
        ],
        successHint: "Success condition: finish all drills while staying in Assisted mode.",
        setup: PracticeLessonSetupData(sixSectionDial: false, layoutType: "logical", inputMode: "assisted"),
        isFreeform: false,
        section: .followUp,
        recommendedStep: nil,
        setupReason: "Assisted mode is a follow-up path after the default two-handed Quick Type route feels understandable."
    ),
    PracticeLessonData(
        id: "controller_drill",
        title: "Controller Drill",
        focus: "Build dual-stick timing with the same two-stick model used by the keyboard extension.",
        instructions: [
            "This lesson keeps the keyboard in 8-section Logical Quick Type so the controller drill starts from the default map.",
            "Use the bottom actions to focus the practice field, then type letters, numbers, and punctuation with both sticks.",
            "If the sticks feel noisy or reversed, adjust the controller behavior you prefer before retrying the drill."
        ],
        exercises: [
            PracticeExerciseData(
                id: "letters",
                title: "Letters",
                coaching: "Use both analog sticks together to type a short word.",
                targetText: "go"
            ),
            PracticeExerciseData(
                id: "numbers",
                title: "Numbers",
                coaching: "Stay on the controller and type a simple repeated number target.",
                targetText: "88"
            ),
            PracticeExerciseData(
                id: "punctuation",
                title: "Punctuation",
                coaching: "Finish the controller lesson with a period so you touch a utility symbol too.",
                targetText: "go."
            )
        ],
        successHint: "Success condition: finish all controller drills with the active ERICK preset.",
        setup: PracticeLessonSetupData(sixSectionDial: false, layoutType: "logical", inputMode: "instant"),
        isFreeform: false,
        section: .followUp,
        recommendedStep: nil,
        setupReason: "Controller practice belongs after the touch workflow is understandable, because both sticks mirror the same chord logic."
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
        exercises: [],
        successHint: "Advanced mode: there is no fixed target here.",
        setup: PracticeLessonSetupData(sixSectionDial: false, layoutType: "logical", inputMode: "instant"),
        isFreeform: true,
        section: .advanced,
        recommendedStep: nil,
        setupReason: "Freeform quote practice is the advanced phase after the shorter guided drills stop feeling difficult."
    )
]

func erickQuickstartSteps(for languageKey: String) -> [QuickstartStepData] {
    erickQuickstartStepTemplates.map { step in
        QuickstartStepData(
            id: step.id,
            title: erickText(step.title, languageKey: languageKey),
            summary: erickText(step.summary, languageKey: languageKey),
            details: erickText(step.details, languageKey: languageKey),
            tryNext: erickText(step.tryNext, languageKey: languageKey)
        )
    }
}