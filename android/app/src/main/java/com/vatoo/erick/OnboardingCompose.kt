package com.vatoo.erick

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp

@Composable
fun QuickstartDialog(
    step: QuickstartStep,
    stepIndex: Int,
    totalSteps: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onFinish: () -> Unit,
    onDismiss: () -> Unit
) {
    var showDetails by rememberSaveable(step.id) { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), tonalElevation = 6.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Quickstart ${stepIndex + 1} of $totalSteps",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(step.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(step.summary, style = MaterialTheme.typography.titleMedium)

                TextButton(onClick = { showDetails = !showDetails }) {
                    ActionButtonText(if (showDetails) "Hide Detail" else "More Detail")
                }

                if (showDetails) {
                    Text(step.details, style = MaterialTheme.typography.bodyMedium)
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = step.tryNext,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val stackActions = maxWidth < 360.dp
                    if (stackActions) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
                                ActionButtonText("Skip")
                            }
                            if (stepIndex > 0) {
                                OutlinedButton(onClick = onPrevious, modifier = Modifier.fillMaxWidth()) {
                                    ActionButtonText("Back")
                                }
                            }
                            Button(
                                onClick = if (stepIndex == totalSteps - 1) onFinish else onNext,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ActionButtonText(if (stepIndex == totalSteps - 1) "Finish" else "Next")
                            }
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = onSkip, modifier = Modifier.weight(1f)) {
                                ActionButtonText("Skip")
                            }
                            if (stepIndex > 0) {
                                OutlinedButton(onClick = onPrevious, modifier = Modifier.weight(1f)) {
                                    ActionButtonText("Back")
                                }
                            }
                            Button(
                                onClick = if (stepIndex == totalSteps - 1) onFinish else onNext,
                                modifier = Modifier.weight(1f)
                            ) {
                                ActionButtonText(if (stepIndex == totalSteps - 1) "Finish" else "Next")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtonText(text: String) {
    Text(
        text = text,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis
    )
}