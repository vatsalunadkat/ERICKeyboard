import SwiftUI

struct QuickstartView: View {
    @Environment(\.erickLanguageKey) private var keyboardLanguage
    @Binding var currentStep: Int
    let onComplete: () -> Void
    let onSkip: () -> Void
    @State private var showDetails = false

    private var quickstartSteps: [QuickstartStepData] {
        erickQuickstartSteps(for: keyboardLanguage)
    }

    private var boundedStep: Int {
        min(max(currentStep, 0), quickstartSteps.count - 1)
    }

    private var step: QuickstartStepData {
        quickstartSteps[boundedStep]
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("\(erickText("Quickstart", languageKey: keyboardLanguage)) \(boundedStep + 1) \(erickText("of", languageKey: keyboardLanguage)) \(quickstartSteps.count)")
                        .font(.caption)
                        .fontWeight(.semibold)
                        .foregroundColor(.accentColor)

                    Text(step.title)
                        .font(.title2)
                        .fontWeight(.bold)

                    Text(step.summary)
                        .font(.title3)

                    DisclosureGroup(showDetails ? erickText("Hide Detail", languageKey: keyboardLanguage) : erickText("More Detail", languageKey: keyboardLanguage), isExpanded: $showDetails) {
                        Text(step.details)
                            .font(.body)
                            .foregroundColor(.secondary)
                            .padding(.top, 6)
                    }

                    Text(step.tryNext)
                        .font(.footnote)
                        .foregroundColor(.secondary)
                        .padding()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color(uiColor: .secondarySystemBackground))
                        .cornerRadius(12)

                    ViewThatFits(in: .horizontal) {
                        HStack(spacing: 12) {
                            Button(action: onSkip) {
                                adaptiveButtonLabel(erickText("Skip", languageKey: keyboardLanguage), fillWidth: true)
                            }
                            .buttonStyle(.bordered)

                            if boundedStep > 0 {
                                Button {
                                    currentStep = max(0, boundedStep - 1)
                                } label: {
                                    adaptiveButtonLabel(erickText("Back", languageKey: keyboardLanguage), fillWidth: true)
                                }
                                .buttonStyle(.bordered)
                            }

                            Button {
                                if boundedStep == quickstartSteps.count - 1 {
                                    onComplete()
                                } else {
                                    currentStep = min(quickstartSteps.count - 1, boundedStep + 1)
                                }
                            } label: {
                                adaptiveButtonLabel(erickText(boundedStep == quickstartSteps.count - 1 ? "Finish" : "Next", languageKey: keyboardLanguage), fillWidth: true)
                            }
                            .buttonStyle(.borderedProminent)
                        }

                        VStack(spacing: 12) {
                            Button(action: onSkip) {
                                adaptiveButtonLabel(erickText("Skip", languageKey: keyboardLanguage), fillWidth: true)
                            }
                            .buttonStyle(.bordered)

                            if boundedStep > 0 {
                                Button {
                                    currentStep = max(0, boundedStep - 1)
                                } label: {
                                    adaptiveButtonLabel(erickText("Back", languageKey: keyboardLanguage), fillWidth: true)
                                }
                                .buttonStyle(.bordered)
                            }

                            Button {
                                if boundedStep == quickstartSteps.count - 1 {
                                    onComplete()
                                } else {
                                    currentStep = min(quickstartSteps.count - 1, boundedStep + 1)
                                }
                            } label: {
                                adaptiveButtonLabel(erickText(boundedStep == quickstartSteps.count - 1 ? "Finish" : "Next", languageKey: keyboardLanguage), fillWidth: true)
                            }
                            .buttonStyle(.borderedProminent)
                        }
                    }
                }
                .padding()
            }
            .navigationTitle(erickText("Quickstart", languageKey: keyboardLanguage))
            .navigationBarTitleDisplayMode(.inline)
        }
        .onChange(of: boundedStep) { _ in
            showDetails = false
        }
    }

    private func adaptiveButtonLabel(_ title: String, fillWidth: Bool) -> some View {
        Text(title)
            .font(.headline)
            .lineLimit(1)
            .minimumScaleFactor(0.85)
            .frame(maxWidth: fillWidth ? .infinity : nil)
            .padding(.vertical, 12)
    }
}

struct PracticeHubView: View {
    @AppStorage(LearningProgressStore.attemptedLessonsKey) private var attemptedLessonsRaw = ""
    @AppStorage(LearningProgressStore.completedLessonsKey) private var completedLessonsRaw = ""
    @AppStorage("keyboard_language", store: learningAppGroupDefaults) private var keyboardLanguage = "english"
    @State private var infoLesson: PracticeLessonData?

    private var lessons: [PracticeLessonData] {
        erickPracticeLessons(for: keyboardLanguage)
    }

    private var attemptedLessons: Set<String> {
        LearningProgressStore.decodeSet(attemptedLessonsRaw)
    }

    private var completedLessons: Set<String> {
        LearningProgressStore.decodeSet(completedLessonsRaw)
    }

    private var nextRecommendedLesson: PracticeLessonData? {
        lessons.first { lesson in
            lesson.recommendedStep != nil && !completedLessons.contains(lesson.id)
        }
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                VStack(alignment: .leading, spacing: 8) {
                    Text(erickText("Pick a lesson", languageKey: keyboardLanguage))
                        .font(.title3)
                        .fontWeight(.bold)
                    Text(erickText("ERICK applies the lesson setup for you so you can focus on one drill at a time.", languageKey: keyboardLanguage))
                        .foregroundColor(.secondary)
                    Text("\(erickText("Progress", languageKey: keyboardLanguage)): \(completedLessons.count) \(erickText("completed", languageKey: keyboardLanguage)) / \(attemptedLessons.count) \(erickText("attempted", languageKey: keyboardLanguage))")
                        .font(.footnote)
                        .foregroundColor(.secondary)
                }
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(uiColor: .secondarySystemBackground))
                .cornerRadius(16)

                VStack(alignment: .leading, spacing: 10) {
                    Text(erickText("Recommended route", languageKey: keyboardLanguage))
                        .font(.headline)
                        .fontWeight(.bold)
                    Text(erickText("Start with the short 6-section lessons, then try the 8-section transition. Assisted and controller drills are follow-up paths.", languageKey: keyboardLanguage))
                        .font(.subheadline)
                    if let nextRecommendedLesson {
                        Text("\(erickText("Next recommended lesson", languageKey: keyboardLanguage)): \(erickText("Step", languageKey: keyboardLanguage)) \(nextRecommendedLesson.recommendedStep ?? 0) - \(nextRecommendedLesson.title)")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        NavigationLink {
                            PracticeLessonView(
                                lesson: nextRecommendedLesson,
                                startFromBeginning: false
                            )
                        } label: {
                            Text(erickText("Open Recommended Lesson", languageKey: keyboardLanguage))
                                .font(.headline)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 12)
                        }
                        .buttonStyle(.borderedProminent)
                    } else {
                        Text(erickText("You have finished the guided route. Use the follow-up paths or jump into Quote Practice.", languageKey: keyboardLanguage))
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color.accentColor.opacity(0.14))
                .cornerRadius(16)

                if keyboardLanguage != "english" {
                    let languageLabel = keyboardLanguageSelfDisplayName(keyboardLanguage)
                    VStack(alignment: .leading, spacing: 8) {
                        Text("\(languageLabel) \(erickText("typing tip", languageKey: keyboardLanguage))")
                            .font(.headline)
                            .fontWeight(.bold)
                        Text(erickText("In 8-section mode, extra language characters appear directly in the logical map. In 6-section mode, open Symbols to reach the extra language characters while the shipped utility wheel stays unchanged.", languageKey: keyboardLanguage))
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color.green.opacity(0.12))
                    .cornerRadius(16)
                }

                ForEach(practiceSectionModels(for: keyboardLanguage)) { sectionModel in
                    let sectionLessons = lessons.filter { $0.section == sectionModel.section }
                    if !sectionLessons.isEmpty {
                        VStack(alignment: .leading, spacing: 12) {
                            Text(sectionModel.title)
                                .font(.title3)
                                .fontWeight(.bold)
                            Text(sectionModel.summary)
                                .font(.footnote)
                                .foregroundColor(.secondary)

                            ForEach(sectionLessons) { lesson in
                                let isNextRecommended = nextRecommendedLesson?.id == lesson.id
                                VStack(alignment: .leading, spacing: 10) {
                                    HStack(alignment: .top, spacing: 12) {
                                        VStack(alignment: .leading, spacing: 4) {
                                            if let recommendedStep = lesson.recommendedStep {
                                                Text(isNextRecommended ? "\(erickText("Recommended next", languageKey: keyboardLanguage)) · \(erickText("Step", languageKey: keyboardLanguage)) \(recommendedStep)" : "\(erickText("Step", languageKey: keyboardLanguage)) \(recommendedStep)")
                                                    .font(.caption)
                                                    .fontWeight(.semibold)
                                                    .foregroundColor(isNextRecommended ? .accentColor : .secondary)
                                            }
                                            Text(lesson.title)
                                                .font(.headline)
                                                .foregroundColor(.primary)
                                            Text(compactLessonSummary(lesson, languageKey: keyboardLanguage))
                                                .font(.caption)
                                                .foregroundColor(.secondary)
                                            if !lesson.setupReason.isEmpty {
                                                Text("\(erickText("Why this setup", languageKey: keyboardLanguage)): \(lesson.setupReason)")
                                                    .font(.caption)
                                                    .foregroundColor(.secondary)
                                            }
                                        }
                                        Spacer()
                                        Button {
                                            infoLesson = lesson
                                        } label: {
                                            Image(systemName: "questionmark.circle")
                                                .font(.title3)
                                        }
                                        .buttonStyle(.plain)
                                    }

                                    HStack(spacing: 8) {
                                        if completedLessons.contains(lesson.id) {
                                            Image(systemName: "checkmark.circle.fill")
                                                .foregroundColor(.green)
                                        }
                                        Text(statusText(for: lesson.id, languageKey: keyboardLanguage))
                                            .font(.caption)
                                            .foregroundColor(statusColor(for: lesson.id))
                                    }

                                    NavigationLink {
                                        PracticeLessonView(
                                            lesson: lesson,
                                            startFromBeginning: completedLessons.contains(lesson.id)
                                        )
                                    } label: {
                                        Text(primaryActionLabel(for: lesson, isNextRecommended: isNextRecommended, languageKey: keyboardLanguage))
                                            .font(.headline)
                                            .frame(maxWidth: .infinity)
                                            .padding(.vertical, 12)
                                    }
                                    .buttonStyle(.borderedProminent)
                                }
                                .padding()
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .background(backgroundColor(for: lesson.id))
                                .cornerRadius(16)
                            }
                        }
                    }
                }
            }
            .padding()
        }
        .navigationTitle(erickText("Practice Lessons", languageKey: keyboardLanguage))
        .navigationBarTitleDisplayMode(.inline)
        .sheet(item: $infoLesson) { lesson in
            LessonHelpSheet(
                lesson: lesson,
                setupSummary: lesson.setup.map { formatLessonSetup($0, languageKey: keyboardLanguage) }
            )
        }
    }

    private func statusText(for lessonId: String, languageKey: String) -> String {
        if completedLessons.contains(lessonId) {
            return erickText("Completed", languageKey: languageKey)
        }
        if attemptedLessons.contains(lessonId) {
            return erickText("In progress", languageKey: languageKey)
        }
        return erickText("Not started", languageKey: languageKey)
    }

    private func statusColor(for lessonId: String) -> Color {
        if completedLessons.contains(lessonId) {
            return .green
        }
        return .secondary
    }

    private func backgroundColor(for lessonId: String) -> Color {
        if completedLessons.contains(lessonId) {
            return Color.green.opacity(0.18)
        }
        return Color(uiColor: .secondarySystemBackground)
    }

    private func primaryActionLabel(for lesson: PracticeLessonData, isNextRecommended: Bool, languageKey: String) -> String {
        let lessonId = lesson.id
        if completedLessons.contains(lessonId) {
            return erickText("Replay Lesson", languageKey: languageKey)
        }
        if attemptedLessons.contains(lessonId) {
            if isNextRecommended {
                return erickText("Continue Recommended Lesson", languageKey: languageKey)
            }
            return erickText("Continue Lesson", languageKey: languageKey)
        }
        if isNextRecommended, let step = lesson.recommendedStep {
            return "\(erickText("Start Step", languageKey: languageKey)) \(step)"
        }
        return erickText("Start Lesson", languageKey: languageKey)
    }
}

struct PracticeLessonView: View {
    let lesson: PracticeLessonData
    let startFromBeginning: Bool

    @Environment(\.erickLanguageKey) private var keyboardLanguage
    @Environment(\.scenePhase) private var scenePhase
    @AppStorage(LearningProgressStore.attemptedLessonsKey) private var attemptedLessonsRaw = ""
    @AppStorage(LearningProgressStore.completedLessonsKey) private var completedLessonsRaw = ""
    @AppStorage("hasEnabledKeyboard") private var hasEnabledKeyboard = false
    @AppStorage("layout_type", store: learningAppGroupDefaults) private var layoutType = "logical"
    @AppStorage("input_mode", store: learningAppGroupDefaults) private var inputMode = "instant"
    @AppStorage("six_section_dial", store: learningAppGroupDefaults) private var sixSectionDial = false

    @State private var typedText = ""
    @State private var currentExerciseIndex = 0
    @State private var completedExerciseIds: Set<String> = []
    @State private var recentCompletionLabel: String?
    @State private var isKeyboardActuallyEnabled = false
    @State private var showHelpSheet = false
    @FocusState private var practiceFieldFocused: Bool

    private var isCompleted: Bool {
        LearningProgressStore.decodeSet(completedLessonsRaw).contains(lesson.id)
    }

    private var lessonIsFinished: Bool {
        if lesson.isFreeform {
            return isCompleted
        }
        return isCompleted || completedExerciseIds.count == lesson.exercises.count
    }

    private var isKeyboardEnabled: Bool {
        hasEnabledKeyboard || isKeyboardActuallyEnabled
    }

    private var currentExercise: PracticeExerciseData? {
        guard lesson.exercises.indices.contains(currentExerciseIndex) else { return nil }
        return lesson.exercises[currentExerciseIndex]
    }

    private var setupMatchesLesson: Bool {
        guard let setup = lesson.setup else { return true }
        return setup.sixSectionDial == sixSectionDial && setup.layoutType == layoutType && setup.inputMode == inputMode
    }

    private var localizedLessons: [PracticeLessonData] {
        erickPracticeLessons(for: keyboardLanguage)
    }

    private var previousLesson: PracticeLessonData? {
        let currentIndex = localizedLessons.firstIndex { $0.id == lesson.id } ?? 0
        guard currentIndex > 0 else { return nil }
        return localizedLessons[currentIndex - 1]
    }

    private var nextLesson: PracticeLessonData? {
        let currentIndex = localizedLessons.firstIndex { $0.id == lesson.id } ?? 0
        guard currentIndex < localizedLessons.count - 1 else { return nil }
        return localizedLessons[currentIndex + 1]
    }

    private var contextActions: [LessonAction] {
        var actions: [LessonAction] = []

        if !isKeyboardEnabled {
            actions.append(
                LessonAction(id: "enable-keyboard", title: erickText("Open iOS Settings", languageKey: keyboardLanguage), prominent: true) {
                    if let url = URL(string: UIApplication.openSettingsURLString) {
                        UIApplication.shared.open(url)
                    }
                }
            )
        } else if !lesson.isFreeform && !practiceFieldFocused {
            actions.append(
                LessonAction(id: "focus-field", title: erickText("Focus Typing Field", languageKey: keyboardLanguage), prominent: true) {
                    practiceFieldFocused = true
                }
            )
        }

        if !lessonIsFinished && !setupMatchesLesson {
            actions.append(
                LessonAction(id: "apply-setup", title: erickText("Apply Setup", languageKey: keyboardLanguage), prominent: false) {
                    applyLessonSetup()
                }
            )
        }

        return actions
    }

    private var partActions: [LessonAction] {
        guard !lesson.isFreeform, lesson.exercises.count > 1 else { return [] }

        var actions: [LessonAction] = []
        if currentExerciseIndex > 0 {
            actions.append(
                LessonAction(id: "previous-part", title: erickText("Previous Part", languageKey: keyboardLanguage), prominent: false) {
                    currentExerciseIndex -= 1
                    typedText = ""
                    recentCompletionLabel = nil
                }
            )
        }
        if currentExerciseIndex < lesson.exercises.count - 1 {
            actions.append(
                LessonAction(id: "next-part", title: erickText("Next Part", languageKey: keyboardLanguage), prominent: true) {
                    currentExerciseIndex += 1
                    typedText = ""
                    recentCompletionLabel = nil
                }
            )
        }
        return actions
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                VStack(alignment: .leading, spacing: 8) {
                    Text(lessonHeaderLabel(lesson: lesson, currentExerciseIndex: currentExerciseIndex, languageKey: keyboardLanguage))
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundColor(.accentColor)
                    Text(currentExercise?.title ?? lesson.focus)
                        .font(.title3)
                        .fontWeight(.bold)
                    Text(lesson.isFreeform ? erickText("Freeform practice", languageKey: keyboardLanguage) : "\(completedExerciseIds.count) \(erickText("of", languageKey: keyboardLanguage)) \(lesson.exercises.count) \(erickText("parts done", languageKey: keyboardLanguage))")
                        .font(.footnote)
                        .foregroundColor(.secondary)
                    if let setup = lesson.setup {
                        Text(formatLessonSetup(setup, languageKey: keyboardLanguage))
                            .font(.footnote)
                            .foregroundColor(.secondary)
                    }
                    if !lesson.setupReason.isEmpty {
                        Text("\(erickText("Why this setup", languageKey: keyboardLanguage)): \(lesson.setupReason)")
                            .font(.footnote)
                            .foregroundColor(.secondary)
                    }
                }
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(uiColor: .secondarySystemBackground))
                .cornerRadius(16)

                if lesson.isFreeform {
                    VStack(alignment: .leading, spacing: 12) {
                        Text(erickText("Advanced Freeform Mode", languageKey: keyboardLanguage))
                            .font(.headline)
                        Text(erickText("Launch the quote practice screen when you want a longer freeform session with the current lesson preset.", languageKey: keyboardLanguage))
                            .foregroundColor(.secondary)

                        NavigationLink {
                            TypingGameView()
                        } label: {
                            Text(erickText("Launch Quote Practice", languageKey: keyboardLanguage))
                                .font(.headline)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 12)
                        }
                        .buttonStyle(.borderedProminent)
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(uiColor: .secondarySystemBackground))
                    .cornerRadius(16)
                } else {
                    VStack(alignment: .leading, spacing: 12) {
                        if let exercise = currentExercise {
                            Text(exercise.coaching)
                                .foregroundColor(.secondary)
                            Text(exercise.targetText)
                                .font(.largeTitle)
                                .fontWeight(.bold)
                        }

                        TextField(erickText("Type the drill target here", languageKey: keyboardLanguage), text: $typedText)
                            .textFieldStyle(.roundedBorder)
                            .textInputAutocapitalization(.never)
                            .autocorrectionDisabled()
                            .focused($practiceFieldFocused)

                        if let recentCompletionLabel {
                            HStack(spacing: 8) {
                                Image(systemName: "checkmark.circle.fill")
                                    .foregroundStyle(.green)
                                Text(recentCompletionLabel)
                                    .font(.subheadline)
                                    .fontWeight(.medium)
                                    .foregroundStyle(.green)
                            }
                            .padding(.horizontal, 12)
                            .padding(.vertical, 10)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .background(Color.green.opacity(0.12))
                            .cornerRadius(12)
                        }
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(uiColor: .secondarySystemBackground))
                    .cornerRadius(16)
                }

                if !contextActions.isEmpty {
                    ActionButtonGroup(actions: contextActions)
                }

                if !lessonIsFinished && !partActions.isEmpty {
                    ActionButtonGroup(actions: partActions)
                }

                if lessonIsFinished {
                    VStack(alignment: .leading, spacing: 12) {
                        Text(erickText("Lesson complete", languageKey: keyboardLanguage))
                            .font(.headline)
                            .foregroundColor(.green)
                        ViewThatFits(in: .horizontal) {
                            HStack(spacing: 12) {
                                Button(action: resetLessonProgress) {
                                    lessonActionLabel(erickText("Replay Lesson", languageKey: keyboardLanguage), fillWidth: true)
                                }
                                .buttonStyle(.bordered)

                                if let nextLesson {
                                    NavigationLink(destination: PracticeLessonView(lesson: nextLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(nextLesson.id))) {
                                        lessonActionLabel(erickText("Next Lesson", languageKey: keyboardLanguage), fillWidth: true)
                                    }
                                    .buttonStyle(.borderedProminent)
                                } else if let previousLesson {
                                    NavigationLink(destination: PracticeLessonView(lesson: previousLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(previousLesson.id))) {
                                        lessonActionLabel(erickText("Previous Lesson", languageKey: keyboardLanguage), fillWidth: true)
                                    }
                                    .buttonStyle(.bordered)
                                }
                            }

                            VStack(spacing: 12) {
                                Button(action: resetLessonProgress) {
                                    lessonActionLabel(erickText("Replay Lesson", languageKey: keyboardLanguage), fillWidth: true)
                                }
                                .buttonStyle(.bordered)

                                if let nextLesson {
                                    NavigationLink(destination: PracticeLessonView(lesson: nextLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(nextLesson.id))) {
                                        lessonActionLabel(erickText("Next Lesson", languageKey: keyboardLanguage), fillWidth: true)
                                    }
                                    .buttonStyle(.borderedProminent)
                                } else if let previousLesson {
                                    NavigationLink(destination: PracticeLessonView(lesson: previousLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(previousLesson.id))) {
                                        lessonActionLabel(erickText("Previous Lesson", languageKey: keyboardLanguage), fillWidth: true)
                                    }
                                    .buttonStyle(.bordered)
                                }
                            }
                        }
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color.green.opacity(0.12))
                    .cornerRadius(16)
                } else if previousLesson != nil || nextLesson != nil {
                    ViewThatFits(in: .horizontal) {
                        HStack(spacing: 12) {
                            if let previousLesson {
                                NavigationLink(destination: PracticeLessonView(lesson: previousLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(previousLesson.id))) {
                                    lessonActionLabel(erickText("Previous Lesson", languageKey: keyboardLanguage), fillWidth: true)
                                }
                                .buttonStyle(.bordered)
                            }
                            if let nextLesson {
                                NavigationLink(destination: PracticeLessonView(lesson: nextLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(nextLesson.id))) {
                                    lessonActionLabel(erickText("Next Lesson", languageKey: keyboardLanguage), fillWidth: true)
                                }
                                .buttonStyle(.borderedProminent)
                            }
                        }

                        VStack(spacing: 12) {
                            if let previousLesson {
                                NavigationLink(destination: PracticeLessonView(lesson: previousLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(previousLesson.id))) {
                                    lessonActionLabel(erickText("Previous Lesson", languageKey: keyboardLanguage), fillWidth: true)
                                }
                                .buttonStyle(.bordered)
                            }
                            if let nextLesson {
                                NavigationLink(destination: PracticeLessonView(lesson: nextLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(nextLesson.id))) {
                                    lessonActionLabel(erickText("Next Lesson", languageKey: keyboardLanguage), fillWidth: true)
                                }
                                .buttonStyle(.borderedProminent)
                            }
                        }
                    }
                }
            }
            .padding()
        }
        .navigationTitle(lesson.title)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                NavigationLink(destination: SettingsView()) {
                    Image(systemName: "gearshape")
                }
            }
            ToolbarItem(placement: .navigationBarTrailing) {
                Button {
                    showHelpSheet = true
                } label: {
                    Image(systemName: "questionmark.circle")
                }
            }
        }
        .sheet(isPresented: $showHelpSheet) {
            LessonHelpSheet(
                lesson: lesson,
                setupSummary: lesson.setup.map { formatLessonSetup($0, languageKey: keyboardLanguage) }
            )
        }
        .onAppear {
            markAttempted()
            applyLessonSetup()
            checkKeyboardStatus()
            if startFromBeginning {
                resetLessonProgress()
            }
        }
        .onChange(of: typedText) { newValue in
            guard !lesson.isFreeform, let targetText = currentExercise?.targetText else { return }
            if newValue.trimmingCharacters(in: .whitespacesAndNewlines).caseInsensitiveCompare(targetText) == .orderedSame {
                guard let exercise = currentExercise else { return }
                let updatedCompleted = completedExerciseIds.union([exercise.id])
                completedExerciseIds = updatedCompleted
                recentCompletionLabel = "\(erickText("Correct", languageKey: keyboardLanguage)): \(exercise.targetText)"
                if updatedCompleted.count == lesson.exercises.count {
                    markCompleted()
                } else {
                    currentExerciseIndex = nextIncompleteExerciseIndex(
                        currentIndex: currentExerciseIndex,
                        exercises: lesson.exercises,
                        completedExerciseIds: updatedCompleted
                    )
                }
                typedText = ""
            }
        }
        .task(id: recentCompletionLabel) {
            guard recentCompletionLabel != nil else { return }
            try? await Task.sleep(nanoseconds: 1_400_000_000)
            recentCompletionLabel = nil
        }
        .onChange(of: scenePhase) { newPhase in
            if newPhase == .active {
                checkKeyboardStatus()
            }
        }
    }

    private func markAttempted() {
        var updated = LearningProgressStore.decodeSet(attemptedLessonsRaw)
        updated.insert(lesson.id)
        attemptedLessonsRaw = LearningProgressStore.encodeSet(updated)
    }

    private func markCompleted() {
        var attempted = LearningProgressStore.decodeSet(attemptedLessonsRaw)
        attempted.insert(lesson.id)
        attemptedLessonsRaw = LearningProgressStore.encodeSet(attempted)

        var completed = LearningProgressStore.decodeSet(completedLessonsRaw)
        completed.insert(lesson.id)
        completedLessonsRaw = LearningProgressStore.encodeSet(completed)
    }

    private func applyLessonSetup() {
        guard let setup = lesson.setup else { return }
        sixSectionDial = setup.sixSectionDial
        layoutType = setup.layoutType
        inputMode = setup.inputMode
    }

    private func resetLessonProgress() {
        typedText = ""
        currentExerciseIndex = 0
        completedExerciseIds = []
        recentCompletionLabel = nil
    }

    private func checkKeyboardStatus() {
        if let keyboards = UserDefaults.standard.object(forKey: "AppleKeyboards") as? [String] {
            let actuallyEnabled = keyboards.contains { $0.localizedCaseInsensitiveContains("erick") }
            isKeyboardActuallyEnabled = actuallyEnabled
            hasEnabledKeyboard = actuallyEnabled
        }
    }

    private func lessonActionLabel(_ title: String, fillWidth: Bool) -> some View {
        Text(title)
            .font(.headline)
            .lineLimit(1)
            .minimumScaleFactor(0.85)
            .frame(maxWidth: fillWidth ? .infinity : nil)
            .padding(.vertical, 12)
    }

    private func formatLessonSetup(_ setup: PracticeLessonSetupData, languageKey: String) -> String {
        let dialLabel = setup.sixSectionDial ? erickText("6-section", languageKey: languageKey) : erickText("8-section", languageKey: languageKey)
        let layoutLabel: String
        switch setup.layoutType {
        case "efficiency":
            layoutLabel = erickText("Efficiency", languageKey: languageKey)
        case "custom":
            layoutLabel = erickText("Custom", languageKey: languageKey)
        default:
            layoutLabel = erickText("Logical", languageKey: languageKey)
        }

        let inputLabel: String
        switch setup.inputMode {
        case "confirm":
            inputLabel = erickText("Steady Type", languageKey: languageKey)
        case "assisted":
            inputLabel = erickText("One-Handed", languageKey: languageKey)
        default:
            inputLabel = erickText("Quick Type", languageKey: languageKey)
        }

        return "\(dialLabel) • \(layoutLabel) • \(inputLabel)"
    }
}

private struct LessonHelpSheet: View {
    @Environment(\.erickLanguageKey) private var keyboardLanguage
    let lesson: PracticeLessonData
    let setupSummary: String?

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    Text(lesson.focus)
                        .font(.body)
                    if let setupSummary {
                        Text("\(erickText("Setup", languageKey: keyboardLanguage)): \(setupSummary)")
                            .font(.subheadline)
                            .fontWeight(.semibold)
                    }
                    if !lesson.setupReason.isEmpty {
                        Text("\(erickText("Why this setup", languageKey: keyboardLanguage)): \(lesson.setupReason)")
                            .font(.subheadline)
                    }
                    ForEach(Array(lesson.instructions.enumerated()), id: \.offset) { _, instruction in
                        Text("• \(instruction)")
                            .font(.subheadline)
                    }
                    Text(lesson.successHint)
                        .font(.footnote)
                        .foregroundColor(.secondary)
                }
                .padding()
            }
            .navigationTitle(lesson.title)
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

private struct LessonAction: Identifiable {
    let id: String
    let title: String
    let prominent: Bool
    let action: () -> Void
}

private struct ActionButtonGroup: View {
    let actions: [LessonAction]

    var body: some View {
        ViewThatFits(in: .horizontal) {
            HStack(spacing: 12) {
                ForEach(actions) { action in
                    LessonActionButton(action: action, fillWidth: true)
                }
            }

            VStack(spacing: 12) {
                ForEach(actions) { action in
                    LessonActionButton(action: action, fillWidth: true)
                }
            }
        }
    }
}

private struct LessonActionButton: View {
    let action: LessonAction
    let fillWidth: Bool

    var body: some View {
        Button(action: action.action) {
            Text(action.title)
                .font(.headline)
                .lineLimit(1)
                .minimumScaleFactor(0.85)
                .frame(maxWidth: fillWidth ? .infinity : nil)
                .padding(.vertical, 12)
        }
        .buttonStyle(action.prominent ? .borderedProminent : .bordered)
    }
}

private func compactLessonSummary(_ lesson: PracticeLessonData, languageKey: String) -> String {
    if lesson.isFreeform {
        return erickText("Freeform quote practice", languageKey: languageKey)
    }

    guard let setup = lesson.setup else {
        return "\(lesson.exercises.count) \(erickText("parts", languageKey: languageKey))"
    }

    var parts: [String] = []
    if let recommendedStep = lesson.recommendedStep {
        parts.append("\(erickText("Step", languageKey: languageKey)) \(recommendedStep)")
    }
    parts.append("\(lesson.exercises.count) \(erickText("parts", languageKey: languageKey))")
    let dialLabel = setup.sixSectionDial ? erickText("6-section", languageKey: languageKey) : erickText("8-section", languageKey: languageKey)
    let inputLabel: String
    switch setup.inputMode {
    case "confirm":
        inputLabel = erickText("Steady Type", languageKey: languageKey)
    case "assisted":
        inputLabel = erickText("One-Handed", languageKey: languageKey)
    default:
        inputLabel = erickText("Quick Type", languageKey: languageKey)
    }

    parts.append(dialLabel)
    parts.append(inputLabel)
    return parts.joined(separator: " • ")
}

private func lessonHeaderLabel(lesson: PracticeLessonData, currentExerciseIndex: Int, languageKey: String) -> String {
    if lesson.isFreeform {
        return erickText("Advanced practice", languageKey: languageKey)
    }
    if let recommendedStep = lesson.recommendedStep {
        return "\(erickText("Recommended step", languageKey: languageKey)) \(recommendedStep)"
    }
    return "\(erickText("Part", languageKey: languageKey)) \(currentExerciseIndex + 1) \(erickText("of", languageKey: languageKey)) \(lesson.exercises.count)"
}

private struct PracticeSectionModel: Identifiable {
    let id: PracticeLessonSectionData
    let section: PracticeLessonSectionData
    let title: String
    let summary: String

    init(section: PracticeLessonSectionData, title: String, summary: String) {
        self.id = section
        self.section = section
        self.title = title
        self.summary = summary
    }
}

private func practiceSectionModels(for languageKey: String) -> [PracticeSectionModel] {
    [
        PracticeSectionModel(
            section: .startHere,
            title: erickText("Start Here", languageKey: languageKey),
            summary: erickText("The guided route keeps the dial mode simple first, then adds more surfaces one step at a time.", languageKey: languageKey)
        ),
        PracticeSectionModel(
            section: .followUp,
            title: erickText("Mode Follow-Ups", languageKey: languageKey),
            summary: erickText("Use these once the main route feels understandable and you want a specific typing path.", languageKey: languageKey)
        ),
        PracticeSectionModel(
            section: .advanced,
            title: erickText("Advanced Practice", languageKey: languageKey),
            summary: erickText("Open-ended practice for when the guided drills already feel easy.", languageKey: languageKey)
        )
    ]
}

private func keyboardLanguageSelfDisplayName(_ keyboardLanguage: String) -> String {
    switch keyboardLanguage {
    case "spanish":
        return "Espanol"
    case "portuguese":
        return "Portugues"
    case "french":
        return "Francais"
    case "german":
        return "Deutsch"
    case "italian":
        return "Italiano"
    case "norwegian_bokmal":
        return "Norsk Bokmal"
    case "danish":
        return "Dansk"
    case "swedish":
        return "Svenska"
    case "finnish":
        return "Suomi"
    default:
        return "English"
    }
}

private func nextIncompleteExerciseIndex(
    currentIndex: Int,
    exercises: [PracticeExerciseData],
    completedExerciseIds: Set<String>
) -> Int {
    if currentIndex < exercises.count - 1 {
        for index in (currentIndex + 1)..<exercises.count where !completedExerciseIds.contains(exercises[index].id) {
            return index
        }
    }

    for index in exercises.indices where !completedExerciseIds.contains(exercises[index].id) {
        return index
    }

    return currentIndex
}