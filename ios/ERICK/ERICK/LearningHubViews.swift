import SwiftUI

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

            HStack(spacing: 12) {
                Button(action: onOpenQuickstart) {
                    Text(quickstartCompleted ? "Replay Quickstart" : (quickstartDismissed ? "Resume Quickstart" : "Start Quickstart"))
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                }
                .buttonStyle(.borderedProminent)

                NavigationLink(destination: PracticeHubView()) {
                    Text("Practice Lessons")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                }
                .buttonStyle(.bordered)
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

                    HStack(spacing: 12) {
                        Button("Skip for Now", action: onSkip)
                            .buttonStyle(.bordered)

                        if boundedStep > 0 {
                            Button("Back") {
                                currentStep = max(0, boundedStep - 1)
                            }
                            .buttonStyle(.bordered)
                        }

                        Spacer()

                        Button(boundedStep == erickQuickstartSteps.count - 1 ? "Finish" : "Next") {
                            if boundedStep == erickQuickstartSteps.count - 1 {
                                onComplete()
                            } else {
                                currentStep = min(erickQuickstartSteps.count - 1, boundedStep + 1)
                            }
                        }
                        .buttonStyle(.borderedProminent)
                    }
                }
                .padding()
            }
            .navigationTitle("Quickstart")
            .navigationBarTitleDisplayMode(.inline)
        }
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
                        if lesson.id == quotePracticeLessonId {
                            TypingGameView()
                        } else {
                            PracticeLessonView(lesson: lesson)
                        }
                    } label: {
                        VStack(alignment: .leading, spacing: 8) {
                            Text(lesson.title)
                                .font(.headline)
                                .foregroundColor(.primary)
                            Text(lesson.focus)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
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

    @AppStorage(LearningProgressStore.attemptedLessonsKey) private var attemptedLessonsRaw = ""
    @AppStorage(LearningProgressStore.completedLessonsKey) private var completedLessonsRaw = ""
    @State private var typedText = ""

    private var isCompleted: Bool {
        LearningProgressStore.decodeSet(completedLessonsRaw).contains(lesson.id)
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

                if let targetText = lesson.targetText {
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Target")
                            .font(.headline)
                        Text(targetText)
                            .font(.largeTitle)
                            .fontWeight(.bold)

                        TextField("Type the target here", text: $typedText)
                            .textFieldStyle(.roundedBorder)
                            .textInputAutocapitalization(.never)
                            .autocorrectionDisabled()

                        if isCompleted {
                            Text("Lesson complete. You can replay it or go back to the hub.")
                                .foregroundColor(.green)
                        }

                        Button("Clear Target") {
                            typedText = ""
                        }
                        .buttonStyle(.borderedProminent)
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(uiColor: .secondarySystemBackground))
                    .cornerRadius(16)
                }
            }
            .padding()
        }
        .navigationTitle(lesson.title)
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            markAttempted()
        }
        .onChange(of: typedText) { newValue in
            guard let targetText = lesson.targetText else { return }
            if newValue.trimmingCharacters(in: .whitespacesAndNewlines).caseInsensitiveCompare(targetText) == .orderedSame {
                markCompleted()
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
}