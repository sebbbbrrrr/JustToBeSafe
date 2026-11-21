package com.justtobesafe.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.justtobesafe.app.data.HouseholdPlan
import com.justtobesafe.app.data.InventoryStore
import com.justtobesafe.app.data.SecurePlanStore
import com.justtobesafe.app.domain.SuppliesCatalog
import com.justtobesafe.app.ui.components.calmMotionDuration
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

private enum class Destination(val label: String) {
    HOME("Home"), GUIDES("Guides"), SUPPLIES("Supplies"), PLAN("Plan")
}

@Composable
private fun DestinationIcon(destination: Destination) {
    val icon = when (destination) {
        Destination.HOME -> Icons.Outlined.Home
        Destination.GUIDES -> Icons.Outlined.MenuBook
        Destination.SUPPLIES -> Icons.Outlined.Inventory2
        Destination.PLAN -> Icons.Outlined.PeopleOutline
    }
    Icon(icon, contentDescription = null)
}

@Composable
private fun CalmWordmark() {
    val colors = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.size(38.dp).background(colors.primary, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.VerifiedUser, contentDescription = null,
                tint = colors.onPrimary, modifier = Modifier.size(25.dp))
        }
        Column {
            Text("JustToBeSafe", style = MaterialTheme.typography.titleMedium)
            Text("HOUSEHOLD PREPAREDNESS", style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JustToBeSafeApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
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
    val duration = calmMotionDuration(190)
    val exitDuration = calmMotionDuration(95)

    LaunchedEffect(Unit) {
        try { plan = securePlan.read() }
        catch (_: Exception) { planError = "Unable to unlock the household plan on this device." }
        finally { planLoaded = true }
    }
    BackHandler(enabled = showAbout || selectedGuideId != null || destination != Destination.HOME) {
        when {
            showAbout -> showAbout = false
            selectedGuideId != null -> selectedGuideId = null
            else -> destinationName = Destination.HOME.name
        }
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
                snackbar.showSnackbar("Secure save failed — no changes were stored")
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
                    NavigationRailItem(
                        selected = tab == destination,
                        onClick = { setDestination(tab) },
                        icon = { DestinationIcon(tab) },
                        label = { Text(tab.label) },
                        alwaysShowLabel = true
                    )
                }
            }
            Scaffold(
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    TopAppBar(
                        title = { CalmWordmark() },
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
                    if (!expanded) NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp
                    ) {
                        Destination.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = tab == destination,
                                onClick = { setDestination(tab) },
                                icon = { DestinationIcon(tab) },
                                label = { Text(tab.label) },
                                alwaysShowLabel = true,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                },
                snackbarHost = { SnackbarHost(snackbar) }
            ) { inner ->
                val contentModifier = Modifier.fillMaxSize().padding(inner)
                AnimatedContent(
                    targetState = destination to selectedGuideId,
                    transitionSpec = {
                        fadeIn(tween(durationMillis = duration)) togetherWith
                            fadeOut(tween(durationMillis = exitDuration))
                    }, label = "Main navigation transition",
                    modifier = contentModifier
                ) { state ->
                    val tab = state.first
                    val selected = state.second
                    val guide = selected?.let { id -> GuideTopic.entries.firstOrNull { it.id == id } }
                    if (guide != null) {
                        GuidesScreen(
                            modifier = Modifier.fillMaxSize(), selectedGuide = guide,
                            onSelect = { selectedGuideId = it },
                            onBack = { selectedGuideId = null }, openOfficial = openOfficial
                        )
                    } else when (tab) {
                        Destination.HOME -> HomeScreen(
                            checked = checked, contacts = plan.contacts.size,
                            hasMeetingPoint = plan.meetingPoint.isNotBlank(),
                            onGuides = { setDestination(Destination.GUIDES) },
                            onSupplies = { setDestination(Destination.SUPPLIES) },
                            onPlan = { setDestination(Destination.PLAN) },
                            onOfficialWarnings = {
                                openOfficial("https://weather.metoffice.gov.uk/warnings-and-advice/uk-warnings")
                            }
                        )
                        Destination.GUIDES -> GuidesScreen(
                            onSelect = { selectedGuideId = it }, openOfficial = openOfficial
                        )
                        Destination.SUPPLIES -> SuppliesScreen(checked = checked, onToggle = toggleSupply)
                        Destination.PLAN -> PlanScreen(plan = plan, loaded = planLoaded,
                            error = planError, onSave = savePlan)
                    }
                }
            }
        }
    }

    if (showAbout) AlertDialog(
        onDismissRequest = { showAbout = false },
        title = { Text("About this preview") },
        text = { Text(
            "JustToBeSafe is an Android interface prototype, not an emergency alert service. " +
                "Emergency guidance awaits qualified specialist review. " +
                "No live hazard data or current warnings are connected. " +
                "Supply selections are stored locally; household plan details are encrypted on this device. " +
                "Follow official advice and contact the appropriate emergency services when needed."
        ) },
        confirmButton = { TextButton(onClick = { showAbout = false }) { Text("Understood") } }
    )
}
