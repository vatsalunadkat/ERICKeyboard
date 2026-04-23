import SwiftUI

struct BenefitExample: Identifiable {
    let id: String
    let title: String
    let description: String
}

struct BenefitAudienceGroup: Identifiable {
    let id: String
    let title: String
    let subtitle: String
    let intro: String
    let examples: [BenefitExample]
}

let benefitAudienceGroups: [BenefitAudienceGroup] = [
    BenefitAudienceGroup(
        id: "physical-support",
        title: "Physical Disability Support",
        subtitle: "Motor, pain, fatigue, or one-handed access needs",
        intro: "ERICK can reduce precision tapping and uneven reach when standard phone keyboards feel physically demanding.",
        examples: [
            BenefitExample(
                id: "motor-dexterity",
                title: "Motor and dexterity",
                description: "Two large directional targets, 6-section mode, and controller input can be easier to control than tiny keys."
            ),
            BenefitExample(
                id: "one-handed-use",
                title: "One-handed use",
                description: "Assisted mode can lock the row so one side can finish the chord, which helps during temporary injury or permanent single-hand use."
            ),
            BenefitExample(
                id: "pain-fatigue",
                title: "Pain and fatigue",
                description: "Broad, repeatable motions can feel less demanding than scattered phone-key reaches for some people."
            )
        ]
    ),
    BenefitAudienceGroup(
        id: "cognitive-support",
        title: "Cognitive and Reading Support",
        subtitle: "Dyslexia, cognitive fatigue, memory load, or visual clutter sensitivity",
        intro: "ERICK can present typing in a more structured way when too much keyboard clutter or too much memorization gets in the way.",
        examples: [
            BenefitExample(
                id: "step-by-step-learning",
                title: "Step-by-step learning",
                description: "Quickstart and practice lessons teach one concept at a time instead of forcing memorization all at once."
            ),
            BenefitExample(
                id: "dyslexia-friendly-reading",
                title: "Dyslexia-friendly reading",
                description: "Logical layouts, live previews, and the OpenDyslexic option can make letters easier to track."
            ),
            BenefitExample(
                id: "visual-separation",
                title: "Visual separation",
                description: "Colorblind-safe palettes and the optional 6-section mode can make the dial easier to scan and distinguish."
            )
        ]
    ),
    BenefitAudienceGroup(
        id: "everyday-use",
        title: "Everyday and General Use",
        subtitle: "Useful for non-disabled users too",
        intro: "ERICK is accessibility-first, but some everyday users still prefer it for comfort, controller typing, and privacy.",
        examples: [
            BenefitExample(
                id: "controller-tv-typing",
                title: "Controller and TV typing",
                description: "The same two-dial model can feel easier than stepping around a TV grid keyboard with a remote or D-pad."
            ),
            BenefitExample(
                id: "small-screen-comfort",
                title: "Small-screen comfort",
                description: "Larger targets can feel calmer and more reliable on compact phones or while traveling."
            ),
            BenefitExample(
                id: "privacy-focused-typing",
                title: "Privacy-focused typing",
                description: "Predictions stay on-device, and the keyboard avoids cloud-style typing collection."
            )
        ]
    )
]

struct BenefitsOverviewSection: View {
    var title: String = "Who ERICK Can Help"
    var summary: String = "Examples across physical access, cognitive support, and everyday use."
    var initiallyExpanded: Bool = false

    @State private var isExpanded: Bool = false

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Button {
                isExpanded.toggle()
            } label: {
                HStack(alignment: .top, spacing: 12) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(title)
                            .font(.headline)
                            .foregroundColor(.primary)
                        Text(summary)
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.leading)
                    }
                    Spacer(minLength: 0)
                    Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                        .foregroundColor(.secondary)
                        .padding(.top, 2)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .buttonStyle(.plain)

            if isExpanded {
                BenefitsOverviewContent()
            }
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(uiColor: .secondarySystemBackground))
        .cornerRadius(16)
        .onAppear {
            isExpanded = initiallyExpanded
        }
    }
}

struct BenefitsOverviewContent: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text("These are example situations, not promises or testimonials.")
                .font(.footnote)
                .foregroundColor(.secondary)

            ForEach(benefitAudienceGroups) { group in
                BenefitAudienceGroupCard(group: group)
            }
        }
    }
}

private struct BenefitAudienceGroupCard: View {
    let group: BenefitAudienceGroup

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text(group.title)
                .font(.headline)
                .foregroundColor(.primary)
            Text(group.subtitle)
                .font(.subheadline)
                .foregroundColor(.accentColor)
            Text(group.intro)
                .font(.footnote)
                .foregroundColor(.secondary)

            ForEach(group.examples) { example in
                BenefitExampleCard(example: example)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(14)
        .background(Color(uiColor: .systemBackground))
        .cornerRadius(12)
    }
}

private struct BenefitExampleCard: View {
    let example: BenefitExample

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(example.title)
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundColor(.primary)
            Text(example.description)
                .font(.footnote)
                .foregroundColor(.secondary)
                .fixedSize(horizontal: false, vertical: true)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(12)
        .background(Color(uiColor: .secondarySystemBackground))
        .cornerRadius(10)
    }
}