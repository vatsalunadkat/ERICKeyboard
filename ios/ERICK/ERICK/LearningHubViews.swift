import SwiftUI

private let learningAppGroupDefaults = UserDefaults(suiteName: "group.com.vatoo.erick") ?? .standard

struct LearningHubCard: View {
    let quickstartCompleted: Bool
    let quickstartDismissed: Bool
    let quickstartStep: Int
    let onOpenQuickstart: () -> Void

    private var title: String {
        if quickstartCompleted {
            return "Replay Quickstart or Jump Into Practice"
        }
        if quickstartDismissed {
            return "Resume Your Quickstart"
        }
        return "Start the Guided Quickstart"
    }

    private var description: String {
        if quickstartCompleted {
            return "You already finished the quickstart. Replay it any time or continue into targeted drills."
        }
        if quickstartDismissed {
            let stepNumber = min(max(quickstartStep + 1, 1), erickQuickstartSteps.count)
            return "Resume from step \(stepNumber) of \(erickQuickstartSteps.count), or skip directly to practice drills."
        }
        return "New users should start here. The quickstart explains the dials, utility swipes, assisted mode, and controller typing."
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title)
                .font(.title3)
                .fontWeight(.bold)

            Text(description)
                .font(.body)
                .foregroundColor(.secondary)

            ViewThatFits(in: .horizontal) {
                HStack(spacing: 12) {
                    Button(action: onOpenQuickstart) {
                        adaptiveButtonLabel(
                            quickstartCompleted ? "Replay Quickstart" : (quickstartDismissed ? "Resume Quickstart" : "Start Quickstart"),
                            fillWidth: true
                        )
                    }
                    .buttonStyle(.borderedProminent)

                    NavigationLink(destination: PracticeHubView()) {
                        adaptiveButtonLabel("Practice Lessons", fillWidth: true)
                    }
                    .buttonStyle(.bordered)
                }

                VStack(spacing: 12) {
                    Button(action: onOpenQuickstart) {
                        adaptiveButtonLabel(
                            quickstartCompleted ? "Replay Quickstart" : (quickstartDismissed ? "Resume Quickstart" : "Start Quickstart"),
                            fillWidth: true
                        )
                    }
                    .buttonStyle(.borderedProminent)

                    NavigationLink(destination: PracticeHubView()) {
                        adaptiveButtonLabel("Practice Lessons", fillWidth: true)
                    }
                    .buttonStyle(.bordered)
                }
            }
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(red: 234/255, green: 221/255, blue: 255/255))
        .cornerRadius(16)
        .environment(\.colorScheme, .light)
    }
}

struct QuickstartView: View {
    @Binding var currentStep: Int
    let onComplete: () -> Void
    let onSkip: () -> Void

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

                    Text(step.details)
                        .font(.body)
                        .foregroundColor(.secondary)

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
                    Text("Learning Path")
                        .font(.title3)
                        .fontWeight(.bold)
                    Text("Start with 6-section basics, then utility swipes, assisted one-handed typing, controller drills, and finally quote practice.")
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
                    NavigationLink {
                        PracticeLessonView(lesson: lesson)
                    } label: {
                        VStack(alignment: .leading, spacing: 8) {
                            Text(lesson.title)
                                .font(.headline)
                                .foregroundColor(.primary)
                            Text(lesson.focus)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            if !lesson.isFreeform {
                                Text("\(lesson.exercises.count) guided drills covering letters, numbers, and symbols.")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            Text(statusText(for: lesson.id))
                                .font(.caption)
                                .foregroundColor(statusColor(for: lesson.id))
                        }
                        .padding()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(backgroundColor(for: lesson.id))
                        .cornerRadius(16)
                    }
                    .simultaneousGesture(TapGesture().onEnded {
                        markAttempted(lesson.id)
                    })
                }
            }
            .padding()
        }
        .navigationTitle("Practice Lessons")
        .navigationBarTitleDisplayMode(.inline)
    }

    private func statusText(for lessonId: String) -> String {
        if completedLessons.contains(lessonId) {
            return "Completed"
        }
        if attemptedLessons.contains(lessonId) {
            return "Attempted"
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
            return Color.green.opacity(0.12)
        }
        return Color(uiColor: .secondarySystemBackground)
    }

    private func markAttempted(_ lessonId: String) {
        var updated = attemptedLessons
        updated.insert(lessonId)
        attemptedLessonsRaw = LearningProgressStore.encodeSet(updated)
    }
}

struct PracticeLessonView: View {
    let lesson: PracticeLessonData

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
    @FocusState private var practiceFieldFocused: Bool

    private var isCompleted: Bool {
        LearningProgressStore.decodeSet(completedLessonsRaw).contains(lesson.id)
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

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                VStack(alignment: .leading, spacing: 8) {
                    Text(lesson.focus)
                        .font(.headline)
                    ForEach(Array(lesson.instructions.enumerated()), id: \.offset) { index, instruction in
                        Text("\(index + 1). \(instruction)")
                            .foregroundColor(.secondary)
                    }
                    Text(lesson.successHint)
                        .font(.footnote)
                        .foregroundColor(.secondary)
                }
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(uiColor: .secondarySystemBackground))
                .cornerRadius(16)

                VStack(alignment: .leading, spacing: 8) {
                    Text("Lesson Setup")
                        .font(.headline)
                    if let setup = lesson.setup {
                        Text("Recommended: \(formatLessonSetup(setup))")
                            .font(.subheadline)
                    }
                    Text(setupMatchesLesson ? "Current keyboard preset already matches this lesson." : "Current keyboard preset: \(formatLessonSetup(PracticeLessonSetupData(sixSectionDial: sixSectionDial, layoutType: layoutType, inputMode: inputMode)))")
                        .font(.footnote)
                        .foregroundColor(.secondary)
                    Text(keyboardStatusMessage)
                        .font(.footnote)
                        .foregroundColor(.secondary)
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
                        Text("Guided Drills")
                            .font(.headline)

                        ForEach(Array(lesson.exercises.enumerated()), id: \.element.id) { index, exercise in
                            Text("\(index + 1). \(exercise.title) - \(drillStatus(for: exercise, at: index))")
                                .foregroundColor(completedExerciseIds.contains(exercise.id) ? .green : .secondary)
                        }
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(uiColor: .secondarySystemBackground))
                    .cornerRadius(16)

                    VStack(alignment: .leading, spacing: 12) {
                        Text(isCompleted ? "Lesson Complete" : "Drill \(currentExerciseIndex + 1) of \(lesson.exercises.count)")
                            .font(.headline)

                        if let exercise = currentExercise {
                            Text(exercise.title)
                                .font(.title3)
                                .fontWeight(.bold)
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

                        if isCompleted {
                            Text("Lesson complete. You can replay the drills, reapply the preset, or go back to the hub.")
                                .foregroundColor(.green)
                        }

                        Button("Clear Drill") {
                            typedText = ""
                        }
                        .buttonStyle(.borderedProminent)
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(uiColor: .secondarySystemBackground))
                    .cornerRadius(16)
                }

                VStack(alignment: .leading, spacing: 12) {
                    Text("Lesson Actions")
                        .font(.headline)

                    ViewThatFits(in: .horizontal) {
                        HStack(spacing: 12) {
                            lessonKeyboardActionButton(fillWidth: true)
                            NavigationLink(destination: SettingsView()) {
                                lessonActionLabel("Lesson Settings", fillWidth: true)
                            }
                            .buttonStyle(.bordered)
                            Button(action: applyLessonSetup) {
                                lessonActionLabel("Reapply Setup", fillWidth: true)
                            }
                            .buttonStyle(.bordered)
                        }

                        VStack(spacing: 12) {
                            lessonKeyboardActionButton(fillWidth: true)
                            NavigationLink(destination: SettingsView()) {
                                lessonActionLabel("Lesson Settings", fillWidth: true)
                            }
                            .buttonStyle(.bordered)
                            Button(action: applyLessonSetup) {
                                lessonActionLabel("Reapply Setup", fillWidth: true)
                            }
                            .buttonStyle(.bordered)
                        }
                    }
                }
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(uiColor: .secondarySystemBackground))
                .cornerRadius(16)
            }
            .padding()
        }
        .navigationTitle(lesson.title)
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            markAttempted()
            applyLessonSetup()
            checkKeyboardStatus()
        }
        .onChange(of: typedText) { newValue in
            guard !lesson.isFreeform, let targetText = currentExercise?.targetText else { return }
            if newValue.trimmingCharacters(in: .whitespacesAndNewlines).caseInsensitiveCompare(targetText) == .orderedSame {
                if let exercise = currentExercise {
                    completedExerciseIds.insert(exercise.id)
                }
                typedText = ""
                if currentExerciseIndex >= lesson.exercises.count - 1 {
                    markCompleted()
                } else {
                    currentExerciseIndex += 1
                }
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

    private func checkKeyboardStatus() {
        if let keyboards = UserDefaults.standard.object(forKey: "AppleKeyboards") as? [String] {
            let actuallyEnabled = keyboards.contains { $0.localizedCaseInsensitiveContains("erick") }
            isKeyboardActuallyEnabled = actuallyEnabled
            hasEnabledKeyboard = actuallyEnabled
        }
    }

    private func drillStatus(for exercise: PracticeExerciseData, at index: Int) -> String {
        if completedExerciseIds.contains(exercise.id) {
            return "Done"
        }
        if index == currentExerciseIndex && !isCompleted {
            return "Current"
        }
        return "Next"
    }

    private var keyboardStatusMessage: String {
        if !isKeyboardEnabled {
            return "ERICK is not enabled in iOS Settings yet. Use the button below to open Settings and enable it first."
        }
        if lesson.isFreeform {
            return "The lesson preset is ready. Launch quote practice, then use the globe key in the typing field if another keyboard is active."
        }
        return "iOS does not let apps switch the active keyboard directly. Focus the practice field below, then tap the globe key if ERICK is not active."
    }

    @ViewBuilder
    private func lessonKeyboardActionButton(fillWidth: Bool) -> some View {
        Button(action: {
            if isKeyboardEnabled {
                if lesson.isFreeform {
                    applyLessonSetup()
                } else {
                    practiceFieldFocused = true
                }
            } else if let url = URL(string: UIApplication.openSettingsURLString) {
                UIApplication.shared.open(url)
            }
        }) {
            lessonActionLabel(
                isKeyboardEnabled ? (lesson.isFreeform ? "Preset Ready" : "Focus Practice Field") : "Open iOS Settings",
                fillWidth: fillWidth
            )
        }
        .buttonStyle(.borderedProminent)
        .disabled(isKeyboardEnabled && lesson.isFreeform)
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