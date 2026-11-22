package com.justtobesafe.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.data.HouseholdPlan
import com.justtobesafe.app.data.PlanContact
import com.justtobesafe.app.ui.components.SafetyNotice
import com.justtobesafe.app.ui.components.SectionTitle
import com.justtobesafe.app.ui.components.SmallBadge
import com.justtobesafe.app.ui.components.SoftCard
import com.justtobesafe.app.ui.components.calmMotionDuration

@Composable
fun PlanScreen(plan: HouseholdPlan, loaded: Boolean, error: String?,
    onSave: (HouseholdPlan) -> Unit) {
    var addingContact by rememberSaveable { mutableStateOf(false) }
    var editingMeeting by rememberSaveable { mutableStateOf(false) }
    var removeContactIndex by remember { mutableStateOf<Int?>(null) }
    val colors = MaterialTheme.colorScheme
    val complete = (if (plan.contacts.isNotEmpty()) 1 else 0) +
        (if (plan.meetingPoint.isNotBlank()) 1 else 0)
    val progress by animateFloatAsState(
        targetValue = complete / 2f, animationSpec = tween(calmMotionDuration(240)),
        label = "Household plan setup progress"
    )

    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item(key = "plan-title") {
            SectionTitle("Household plan", "Keep the people and places that matter close to hand")
        }
        item(key = "privacy") {
            SmallBadge("PRIVATE · ENCRYPTED ON THIS DEVICE")
        }
        if (!loaded) item(key = "loading") {
            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        if (error != null) item(key = "plan-error") { SafetyNotice(error) }
        if (loaded && error == null) {
            item(key = "plan-setup") {
                SoftCard {
                    Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text("Plan setup", style = MaterialTheme.typography.titleMedium)
                                Text("$complete of 2 basics added",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant)
                            }
                            Text("${complete * 50}%", style = MaterialTheme.typography.headlineMedium,
                                color = colors.primary)
                        }
                        LinearProgressIndicator(progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = colors.primary, trackColor = colors.surfaceVariant)
                        Text("Set a meeting point and add at least one contact. This is a setup indicator, not a measure of safety.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant)
                    }
                }
            }
            item(key = "meeting") {
                SoftCard {
                    Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                            Box(Modifier.size(44.dp).background(colors.primaryContainer,
                                androidx.compose.foundation.shape.RoundedCornerShape(13.dp)),
                                contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Place, contentDescription = null,
                                    tint = colors.primary)
                            }
                            Column(Modifier.weight(1f)) {
                                Text("Meeting point", style = MaterialTheme.typography.titleMedium)
                                Text("Somewhere familiar to your household",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant)
                            }
                        }
                        Text(if (plan.meetingPoint.isBlank()) "No meeting point added yet"
                             else plan.meetingPoint,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (plan.meetingPoint.isBlank()) colors.onSurfaceVariant
                                else colors.onSurface)
                        OutlinedButton(onClick = { editingMeeting = true },
                            modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Outlined.Edit, contentDescription = null,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(if (plan.meetingPoint.isBlank()) "Add meeting point" else "Edit meeting point")
                        }
                    }
                }
            }
            item(key = "people") {
                SoftCard {
                    Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                            Box(Modifier.size(44.dp).background(colors.primaryContainer,
                                androidx.compose.foundation.shape.RoundedCornerShape(13.dp)),
                                contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.PeopleOutline, contentDescription = null,
                                    tint = colors.primary)
                            }
                            Column(Modifier.weight(1f)) {
                                Text("Household contacts", style = MaterialTheme.typography.titleMedium)
                                Text("People you might need to reach",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant)
                            }
                        }
                        if (plan.contacts.isEmpty()) {
                            Text("No contacts saved yet. Add a name and number to begin.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant)
                        } else {
                            plan.contacts.forEachIndexed { index, contact ->
                                if (index > 0) HorizontalDivider(color = colors.outlineVariant)
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(Modifier.size(42.dp).background(colors.secondaryContainer,
                                        CircleShape), contentAlignment = Alignment.Center) {
                                        Text(contact.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = colors.onSecondaryContainer)
                                    }
                                    Column(Modifier.weight(1f)) {
                                        Text(contact.name, style = MaterialTheme.typography.titleSmall)
                                        Text(contact.phone, style = MaterialTheme.typography.bodyMedium,
                                            color = colors.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { removeContactIndex = index }) {
                                        Icon(Icons.Outlined.DeleteOutline,
                                            contentDescription = "Remove ${contact.name}")
                                    }
                                }
                            }
                        }
                        OutlinedButton(onClick = { addingContact = true },
                            enabled = plan.contacts.size < 12,
                            modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Outlined.Add, contentDescription = null,
                                modifier = Modifier.size(19.dp))
                            Spacer(Modifier.width(7.dp))
                            Text("Add contact")
                        }
                        if (plan.contacts.size >= 12) Text("Maximum of 12 contacts in this prototype.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant)
                    }
                }
            }
            item(key = "secure") {
                SoftCard {
                    Row(Modifier.padding(17.dp), verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Outlined.Lock, contentDescription = null,
                            tint = colors.primary)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text("Private by design", style = MaterialTheme.typography.titleMedium)
                            Text("Your contacts and meeting point are encrypted using Android Keystore and aren't uploaded. Uninstalling the app removes local records.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        item(key = "plan-notice") {
            SafetyNotice("Do not rely on this early prototype as your only record of critical contacts. Keep a separate copy that your household can access.")
        }
        item { Spacer(Modifier.height(14.dp)) }
    }

    if (addingContact && error == null) {
        var name by rememberSaveable { mutableStateOf("") }
        var phone by rememberSaveable { mutableStateOf("") }
        val valid = name.trim().isNotEmpty() && name.length <= 48 &&
            phone.trim().matches(Regex("[+0-9() \\-]{6,26}"))
        AlertDialog(onDismissRequest = { addingContact = false },
            title = { Text("Add household contact") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it.take(48) },
                        label = { Text("Name") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone, onValueChange = { phone = it.take(26) },
                        label = { Text("Phone number") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth())
                    Text("Encrypted on this device. No contacts permission required.",
                        style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = { TextButton(enabled = valid, onClick = {
                onSave(plan.copy(contacts = plan.contacts + PlanContact(name.trim(), phone.trim())))
                name = ""; phone = ""; addingContact = false
            }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { addingContact = false }) { Text("Cancel") } }
        )
    }

    if (editingMeeting && error == null) {
        var value by rememberSaveable(plan.meetingPoint) { mutableStateOf(plan.meetingPoint) }
        AlertDialog(onDismissRequest = { editingMeeting = false },
            title = { Text("Meeting point") },
            text = {
                OutlinedTextField(value = value, onValueChange = { value = it.take(180) },
                    label = { Text("Description or familiar location") }, maxLines = 3,
                    modifier = Modifier.fillMaxWidth())
            },
            confirmButton = { TextButton(onClick = {
                onSave(plan.copy(meetingPoint = value.trim()))
                editingMeeting = false
            }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { editingMeeting = false }) { Text("Cancel") } }
        )
    }

    if (removeContactIndex != null && error == null) {
        val index = removeContactIndex!!
        AlertDialog(onDismissRequest = { removeContactIndex = null },
            title = { Text("Remove this contact?") },
            text = { Text("The contact will be removed from the encrypted plan on this device.") },
            confirmButton = { TextButton(onClick = {
                if (index in plan.contacts.indices) {
                    onSave(plan.copy(contacts = plan.contacts.filterIndexed { i, _ -> i != index }))
                }
                removeContactIndex = null
            }) { Text("Remove") } },
            dismissButton = { TextButton(onClick = { removeContactIndex = null }) { Text("Cancel") } }
        )
    }
}
