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
    val isFreeform: Boolean = false,
    val section: PracticeLessonSection = PracticeLessonSection.START_HERE,
    val recommendedStep: Int? = null,
    val setupReason: String = ""
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

enum class PracticeLessonSection {
    START_HERE,
    FOLLOW_UP,
    ADVANCED
}

const val QUOTE_PRACTICE_LESSON_ID = "quote_practice"

private val quickstartStepTemplates = listOf(
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
        id = "first_letters",
        title = "First Letters",
        focus = "Start with the clearest first word on the shipped 6-section logical layout.",
        instructions = listOf(
            "This lesson automatically enables 6-section mode, the Logical layout, and Quick Type.",
            "Stay on one short word so you can learn row selection and commit timing before utilities or symbols.",
            "If ERICK is not active in the practice field yet, use the lesson actions at the bottom to switch keyboards or open settings."
        ),
        exercises = listOf(
            PracticeExercise(
                id = "letters",
                title = "Letters",
                coaching = "Use the wider 6-section targets to type one short word from the first logical rows.",
                targetText = "face"
            )
        ),
        successHint = "Success condition: finish the short opening word cleanly at least once.",
        setup = PracticeLessonSetup(
            sixSectionDial = true,
            layoutType = PreferencesManager.LAYOUT_LOGICAL,
            inputMode = PreferencesManager.INPUT_MODE_INSTANT
        ),
        section = PracticeLessonSection.START_HERE,
        recommendedStep = 1,
        setupReason = "6-section + Logical + Quick Type gives the largest targets and the easiest preview order for the first lesson."
    ),
    PracticeLesson(
        id = "utility_swipes",
        title = "Utility Swipes",
        focus = "Practice the right-dial actions before adding more rows or modes.",
        instructions = listOf(
            "This lesson stays in 6-section mode so the rotated utility wheel is easy to inspect while you drill.",
            "Practice space, period, and symbols in isolation before switching dial modes.",
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
        ),
        section = PracticeLessonSection.START_HERE,
        recommendedStep = 2,
        setupReason = "The shipped 6-section utility wheel is easiest to learn when utilities are drilled on their own."
    ),
    PracticeLesson(
        id = "numbers_and_symbols",
        title = "Numbers And Symbols",
        focus = "Add short number targets and a dedicated symbols-layer target after the utility drill.",
        instructions = listOf(
            "This lesson stays in 6-section mode with the Logical layout and Quick Type.",
            "Type one short number sequence first, then open the symbols layer for a single symbol target.",
            "The goal is to add one new surface at a time instead of relearning everything in one lesson."
        ),
        exercises = listOf(
            PracticeExercise(
                id = "numbers",
                title = "Numbers",
                coaching = "Stay in 6-section mode and type a short number sequence without changing the preset.",
                targetText = "907"
            ),
            PracticeExercise(
                id = "symbols",
                title = "Symbols",
                coaching = "Open the dedicated Symbols layer and type a question mark from the preview.",
                targetText = "?"
            )
        ),
        successHint = "Success condition: complete the numbers drill and the symbols drill in sequence.",
        setup = PracticeLessonSetup(
            sixSectionDial = true,
            layoutType = PreferencesManager.LAYOUT_LOGICAL,
            inputMode = PreferencesManager.INPUT_MODE_INSTANT
        ),
        section = PracticeLessonSection.START_HERE,
        recommendedStep = 3,
        setupReason = "This keeps the dial mode stable while you add numbers and the symbols layer one step at a time."
    ),
    PracticeLesson(
        id = "eight_section_transition",
        title = "8-Section Transition",
        focus = "Move to the classic eight-direction dial only after the first 6-section lessons feel stable.",
        instructions = listOf(
            "This lesson automatically applies 8-section mode, the Logical layout, and Quick Type.",
            "Work through one short word, one number target, and one punctuation target so you can feel the tighter segment spacing in context.",
            "If 8-section still feels noisy, return to the earlier 6-section lessons and come back later."
        ),
        exercises = listOf(
            PracticeExercise(
                id = "letters",
                title = "Letters",
                coaching = "Start with a short word so you can compare the 8-section feel against the 6-section lessons you already finished.",
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
                coaching = "Finish with a period so you end the transition lesson with a utility symbol in context.",
                targetText = "go."
            )
        ),
        successHint = "Success condition: finish all three drills and decide whether 8-section feels ready for regular practice.",
        setup = PracticeLessonSetup(
            sixSectionDial = false,
            layoutType = PreferencesManager.LAYOUT_LOGICAL,
            inputMode = PreferencesManager.INPUT_MODE_INSTANT
        ),
        section = PracticeLessonSection.START_HERE,
        recommendedStep = 4,
        setupReason = "8-section is easier to evaluate once the row-selection and utility basics already feel familiar."
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
        ),
        section = PracticeLessonSection.FOLLOW_UP,
        setupReason = "Assisted mode is a follow-up path after the default two-handed Quick Type route feels understandable."
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
        ),
        section = PracticeLessonSection.FOLLOW_UP,
        setupReason = "Controller practice belongs after the touch workflow is understandable, because both sticks mirror the same chord logic."
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
        isFreeform = true,
        section = PracticeLessonSection.ADVANCED,
        setupReason = "Freeform quote practice is the advanced phase after the shorter guided drills stop feeling difficult."
    )
)

fun quickstartStepsForLanguage(languageKey: String): List<QuickstartStep> {
    return quickstartStepTemplates.map { step ->
        step.copy(
            title = erickText(languageKey, step.title),
            summary = erickText(languageKey, step.summary),
            details = erickText(languageKey, step.details),
            tryNext = erickText(languageKey, step.tryNext)
        )
    }
}

fun practiceLessonsForLanguage(languageKey: String): List<PracticeLesson> {
    return practiceLessons.map { lesson ->
        lesson.copy(
            title = erickText(languageKey, lesson.title),
            focus = erickText(languageKey, lesson.focus),
            instructions = lesson.instructions.map { instruction -> erickText(languageKey, instruction) },
            exercises = lesson.exercises.map { exercise ->
                exercise.copy(
                    title = erickText(languageKey, exercise.title),
                    coaching = erickText(languageKey, exercise.coaching),
                    targetText = erickText(languageKey, exercise.targetText)
                )
            },
            successHint = erickText(languageKey, lesson.successHint),
            setupReason = erickText(languageKey, lesson.setupReason)
        )
    }
}