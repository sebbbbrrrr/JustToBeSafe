package com.justtobesafe.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.data.FoodProductLookup
import com.justtobesafe.app.data.PantryStore
import com.justtobesafe.app.domain.ExpiryState
import com.justtobesafe.app.domain.PantryItem
import com.justtobesafe.app.domain.expiryState
import com.justtobesafe.app.domain.pantryPackageCount
import com.justtobesafe.app.domain.pantryValidationError
import com.justtobesafe.app.domain.reservePackageCount
import com.justtobesafe.app.ui.components.BarcodeScannerDialog
import com.justtobesafe.app.ui.components.SectionTitle
import com.justtobesafe.app.ui.components.SmallBadge
import com.justtobesafe.app.ui.components.SoftCard
import kotlinx.coroutines.launch

@Composable
fun PantryScreen() {
    val context = LocalContext.current
    val store = remember(context) { PantryStore(context) }
    DisposableEffect(store) { onDispose { store.close() } }
    val lookup = remember { FoodProductLookup() }
    val scope = rememberCoroutineScope()
    val initial = remember(store) { runCatching { store.readAll() } }
    var products by remember(store) { mutableStateOf(initial.getOrDefault(emptyList())) }
    var search by rememberSaveable { mutableStateOf("") }
    var scanning by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<PantryItem?>(null) }
    var deleting by remember { mutableStateOf<PantryItem?>(null) }
    var loading by remember { mutableStateOf(false) }
    var notice by remember {
        mutableStateOf<String?>(
            if (initial.isFailure) "Could not read saved pantry data. Reopen the screen to retry." else null
        )
    }

    fun reload() { products = store.readAll() }
    fun save(item: PantryItem) {
        runCatching { store.save(item); reload() }.onSuccess {
            editing = null
            notice = "Saved locally. Your pantry is available offline."
        }.onFailure { notice = "Could not save your item. Check storage and try again." }
    }
    fun handleBarcode(barcode: String) {
        scanning = false
        // Never silently duplicate a known barcode; the proposed increment is editable.
        val existing = products.firstOrNull { it.barcode == barcode }
        if (existing != null) {
            editing = existing.copy(quantity = (existing.quantity + 1).coerceAtMost(9999))
            notice = "Already in your pantry. Check the proposed quantity before saving."
            return
        }
        scope.launch {
            loading = true
            val found = runCatching { lookup.find(barcode) }.getOrNull()
            loading = false
            editing = PantryItem(barcode = barcode, name = found?.name.orEmpty(),
                brand = found?.brand.orEmpty(), packSize = found?.packSize.orEmpty())
            notice = if (found == null) {
                "Product unavailable or you're offline. Enter its name to save it."
            } else "Product details suggested by Open Food Facts. Check the packaging."
        }
    }
    val filtered = products.filter {
        search.isBlank() || listOf(it.name, it.brand, it.barcode, it.location)
            .any { value -> value.contains(search.trim(), ignoreCase = true) }
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "pantry-heading") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionTitle("Food pantry", "Track the packages you have at home, including emergency reserves")
                SmallBadge("INVENTORY SAVED ON THIS DEVICE")
            }
        }
        item(key = "pantry-counts") {
            SoftCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${pantryPackageCount(products)} packages across ${products.size} products",
                        style = MaterialTheme.typography.titleMedium)
                    Text("${reservePackageCount(products)} packages marked as emergency reserves",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Counts describe stored packages only, not how many days of food are available.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item(key = "pantry-actions") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { scanning = true }, modifier = Modifier.weight(1f),
                    enabled = !loading && initial.isSuccess) {
                    Icon(Icons.Outlined.QrCodeScanner, contentDescription = null)
                    Spacer(Modifier.width(7.dp))
                    Text("Scan")
                }
                OutlinedButton(onClick = { editing = PantryItem() },
                    modifier = Modifier.weight(1f), enabled = !loading) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(Modifier.width(7.dp))
                    Text("Add item")
                }
            }
        }
        if (loading) item(key = "lookup-progress") {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CircularProgressIndicator(modifier = Modifier.width(24.dp).height(24.dp))
                Text("Looking up this barcode…", style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (notice != null) item(key = "pantry-notice") {
            Text(notice!!, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item(key = "pantry-search") {
            OutlinedTextField(
                value = search, onValueChange = { search = it },
                label = { Text("Search pantry") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
        }
        if (products.isEmpty()) item(key = "pantry-empty") {
            SoftCard {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("No food recorded yet", style = MaterialTheme.typography.titleMedium)
                    Text("Scan a package or add an item manually. Your list stays on this phone.",
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else if (filtered.isEmpty()) item(key = "pantry-no-results") {
            Text("No matching food. Try another search.",
                style = MaterialTheme.typography.bodyMedium)
        }
        items(items = filtered, key = { it.id }) { product ->
            PantryRow(
                item = product,
                onEdit = { editing = product },
                onDelete = { deleting = product },
                onChangeQuantity = { newQuantity -> save(product.copy(quantity = newQuantity)) }
            )
        }
        item(key = "pantry-footnote") {
            Text("Barcode names, brands and package sizes are supplied by Open Food Facts (ODbL). " +
                "Lookups need internet and may be missing or inaccurate. Camera scanning and saved inventory work offline. " +
                "Dates are reminders only; follow the date and storage instructions on the food packaging.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item { Spacer(Modifier.height(16.dp)) }
    }

    if (scanning) BarcodeScannerDialog(onDetected = ::handleBarcode, onDismiss = { scanning = false })
    editing?.let { item ->
        PantryEditorDialog(item = item, onDismiss = { editing = null }, onSave = ::save)
    }
    deleting?.let { item ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Remove ${item.name}?") },
            text = { Text("This removes the inventory entry from this device.") },
            confirmButton = {
                TextButton(onClick = {
                    runCatching {
                        check(store.delete(item.id))
                        reload()
                    }.onSuccess { notice = "Item removed." }
                        .onFailure { notice = "Could not remove the item. Try again." }
                    deleting = null
                }) { Text("Remove") }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun PantryRow(
    item: PantryItem, onEdit: () -> Unit, onDelete: () -> Unit,
    onChangeQuantity: (Int) -> Unit
) {
    val state = expiryState(item.expiryDate)
    SoftCard {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(item.name, style = MaterialTheme.typography.titleMedium,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                    val detail = listOf(item.brand, item.packSize).filter { it.isNotBlank() }
                        .joinToString(" · ")
                    if (detail.isNotEmpty()) Text(detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit ${item.name}")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = "Remove ${item.name}")
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("${item.quantity} pack${if (item.quantity == 1) "" else "s"}",
                    style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                IconButton(onClick = { onChangeQuantity(item.quantity - 1) },
                    enabled = item.quantity > 1) {
                    Icon(Icons.Outlined.Remove, contentDescription = "One fewer ${item.name}")
                }
                IconButton(onClick = { onChangeQuantity(item.quantity + 1) },
                    enabled = item.quantity < 9999) {
                    Icon(Icons.Outlined.Add, contentDescription = "One more ${item.name}")
                }
            }
            val dateLabel = when (state) {
                ExpiryState.EXPIRED -> "Entered date passed: ${item.expiryDate}"
                ExpiryState.SOON -> "Date within 30 days: ${item.expiryDate}"
                ExpiryState.GOOD -> "Date on pack: ${item.expiryDate}"
                ExpiryState.UNKNOWN -> "No date entered"
            }
            Text("${item.location.ifBlank { "Pantry" }} · $dateLabel",
                style = MaterialTheme.typography.bodySmall,
                color = if (state == ExpiryState.EXPIRED) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant)
            if (item.emergencyReserve) SmallBadge("EMERGENCY RESERVE")
        }
    }
}

@Composable
private fun PantryEditorDialog(
    item: PantryItem,
    onDismiss: () -> Unit,
    onSave: (PantryItem) -> Unit
) {
    var name by remember(item.id, item.barcode, item.name) { mutableStateOf(item.name) }
    var barcode by remember(item.id, item.barcode, item.name) { mutableStateOf(item.barcode) }
    var brand by remember(item.id, item.barcode, item.name) { mutableStateOf(item.brand) }
    var packSize by remember(item.id, item.barcode, item.name) { mutableStateOf(item.packSize) }
    var quantity by remember(item.id, item.barcode, item.name) { mutableStateOf(item.quantity.toString()) }
    var expiry by remember(item.id, item.barcode, item.name) { mutableStateOf(item.expiryDate) }
    var location by remember(item.id, item.barcode, item.name) { mutableStateOf(item.location) }
    var reserve by remember(item.id, item.barcode, item.name) { mutableStateOf(item.emergencyReserve) }
    val next = item.copy(name = name.trim(), barcode = barcode.trim(), brand = brand.trim(),
        packSize = packSize.trim(), quantity = quantity.toIntOrNull() ?: 0,
        expiryDate = expiry.trim(), location = location.trim(), emergencyReserve = reserve)
    val error = pantryValidationError(next)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item.id == 0L) "Add food" else "Edit food") },
        text = {
            Column(Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Product name *") },
                    singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(barcode, { barcode = it.filter { ch -> ch in '0'..'9' }.take(14) },
                    label = { Text("Barcode (optional)") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(brand, { brand = it }, label = { Text("Brand") },
                    singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(packSize, { packSize = it },
                    label = { Text("Pack size (e.g. 400 g)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(quantity, { quantity = it.filter { ch -> ch in '0'..'9' }.take(4) },
                    label = { Text("Number of packs *") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(expiry, { expiry = it },
                    label = { Text("Date on pack (YYYY-MM-DD)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(location, { location = it },
                    label = { Text("Storage location") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Emergency reserve", style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f))
                    Switch(checked = reserve, onCheckedChange = { reserve = it })
                }
                if (error != null) Text(error, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(next) }, enabled = error == null) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
