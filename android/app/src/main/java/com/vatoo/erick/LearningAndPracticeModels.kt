package com.vatoo.erick

data class QuickstartStep(
    val id: String,
    val title: String,
    val summary: String,
    val details: String,
    val tryNext: String
)

data class PracticeLesson(
    val id: String,
    val title: String,
    val focus: String,
    val instructions: List<String>,
    val exercises: List<PracticeExercise> = emptyList(),
    val successHint: String,
    val setup: PracticeLessonSetup? = null,
    val isFreeform: Boolean = false
)

data class PracticeExercise(
    val id: String,
    val title: String,
    val coaching: String,
    val targetText: String
)

data class PracticeLessonSetup(
    val sixSectionDial: Boolean,
    val layoutType: String,
    val inputMode: String
)

const val QUOTE_PRACTICE_LESSON_ID = "quote_practice"

val quickstartSteps = listOf(
    QuickstartStep(
        id = "dials_and_preview",
        title = "Dials and Preview",
        summary = "The left dial picks a row and the right dial picks the character.",
        details = "Hold the left dial in a direction to expose a letter row. Then move the right dial to choose the specific character in that row. Release both dials to commit the letter.",
        tryNext = "Try this next: open Practice Lessons and start with 6-Section Basics."
    ),
    QuickstartStep(
        id = "utility_swipes",
        title = "Utility Swipes",
        summary = "The right dial alone handles actions like space, shift, period, enter, backspace, and symbols.",
        details = "In 6-section mode the rotated utility wheel is N = Symbols, NE = Shift, SE = Space, S = Period, SW = Enter, NW = Backspace. In 8-section mode the right dial exposes the full 8-direction utility wheel.",
        tryNext = "Try this next: run the Utility Swipes drill and type go."
    ),
    QuickstartStep(
        id = "input_modes",
        title = "Input Modes and One-Handed Typing",
        summary = "Instant, Confirm, and Assisted modes change how and when a chord commits.",
        details = "Assisted mode is the one-handed path. It locks the left-side row selection so you can finish the chord from the letter side. Pair it with Left-Handed Mode if you prefer the physical dials swapped.",
        tryNext = "Try this next: switch to Assisted mode and open the Assisted One-Handed drill."
    ),
    QuickstartStep(
        id = "controllers",
        title = "Controller Typing",
        summary = "A physical controller mirrors the two dial inputs with both analog sticks.",
        details = "Use Controller Diagnostics to tune dead zone and Y-axis inversion, then practice typing short targets with both sticks. The diagnostics screen uses the same shared controller normalization as the IME.",
        tryNext = "Try this next: connect a controller, open Controller Diagnostics, and then start the Controller Drill."
    )
)

val practiceLessons = listOf(
    PracticeLesson(
        id = "eight_section_basics",
        title = "8-Section Basics",
        focus = "Learn the classic eight-direction chord flow.",
        instructions = listOf(
            "This lesson automatically applies 8-section mode, the Logical layout, and Quick Type.",
            "Work through letters, then numbers, then punctuation so you cover the core basics in one pass.",
            "If ERICK is not active in the practice field yet, use the lesson actions at the bottom to switch keyboards or open settings."
        ),
        exercises = listOf(
            PracticeExercise(
                id = "letters",
                title = "Letters",
                coaching = "Start with a short word so you can practice row selection and character selection together.",
                targetText = "cat"
            ),
            PracticeExercise(
                id = "numbers",
                title = "Numbers",
                coaching = "Stay in 8-section mode and type a short number sequence without changing the preset.",
                targetText = "120"
            ),
            PracticeExercise(
                id = "punctuation",
                title = "Punctuation",
                coaching = "Finish with a period so you end the lesson with a utility symbol in context.",
                targetText = "go."
            )
        ),
        successHint = "Success condition: finish all three drills from letters through punctuation.",
        setup = PracticeLessonSetup(
            sixSectionDial = false,
            layoutType = PreferencesManager.LAYOUT_LOGICAL,
            inputMode = PreferencesManager.INPUT_MODE_INSTANT
        )
    ),
    PracticeLesson(
        id = "six_section_basics",
        title = "6-Section Basics",
        focus = "Practice the shipped 6-section geometry and the logical preview order.",
        instructions = listOf(
            "This lesson automatically enables 6-section mode with the Logical layout and Quick Type.",
            "Notice that the first left-dial preview row reads a, b, c, d, e, f in order before you start typing.",
            "Move from letters to numbers and then to the symbols layer so you cover the full 6-section basics."
        ),
        exercises = listOf(
            PracticeExercise(
                id = "letters",
                title = "Letters",
                coaching = "Use the wider 6-section targets to type a short word from the first few logical rows.",
                targetText = "face"
            ),
            PracticeExercise(
                id = "numbers",
                title = "Numbers",
                coaching = "Stay in 6-section mode and type a short number sequence.",
                targetText = "907"
            ),
            PracticeExercise(
                id = "symbols",
                title = "Symbols",
                coaching = "Open the dedicated Symbols layer and type a question mark from the preview.",
                targetText = "?"
            )
        ),
        successHint = "Success condition: finish the letters, numbers, and symbols drills in sequence.",
        setup = PracticeLessonSetup(
            sixSectionDial = true,
            layoutType = PreferencesManager.LAYOUT_LOGICAL,
            inputMode = PreferencesManager.INPUT_MODE_INSTANT
        )
    ),
    PracticeLesson(
        id = "utility_swipes",
        title = "Utility Swipes",
        focus = "Practice the right-dial utility actions in your current dial mode.",
        instructions = listOf(
            "This lesson uses 6-section mode so the rotated utility wheel is easy to inspect while you drill.",
            "Practice space, period, and the symbols layer in a controlled order.",
            "If you need a reminder, the utility preview updates live while you hold the right dial."
        ),
        exercises = listOf(
            PracticeExercise(
                id = "space",
                title = "Space",
                coaching = "Type two short words separated by the space utility swipe.",
                targetText = "go on"
            ),
            PracticeExercise(
                id = "period",
                title = "Period",
                coaching = "Repeat the phrase and finish it with the south period swipe.",
                targetText = "go."
            ),
            PracticeExercise(
                id = "symbols",
                title = "Symbols",
                coaching = "Toggle into Symbols and type a question mark to round out the utility lesson.",
                targetText = "?"
            )
        ),
        successHint = "Success condition: complete the space, period, and symbols drills.",
        setup = PracticeLessonSetup(
            sixSectionDial = true,
            layoutType = PreferencesManager.LAYOUT_LOGICAL,
            inputMode = PreferencesManager.INPUT_MODE_INSTANT
        )
    ),
    PracticeLesson(
        id = "assisted_one_handed",
        title = "Assisted One-Handed",
        focus = "Lock the left-side row and finish the chord from the letter side.",
        instructions = listOf(
            "This lesson automatically switches ERICK to Assisted mode while keeping the Logical layout visible.",
            "Use the lesson settings button if you also want to inspect Left-Handed Mode for your physical setup.",
            "Work through a letter drill, a number drill, and a punctuation drill without leaving Assisted mode."
        ),
        exercises = listOf(
            PracticeExercise(
                id = "letters",
                title = "Letters",
                coaching = "Lock a row with the left dial, then finish the word from the right side.",
                targetText = "be"
            ),
            PracticeExercise(
                id = "numbers",
                title = "Numbers",
                coaching = "Keep the same assisted flow while you type a simple number sequence.",
                targetText = "12"
            ),
            PracticeExercise(
                id = "punctuation",
                title = "Punctuation",
                coaching = "Finish with a period so the lesson still covers a utility symbol.",
                targetText = "go."
            )
        ),
        successHint = "Success condition: finish all drills while staying in Assisted mode.",
        setup = PracticeLessonSetup(
            sixSectionDial = false,
            layoutType = PreferencesManager.LAYOUT_LOGICAL,
            inputMode = PreferencesManager.INPUT_MODE_ASSISTED
        )
    ),
    PracticeLesson(
        id = "controller_drill",
        title = "Controller Drill",
        focus = "Build dual-stick timing after calibrating your controller.",
        instructions = listOf(
            "This lesson keeps the keyboard in 8-section Logical Quick Type so the controller drill starts from the default map.",
            "Use the bottom actions to switch to ERICK, then type letters, numbers, and punctuation with both sticks.",
            "If the sticks feel noisy or reversed, open Controller Diagnostics or Settings before retrying the drill."
        ),
        exercises = listOf(
            PracticeExercise(
                id = "letters",
                title = "Letters",
                coaching = "Use both analog sticks together to type a short word.",
                targetText = "go"
            ),
            PracticeExercise(
                id = "numbers",
                title = "Numbers",
                coaching = "Stay on the controller and type a simple repeated number target.",
                targetText = "88"
            ),
            PracticeExercise(
                id = "punctuation",
                title = "Punctuation",
                coaching = "Finish the controller lesson with a period so you touch a utility symbol too.",
                targetText = "go."
            )
        ),
        successHint = "Success condition: finish all controller drills with the active ERICK preset.",
        setup = PracticeLessonSetup(
            sixSectionDial = false,
            layoutType = PreferencesManager.LAYOUT_LOGICAL,
            inputMode = PreferencesManager.INPUT_MODE_INSTANT
        )
    ),
    PracticeLesson(
        id = QUOTE_PRACTICE_LESSON_ID,
        title = "Quote Practice",
        focus = "Use the existing freeform quote practice mode once the drills feel easy.",
        instructions = listOf(
            "Launch the quote practice mode from this hub.",
            "Use it as the advanced phase after drills and quickstart.",
            "A clean run still triggers the subtle celebration effect."
        ),
        successHint = "Advanced mode: there is no fixed target here.",
        setup = PracticeLessonSetup(
            sixSectionDial = false,
            layoutType = PreferencesManager.LAYOUT_LOGICAL,
            inputMode = PreferencesManager.INPUT_MODE_INSTANT
        ),
        isFreeform = true
    )
)