package com.justtobesafe.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.data.HouseholdPlan
import com.justtobesafe.app.data.PlanContact
import com.justtobesafe.app.ui.components.SafetyNotice
import com.justtobesafe.app.ui.components.SectionTitle
import com.justtobesafe.app.ui.components.SmallBadge
import com.justtobesafe.app.ui.components.SoftCard

@Composable
fun PlanScreen(plan: HouseholdPlan, loaded: Boolean, error: String?,
    onSave: (HouseholdPlan) -> Unit) {
    var addContact by rememberSaveable { mutableStateOf(false) }
    var editMeeting by rememberSaveable { mutableStateOf(false) }
    var deleteIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { SectionTitle("Your household plan", "Important details, kept privately on this device") }
        item { SmallBadge("ENCRYPTED ON DEVICE · NO ACCOUNT") }
        if (!loaded) item { CircularProgressIndicator() }
        if (error != null) item { SafetyNotice(error) }
        if (loaded && error == null) {
            item {
                SoftCard {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            Icon(Icons.Outlined.Place, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary)
                            Text("Meeting point", style = MaterialTheme.typography.titleLarge)
                        }
                        Text(if (plan.meetingPoint.isBlank()) "Not yet chosen" else plan.meetingPoint,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedButton(onClick = { editMeeting = true }) {
                            Icon(Icons.Outlined.Edit, contentDescription = null)
                            Text(if (plan.meetingPoint.isBlank()) "  Add meeting point" else "  Edit meeting point")
                        }
                    }
                }
            }
            item {
                SoftCard {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            Icon(Icons.Outlined.People, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary)
                            Text("Household contacts", style = MaterialTheme.typography.titleLarge)
                        }
                        if (plan.contacts.isEmpty()) {
                            Text("No contacts yet. Add someone you may need to reach.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else plan.contacts.forEachIndexed { index, contact ->
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Column(Modifier.weight(1f)) {
                                    Text(contact.name, style = MaterialTheme.typography.titleSmall)
                                    Text(contact.phone, style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { deleteIndex = index }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Remove ${contact.name}")
                                }
                            }
                        }
                        OutlinedButton(onClick = { addContact = true },
                            enabled = plan.contacts.size < 12) {
                            Icon(Icons.Outlined.Add, contentDescription = null)
                            Text("  Add contact")
                        }
                        if (plan.contacts.size >= 12) Text("Up to 12 contacts in this preview.",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item {
                SoftCard {
                    Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Outlined.Lock, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                        Column(Modifier.weight(1f)) {
                            Text("Private by design", style = MaterialTheme.typography.titleMedium)
                            Text("Your plan is encrypted using Android Keystore and is not uploaded. Removing the app deletes its local records.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        item { SafetyNotice("Do not rely on this early prototype as your only copy of critical contact information. Keep a separate accessible household plan.") }
        item { Spacer(Modifier.height(20.dp)) }
    }

    if (addContact && error == null) {
        var name by rememberSaveable { mutableStateOf("") }
        var phone by rememberSaveable { mutableStateOf("") }
        val valid = name.trim().isNotBlank() && name.length <= 48 &&
            phone.trim().matches(Regex("[+0-9() \\-]{6,26}"))
        AlertDialog(onDismissRequest = { addContact = false },
            title = { Text("Add a contact") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it.take(48) },
                        label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone, onValueChange = { phone = it.take(26) },
                        label = { Text("Phone number") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth())
                    Text("Stored only in the encrypted plan on this device.",
                        style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(enabled = valid, onClick = {
                    onSave(plan.copy(contacts = plan.contacts + PlanContact(name.trim(), phone.trim())))
                    addContact = false
                }) { Text("Save contact") }
            },
            dismissButton = { TextButton(onClick = { addContact = false }) { Text("Cancel") } }
        )
    }
    if (editMeeting && error == null) {
        var value by rememberSaveable { mutableStateOf(plan.meetingPoint) }
        AlertDialog(onDismissRequest = { editMeeting = false },
            title = { Text("Meeting point") },
            text = {
                OutlinedTextField(value = value, onValueChange = { value = it.take(180) },
                    label = { Text("Description or location") }, maxLines = 3,
                    modifier = Modifier.fillMaxWidth())
            },
            confirmButton = {
                TextButton(onClick = { onSave(plan.copy(meetingPoint = value.trim())); editMeeting = false }) {
                    Text("Save")
                }
            },
            dismissButton = { TextButton(onClick = { editMeeting = false }) { Text("Cancel") } }
        )
    }
    if (deleteIndex != null && error == null) {
        val index = deleteIndex!!
        AlertDialog(onDismissRequest = { deleteIndex = null },
            title = { Text("Remove contact?") },
            text = { Text("This contact will be removed from the encrypted plan on this device.") },
            confirmButton = {
                TextButton(onClick = {
                    if (index in plan.contacts.indices) {
                        onSave(plan.copy(contacts = plan.contacts.filterIndexed { i, _ -> i != index }))
                    }
                    deleteIndex = null
                }) { Text("Remove") }
            },
            dismissButton = { TextButton(onClick = { deleteIndex = null }) { Text("Cancel") } }
        )
    }
}
