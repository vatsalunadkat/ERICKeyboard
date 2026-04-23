package com.vatoo.erick

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class BenefitExample(
    val title: String,
    val description: String
)

data class BenefitAudienceGroup(
    val title: String,
    val subtitle: String,
    val intro: String,
    val examples: List<BenefitExample>
)

val benefitAudienceGroups = listOf(
    BenefitAudienceGroup(
        title = "Physical Disability Support",
        subtitle = "Motor, pain, fatigue, or one-handed access needs",
        intro = "ERICK can reduce precision tapping and uneven reach when standard phone keyboards feel physically demanding.",
        examples = listOf(
            BenefitExample(
                title = "Motor and dexterity",
                description = "Two large directional targets, 6-section mode, and controller input can be easier to control than tiny keys."
            ),
            BenefitExample(
                title = "One-handed use",
                description = "Assisted mode can lock the row so one side can finish the chord, which helps during temporary injury or permanent single-hand use."
            ),
            BenefitExample(
                title = "Pain and fatigue",
                description = "Broad, repeatable motions can feel less demanding than scattered phone-key reaches for some people."
            )
        )
    ),
    BenefitAudienceGroup(
        title = "Cognitive and Reading Support",
        subtitle = "Dyslexia, cognitive fatigue, memory load, or visual clutter sensitivity",
        intro = "ERICK can present typing in a more structured way when too much keyboard clutter or too much memorization gets in the way.",
        examples = listOf(
            BenefitExample(
                title = "Step-by-step learning",
                description = "Quickstart and practice lessons teach one concept at a time instead of forcing memorization all at once."
            ),
            BenefitExample(
                title = "Dyslexia-friendly reading",
                description = "Logical layouts, live previews, and the OpenDyslexic option can make letters easier to track."
            ),
            BenefitExample(
                title = "Visual separation",
                description = "Colorblind-safe palettes and the optional 6-section mode can make the dial easier to scan and distinguish."
            )
        )
    ),
    BenefitAudienceGroup(
        title = "Everyday and General Use",
        subtitle = "Useful for non-disabled users too",
        intro = "ERICK is accessibility-first, but some everyday users still prefer it for comfort, controller typing, and privacy.",
        examples = listOf(
            BenefitExample(
                title = "Controller and TV typing",
                description = "The same two-dial model can feel easier than stepping around a TV grid keyboard with a remote or D-pad."
            ),
            BenefitExample(
                title = "Small-screen comfort",
                description = "Larger targets can feel calmer and more reliable on compact phones or while traveling."
            ),
            BenefitExample(
                title = "Privacy-focused typing",
                description = "Predictions stay on-device, and the keyboard avoids cloud-style typing collection."
            )
        )
    )
)

@Composable
fun BenefitsOverviewSection(
    modifier: Modifier = Modifier,
    title: String = "Who ERICK Can Help",
    summary: String = "Examples across physical access, cognitive support, and everyday use.",
    initiallyExpanded: Boolean = false
) {
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse $title" else "Expand $title"
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                BenefitsOverviewContent()
            }
        }
    }
}

@Composable
fun BenefitsOverviewContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "These are example situations, not promises or testimonials.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        benefitAudienceGroups.forEach { group ->
            BenefitAudienceGroupCard(group)
        }
    }
}

@Composable
private fun BenefitAudienceGroupCard(group: BenefitAudienceGroup) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(group.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(
                text = group.subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = group.intro,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            group.examples.forEach { example ->
                BenefitExampleCard(example)
            }
        }
    }
}

@Composable
private fun BenefitExampleCard(example: BenefitExample) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(example.title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(
                text = example.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}