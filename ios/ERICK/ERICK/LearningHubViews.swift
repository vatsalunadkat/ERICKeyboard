import SwiftUI

struct QuickstartView: View {
    @Binding var currentStep: Int
    let onComplete: () -> Void
    let onSkip: () -> Void
    @State private var showDetails = false

    private var boundedStep: Int {
        min(max(currentStep, 0), erickQuickstartSteps.count - 1)
    }

    private var step: QuickstartStepData {
        erickQuickstartSteps[boundedStep]
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("Quickstart \(boundedStep + 1) of \(erickQuickstartSteps.count)")
                        .font(.caption)
                        .fontWeight(.semibold)
                        .foregroundColor(.accentColor)

                    Text(step.title)
                        .font(.title2)
                        .fontWeight(.bold)

                    Text(step.summary)
                        .font(.title3)

                    DisclosureGroup(showDetails ? "Hide detail" : "More detail", isExpanded: $showDetails) {
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
                                adaptiveButtonLabel("Skip", fillWidth: true)
                            }
                            .buttonStyle(.bordered)

                            if boundedStep > 0 {
                                Button {
                                    currentStep = max(0, boundedStep - 1)
                                } label: {
                                    adaptiveButtonLabel("Back", fillWidth: true)
                                }
                                .buttonStyle(.bordered)
                            }

                            Button {
                                if boundedStep == erickQuickstartSteps.count - 1 {
                                    onComplete()
                                } else {
                                    currentStep = min(erickQuickstartSteps.count - 1, boundedStep + 1)
                                }
                            } label: {
                                adaptiveButtonLabel(boundedStep == erickQuickstartSteps.count - 1 ? "Finish" : "Next", fillWidth: true)
                            }
                            .buttonStyle(.borderedProminent)
                        }

                        VStack(spacing: 12) {
                            Button(action: onSkip) {
                                adaptiveButtonLabel("Skip", fillWidth: true)
                            }
                            .buttonStyle(.bordered)

                            if boundedStep > 0 {
                                Button {
                                    currentStep = max(0, boundedStep - 1)
                                } label: {
                                    adaptiveButtonLabel("Back", fillWidth: true)
                                }
                                .buttonStyle(.bordered)
                            }

                            Button {
                                if boundedStep == erickQuickstartSteps.count - 1 {
                                    onComplete()
                                } else {
                                    currentStep = min(erickQuickstartSteps.count - 1, boundedStep + 1)
                                }
                            } label: {
                                adaptiveButtonLabel(boundedStep == erickQuickstartSteps.count - 1 ? "Finish" : "Next", fillWidth: true)
                            }
                            .buttonStyle(.borderedProminent)
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("Quickstart")
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
    @State private var infoLesson: PracticeLessonData?

    private var attemptedLessons: Set<String> {
        LearningProgressStore.decodeSet(attemptedLessonsRaw)
    }

    private var completedLessons: Set<String> {
        LearningProgressStore.decodeSet(completedLessonsRaw)
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                VStack(alignment: .leading, spacing: 8) {
                    Text("Pick a lesson")
                        .font(.title3)
                        .fontWeight(.bold)
                    Text("ERICK applies the lesson setup for you so you can focus on the drill.")
                        .foregroundColor(.secondary)
                    Text("Progress: \(completedLessons.count) completed / \(attemptedLessons.count) attempted")
                        .font(.footnote)
                        .foregroundColor(.secondary)
                }
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(uiColor: .secondarySystemBackground))
                .cornerRadius(16)

                ForEach(erickPracticeLessons) { lesson in
                    VStack(alignment: .leading, spacing: 10) {
                        HStack(alignment: .top, spacing: 12) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(lesson.title)
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                Text(compactLessonSummary(lesson))
                                    .font(.caption)
                                    .foregroundColor(.secondary)
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
                            Text(statusText(for: lesson.id))
                                .font(.caption)
                                .foregroundColor(statusColor(for: lesson.id))
                        }

                        NavigationLink {
                            PracticeLessonView(
                                lesson: lesson,
                                startFromBeginning: completedLessons.contains(lesson.id)
                            )
                        } label: {
                            Text(primaryActionLabel(for: lesson.id))
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
            .padding()
        }
        .navigationTitle("Practice Lessons")
        .navigationBarTitleDisplayMode(.inline)
        .sheet(item: $infoLesson) { lesson in
            LessonHelpSheet(
                lesson: lesson,
                setupSummary: lesson.setup.map(formatLessonSetup)
            )
        }
    }

    private func statusText(for lessonId: String) -> String {
        if completedLessons.contains(lessonId) {
            return "Completed"
        }
        if attemptedLessons.contains(lessonId) {
            return "In progress"
        }
        return "Not started"
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

    private func primaryActionLabel(for lessonId: String) -> String {
        if completedLessons.contains(lessonId) {
            return "Replay Lesson"
        }
        if attemptedLessons.contains(lessonId) {
            return "Continue Lesson"
        }
        return "Start Lesson"
    }
}

struct PracticeLessonView: View {
    let lesson: PracticeLessonData
    let startFromBeginning: Bool

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

    private var previousLesson: PracticeLessonData? {
        let currentIndex = erickPracticeLessons.firstIndex { $0.id == lesson.id } ?? 0
        guard currentIndex > 0 else { return nil }
        return erickPracticeLessons[currentIndex - 1]
    }

    private var nextLesson: PracticeLessonData? {
        let currentIndex = erickPracticeLessons.firstIndex { $0.id == lesson.id } ?? 0
        guard currentIndex < erickPracticeLessons.count - 1 else { return nil }
        return erickPracticeLessons[currentIndex + 1]
    }

    private var contextActions: [LessonAction] {
        var actions: [LessonAction] = []

        if !isKeyboardEnabled {
            actions.append(
                LessonAction(id: "enable-keyboard", title: "Open iOS Settings", prominent: true) {
                    if let url = URL(string: UIApplication.openSettingsURLString) {
                        UIApplication.shared.open(url)
                    }
                }
            )
        } else if !lesson.isFreeform && !practiceFieldFocused {
            actions.append(
                LessonAction(id: "focus-field", title: "Focus Typing Field", prominent: true) {
                    practiceFieldFocused = true
                }
            )
        }

        if !setupMatchesLesson {
            actions.append(
                LessonAction(id: "apply-setup", title: "Apply Setup", prominent: false) {
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
                LessonAction(id: "previous-part", title: "Previous Part", prominent: false) {
                    currentExerciseIndex -= 1
                    typedText = ""
                }
            )
        }
        if currentExerciseIndex < lesson.exercises.count - 1 {
            actions.append(
                LessonAction(id: "next-part", title: "Next Part", prominent: true) {
                    currentExerciseIndex += 1
                    typedText = ""
                }
            )
        }
        return actions
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                VStack(alignment: .leading, spacing: 8) {
                    Text(lesson.isFreeform ? "Advanced practice" : "Part \(currentExerciseIndex + 1) of \(lesson.exercises.count)")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundColor(.accentColor)
                    Text(currentExercise?.title ?? lesson.focus)
                        .font(.title3)
                        .fontWeight(.bold)
                    Text(lesson.isFreeform ? "Freeform practice" : "\(completedExerciseIds.count) of \(lesson.exercises.count) parts done")
                        .font(.footnote)
                        .foregroundColor(.secondary)
                    if let setup = lesson.setup {
                        Text(formatLessonSetup(setup))
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
                        Text("Advanced Freeform Mode")
                            .font(.headline)
                        Text("Launch the quote practice screen when you want a longer freeform session with the current lesson preset.")
                            .foregroundColor(.secondary)

                        NavigationLink {
                            TypingGameView()
                        } label: {
                            Text("Launch Quote Practice")
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

                        TextField("Type the drill target here", text: $typedText)
                            .textFieldStyle(.roundedBorder)
                            .textInputAutocapitalization(.never)
                            .autocorrectionDisabled()
                            .focused($practiceFieldFocused)
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
                        Text("Lesson complete")
                            .font(.headline)
                            .foregroundColor(.green)
                        ViewThatFits(in: .horizontal) {
                            HStack(spacing: 12) {
                                Button(action: resetLessonProgress) {
                                    lessonActionLabel("Replay Lesson", fillWidth: true)
                                }
                                .buttonStyle(.bordered)

                                if let nextLesson {
                                    NavigationLink(destination: PracticeLessonView(lesson: nextLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(nextLesson.id))) {
                                        lessonActionLabel("Next Lesson", fillWidth: true)
                                    }
                                    .buttonStyle(.borderedProminent)
                                } else if let previousLesson {
                                    NavigationLink(destination: PracticeLessonView(lesson: previousLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(previousLesson.id))) {
                                        lessonActionLabel("Previous Lesson", fillWidth: true)
                                    }
                                    .buttonStyle(.bordered)
                                }
                            }

                            VStack(spacing: 12) {
                                Button(action: resetLessonProgress) {
                                    lessonActionLabel("Replay Lesson", fillWidth: true)
                                }
                                .buttonStyle(.bordered)

                                if let nextLesson {
                                    NavigationLink(destination: PracticeLessonView(lesson: nextLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(nextLesson.id))) {
                                        lessonActionLabel("Next Lesson", fillWidth: true)
                                    }
                                    .buttonStyle(.borderedProminent)
                                } else if let previousLesson {
                                    NavigationLink(destination: PracticeLessonView(lesson: previousLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(previousLesson.id))) {
                                        lessonActionLabel("Previous Lesson", fillWidth: true)
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
                                    lessonActionLabel("Previous Lesson", fillWidth: true)
                                }
                                .buttonStyle(.bordered)
                            }
                            if let nextLesson {
                                NavigationLink(destination: PracticeLessonView(lesson: nextLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(nextLesson.id))) {
                                    lessonActionLabel("Next Lesson", fillWidth: true)
                                }
                                .buttonStyle(.borderedProminent)
                            }
                        }

                        VStack(spacing: 12) {
                            if let previousLesson {
                                NavigationLink(destination: PracticeLessonView(lesson: previousLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(previousLesson.id))) {
                                    lessonActionLabel("Previous Lesson", fillWidth: true)
                                }
                                .buttonStyle(.bordered)
                            }
                            if let nextLesson {
                                NavigationLink(destination: PracticeLessonView(lesson: nextLesson, startFromBeginning: LearningProgressStore.decodeSet(completedLessonsRaw).contains(nextLesson.id))) {
                                    lessonActionLabel("Next Lesson", fillWidth: true)
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
                setupSummary: lesson.setup.map(formatLessonSetup)
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

    private func formatLessonSetup(_ setup: PracticeLessonSetupData) -> String {
        let dialLabel = setup.sixSectionDial ? "6-section" : "8-section"
        let layoutLabel: String
        switch setup.layoutType {
        case "efficiency":
            layoutLabel = "Efficiency"
        case "custom":
            layoutLabel = "Custom"
        default:
            layoutLabel = "Logical"
        }

        let inputLabel: String
        switch setup.inputMode {
        case "confirm":
            inputLabel = "Steady Type"
        case "assisted":
            inputLabel = "One-Handed"
        default:
            inputLabel = "Quick Type"
        }

        return "\(dialLabel) • \(layoutLabel) • \(inputLabel)"
    }
}

private struct LessonHelpSheet: View {
    let lesson: PracticeLessonData
    let setupSummary: String?

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    Text(lesson.focus)
                        .font(.body)
                    if let setupSummary {
                        Text("Setup: \(setupSummary)")
                            .font(.subheadline)
                            .fontWeight(.semibold)
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

private func compactLessonSummary(_ lesson: PracticeLessonData) -> String {
    if lesson.isFreeform {
        return "Freeform quote practice"
    }

    guard let setup = lesson.setup else {
        return "\(lesson.exercises.count) parts"
    }

    let dialLabel = setup.sixSectionDial ? "6-section" : "8-section"
    let inputLabel: String
    switch setup.inputMode {
    case "confirm":
        inputLabel = "Steady Type"
    case "assisted":
        inputLabel = "One-Handed"
    default:
        inputLabel = "Quick Type"
    }

    return "\(lesson.exercises.count) parts • \(dialLabel) • \(inputLabel)"
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