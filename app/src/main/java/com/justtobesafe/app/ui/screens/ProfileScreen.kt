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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.data.LocalHouseholdStore
import com.justtobesafe.app.ui.components.DestinationRow
import com.justtobesafe.app.ui.components.SafetyNotice
import com.justtobesafe.app.ui.components.SectionTitle
import com.justtobesafe.app.ui.components.SmallBadge
import com.justtobesafe.app.ui.components.SoftCard

/** Profile > Household: an honest local-only first vertical slice. */
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val store = remember(context) { LocalHouseholdStore(context) }
    var name by remember { mutableStateOf(store.readName()) }
    var page by rememberSaveable { mutableStateOf("profile") }
    var editing by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            if (page != "profile") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { page = "profile" }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back to profile")
                    }
                    Text("Household", style = MaterialTheme.typography.headlineSmall)
                }
            } else SectionTitle("Profile", "Household and app preferences")
        }

        if (page == "profile") {
            item {
                DestinationRow(
                    title = name,
                    subtitle = "Manage this device's household profile",
                    icon = { Icon(Icons.Outlined.Home, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary) },
                    onClick = { page = "household" }
                )
            }
            item {
                SoftCard {
                    Column(Modifier.padding(17.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Family collaboration", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Family invitations and shared accounts are not connected in this prototype. " +
                                "Your current pantry and household plan stay on this device.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        SmallBadge("LOCAL DEVICE ONLY")
                    }
                }
            }
            item {
                SafetyNotice("Emergency guidance is not yet expert-reviewed and no live official warnings are supplied.")
            }
        } else {
            item {
                SoftCard {
                    Column(Modifier.padding(17.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Outlined.Groups, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary)
                            Column(Modifier.weight(1f)) {
                                Text(name, style = MaterialTheme.typography.titleLarge,
                                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text("Private local household",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Button(onClick = { editing = true }) { Text("Rename household") }
                    }
                }
            }
            item {
                SectionTitle("Household members", "People with access to this household")
            }
            item {
                SoftCard {
                    Row(Modifier.padding(17.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Outlined.PersonOutline, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                        Column(Modifier.weight(1f)) {
                            Text("This device", style = MaterialTheme.typography.titleMedium)
                            Text("Local owner · Not a signed-in account",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Column(Modifier.padding(17.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Invite family", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Secure invitations, member roles and shared editing will be available " +
                                "after account authentication and household synchronisation are built.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item {
                SoftCard {
                    Row(Modifier.padding(17.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Outlined.Lock, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                        Column(Modifier.weight(1f)) {
                            Text("Your information stays local",
                                style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(5.dp))
                            Text(
                                "Changing the household name does not move, synchronise or share " +
                                    "your pantry, contacts or preparedness plan. Do not enter sensitive " +
                                    "personal details in the household name.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        if (message != null) item {
            Text(message!!, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item { Spacer(Modifier.height(12.dp)) }
    }

    if (editing) {
        var draft by remember(name) { mutableStateOf(name) }
        val valid = draft.trim().isNotEmpty() && draft.trim().length <= 60 &&
            draft.none { Character.isISOControl(it) }
        AlertDialog(
            onDismissRequest = { editing = false },
            title = { Text("Household name") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = draft, onValueChange = { draft = it },
                        label = { Text("Name") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = { Text("Up to 60 characters · stored only on this device") }
                    )
                    if (!valid) Text("Enter a name of up to 60 characters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {
                TextButton(enabled = valid, onClick = {
                    if (store.saveName(draft)) {
                        name = draft.trim()
                        message = "Household name saved on this device."
                        editing = false
                    } else message = "Could not save. Please try again."
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { editing = false }) { Text("Cancel") } }
        )
    }
}
