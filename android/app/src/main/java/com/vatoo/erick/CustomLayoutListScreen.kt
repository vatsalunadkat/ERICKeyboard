package com.vatoo.erick

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.vatoo.erick.shared.CustomLayout
import com.vatoo.erick.shared.CustomLayoutManager
import com.vatoo.erick.shared.LayoutType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomLayoutListScreen(
    customLayoutManager: CustomLayoutManager,
    customLayouts: List<CustomLayout>,
    onLayoutsChanged: () -> Unit,
    onEditLayout: (CustomLayout) -> Unit,
    onBack: () -> Unit
) {
    val appLanguage = LocalAppLanguageKey.current
    fun t(english: String): String = erickText(appLanguage, english)
    var showCreateDialog by remember { mutableStateOf(false) }
    var showDuplicateDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<CustomLayout?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recoverableEnglishTitle(appLanguage, "Custom Layouts")) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("Back"))
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(t("Start here"), style = MaterialTheme.typography.titleMedium)
                        Text(
                            t("Create a blank layout if you want full control, or duplicate a built-in layout if you only want to adjust a few chords."),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { showCreateDialog = true }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(t("Create Blank"))
                            }
                            OutlinedButton(onClick = { showDuplicateDialog = true }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(t("Duplicate Built-in"))
                            }
                        }
                    }
                }
            }

            if (customLayouts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            t("No custom layouts yet."),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(customLayouts, key = { it.id }) { layout ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEditLayout(layout) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(layout.name, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "${layout.normalChordMap.values.flatten().count { it.isNotBlank() }} ${t("characters mapped")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { onEditLayout(layout) }) {
                                Icon(Icons.Default.Edit, contentDescription = t("Edit"))
                            }
                            IconButton(onClick = { deleteTarget = layout }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = t("Delete"),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(t("New Blank Layout")) },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 30) name = it },
                    label = { Text(t("Layout Name")) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val layout = customLayoutManager.createBlank(name)
                        customLayoutManager.save(layout)
                        onLayoutsChanged()
                        showCreateDialog = false
                        onEditLayout(layout)
                    },
                    enabled = name.isNotBlank()
                ) { Text(t("Create")) }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text(t("Cancel")) }
            }
        )
    }

    if (showDuplicateDialog) {
        var name by remember { mutableStateOf("") }
        var sourceLayout by remember { mutableStateOf(LayoutType.LOGICAL) }
        AlertDialog(
            onDismissRequest = { showDuplicateDialog = false },
            title = { Text(t("Duplicate Built-in Layout")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        t("Start from a built-in layout when you only want to tweak a few chords."),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.length <= 30) name = it },
                        label = { Text(t("New Layout Name")) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(t("Source:"), style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = sourceLayout == LayoutType.LOGICAL,
                            onClick = { sourceLayout = LayoutType.LOGICAL }
                        )
                        Text(t("Logical (A-Z)"))
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = sourceLayout == LayoutType.EFFICIENCY,
                            onClick = { sourceLayout = LayoutType.EFFICIENCY }
                        )
                        Text(t("Efficiency"))
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val layout = customLayoutManager.duplicateFromBuiltIn(sourceLayout, name)
                        customLayoutManager.save(layout)
                        onLayoutsChanged()
                        showDuplicateDialog = false
                        onEditLayout(layout)
                    },
                    enabled = name.isNotBlank()
                ) { Text(t("Duplicate")) }
            },
            dismissButton = {
                TextButton(onClick = { showDuplicateDialog = false }) { Text(t("Cancel")) }
            }
        )
    }

    deleteTarget?.let { layout ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(t("Delete Layout")) },
            text = { Text("${t("Delete")} \"${layout.name}\"? ${t("This cannot be undone.")}") },
            confirmButton = {
                TextButton(onClick = {
                    customLayoutManager.delete(layout.id)
                    onLayoutsChanged()
                    deleteTarget = null
                }) { Text(t("Delete"), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text(t("Cancel")) }
            }
        )
    }
}