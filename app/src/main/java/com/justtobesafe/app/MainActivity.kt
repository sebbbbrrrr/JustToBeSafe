package com.justtobesafe.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.data.HouseholdPlan
import com.justtobesafe.app.data.InventoryStore
import com.justtobesafe.app.data.SecurePlanStore
import com.justtobesafe.app.domain.SuppliesCatalog
import com.justtobesafe.app.ui.screens.GuideTopic
import com.justtobesafe.app.ui.screens.GuidesScreen
import com.justtobesafe.app.ui.screens.HomeScreen
import com.justtobesafe.app.ui.screens.PlanScreen
import com.justtobesafe.app.ui.screens.SuppliesScreen
import com.justtobesafe.app.ui.theme.CalmUtilityTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CalmUtilityTheme { JustToBeSafeApp() } }
    }
}

private enum class Destination(val label: String) { HOME("Home"), GUIDES("Guides"), SUPPLIES("Supplies"), PLAN("Plan") }

@Composable
private fun DestinationIcon(destination: Destination) {
    val icon = when (destination) {
        Destination.HOME -> Icons.Outlined.Home
        Destination.GUIDES -> Icons.Outlined.MenuBook
        Destination.SUPPLIES -> Icons.Outlined.Inventory2
        Destination.PLAN -> Icons.Outlined.People
    }
    Icon(icon, contentDescription = null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JustToBeSafeApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val inventory = remember(context) { InventoryStore(context) }
    val securePlan = remember(context) { SecurePlanStore(context) }
    var checked by remember { mutableStateOf(inventory.read()) }
    var plan by remember { mutableStateOf(HouseholdPlan()) }
    var planLoaded by remember { mutableStateOf(false) }
    var planError by remember { mutableStateOf<String?>(null) }
    var destinationName by rememberSaveable { mutableStateOf(Destination.HOME.name) }
    val destination = Destination.valueOf(destinationName)
    var selectedGuideId by rememberSaveable { mutableStateOf<String?>(null) }
    var showAbout by rememberSaveable { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        try { plan = securePlan.read() }
        catch (_: Exception) { planError = "Unable to unlock the household plan on this device." }
        finally { planLoaded = true }
    }
    BackHandler(enabled = selectedGuideId != null || destination != Destination.HOME) {
        if (selectedGuideId != null) selectedGuideId = null
        else destinationName = Destination.HOME.name
    }

    val setDestination: (Destination) -> Unit = {
        selectedGuideId = null
        destinationName = it.name
    }
    val toggleSupply: (String) -> Unit = { id ->
        if (id in SuppliesCatalog.validIds) {
            val next = if (id in checked) checked - id else checked + id
            if (inventory.write(next)) checked = next
            else scope.launch { snackbar.showSnackbar("Could not save. Please try again.") }
        }
    }
    val savePlan: (HouseholdPlan) -> Unit = { next ->
        scope.launch {
            try {
                securePlan.write(next)
                plan = next
                planError = null
                snackbar.showSnackbar("Saved securely on this device")
            } catch (_: Exception) {
                planError = "Unable to save the household plan. Please try again."
                snackbar.showSnackbar("Secure save failed — nothing was changed")
            }
        }
    }
    val openOfficial: (String) -> Unit = { url ->
        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
            .onFailure { scope.launch { snackbar.showSnackbar("No browser available on this device") } }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val expanded = maxWidth >= 600.dp
        Row(Modifier.fillMaxSize()) {
            if (expanded) NavigationRail(containerColor = MaterialTheme.colorScheme.surface) {
                Destination.entries.forEach { tab ->
                    NavigationRailItem(selected = tab == destination,
                        onClick = { setDestination(tab) },
                        icon = { DestinationIcon(tab) }, label = { Text(tab.label) },
                        alwaysShowLabel = true)
                }
            }
            Scaffold(
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    TopAppBar(
                        title = { Text("JustToBeSafe", style = MaterialTheme.typography.titleLarge) },
                        actions = {
                            IconButton(onClick = { showAbout = true }) {
                                Icon(Icons.Outlined.Info, contentDescription = "About this prototype")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background)
                    )
                },
                bottomBar = {
                    if (!expanded) NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        Destination.entries.forEach { tab ->
                            NavigationBarItem(selected = tab == destination,
                                onClick = { setDestination(tab) },
                                icon = { DestinationIcon(tab) }, label = { Text(tab.label) },
                                alwaysShowLabel = true)
                        }
                    }
                },
                snackbarHost = { SnackbarHost(snackbar) }
            ) { inner ->
                val screenModifier = Modifier.fillMaxSize().padding(inner)
                if (selectedGuideId != null) {
                    val topic = GuideTopic.entries.firstOrNull { it.id == selectedGuideId }
                    if (topic != null) {
                        GuidesScreen(modifier = screenModifier, selectedGuide = topic,
                            onSelect = { selectedGuideId = it }, onBack = { selectedGuideId = null },
                            openOfficial = openOfficial)
                    }
                } else {
                    AnimatedContent(targetState = destination,
                        transitionSpec = { fadeIn(tween(170)) togetherWith fadeOut(tween(100)) },
                        label = "Destination content", modifier = screenModifier) { tab ->
                        when (tab) {
                            Destination.HOME -> HomeScreen(checked = checked,
                                contacts = plan.contacts.size, hasMeetingPoint = plan.meetingPoint.isNotBlank(),
                                onGuides = { setDestination(Destination.GUIDES) },
                                onSupplies = { setDestination(Destination.SUPPLIES) },
                                onPlan = { setDestination(Destination.PLAN) },
                                onOfficialWarnings = { openOfficial("https://weather.metoffice.gov.uk/warnings-and-advice/uk-warnings") })
                            Destination.GUIDES -> GuidesScreen(
                                onSelect = { selectedGuideId = it }, openOfficial = openOfficial)
                            Destination.SUPPLIES -> SuppliesScreen(checked = checked, onToggle = toggleSupply)
                            Destination.PLAN -> PlanScreen(plan = plan, loaded = planLoaded,
                                error = planError, onSave = savePlan)
                        }
                    }
                }
            }
        }
    }

    if (showAbout) AlertDialog(onDismissRequest = { showAbout = false },
        title = { Text("About this preview") },
        text = { Text("JustToBeSafe is an Android prototype, not an emergency alert service. " +
            "Emergency guidance is awaiting specialist review. No live hazards or warnings are connected. " +
            "Your checklist stays on this device; household details are encrypted on this device. " +
            "In an emergency, contact the appropriate emergency services and consult official sources.") },
        confirmButton = { TextButton(onClick = { showAbout = false }) { Text("Understood") } })
}
