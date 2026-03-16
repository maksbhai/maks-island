package com.maks.island.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maks.island.domain.models.NotificationItem
import com.maks.island.domain.models.PreviewScenario
import com.maks.island.ui.components.IslandComposable
import com.maks.island.viewmodel.IslandViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(viewModel: IslandViewModel, onSettings: () -> Unit, onAbout: () -> Unit) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val state by viewModel.islandState.collectAsStateWithLifecycle()
    val summary by viewModel.homeSummary.collectAsStateWithLifecycle()
    val diagnostics by viewModel.diagnostics.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.onAppLaunch()
    }

    fun openOverlaySettings() {
        context.startActivity(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")),
        )
    }

    fun openNotificationAccessSettings() {
        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    fun openBatteryOptimizationSettings() {
        val pm = context.getSystemService(PowerManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && pm?.isIgnoringBatteryOptimizations(context.packageName) == false) {
            context.startActivity(
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                },
            )
        } else {
            context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Card(shape = RoundedCornerShape(30.dp), modifier = Modifier.fillMaxWidth()) {
            Row(
                Modifier
                    .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.surface)))
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Maks Island", style = MaterialTheme.typography.headlineSmall)
                    Text("Smarter Pixel-style dynamic island", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onAbout) { Icon(Icons.Default.Info, contentDescription = null) }
                IconButton(onClick = onSettings) { Icon(Icons.Default.Tune, contentDescription = null) }
            }
        }

        if (!diagnostics.overlayPermissionGranted || !diagnostics.notificationAccessGranted) {
            Card {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("First-launch setup", style = MaterialTheme.typography.titleMedium)
                    Text("Grant required permissions so Dynamic Island can appear and react at runtime.")
                    if (!diagnostics.overlayPermissionGranted) {
                        Button(onClick = ::openOverlaySettings, modifier = Modifier.fillMaxWidth()) { Text("Grant overlay permission") }
                    }
                    if (!diagnostics.notificationAccessGranted) {
                        Button(onClick = ::openNotificationAccessSettings, modifier = Modifier.fillMaxWidth()) { Text("Grant notification listener access") }
                    }
                    if (!diagnostics.batteryOptimizationIgnored) {
                        Button(onClick = ::openBatteryOptimizationSettings, modifier = Modifier.fillMaxWidth()) { Text("Disable battery optimization") }
                    }
                    Button(onClick = viewModel::refreshDiagnostics, modifier = Modifier.fillMaxWidth()) { Text("Refresh permission status") }
                }
            }
        }

        Card(shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
            Column(
                Modifier
                    .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface)))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Live Island", style = MaterialTheme.typography.titleMedium)
                IslandComposable(state = state, settings = settings, isPreview = true)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Enable island", modifier = Modifier.weight(1f))
                    Switch(checked = settings.enabled, onCheckedChange = { viewModel.setBool("enabled", it) })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = viewModel::showTestIslandNow,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                        Text(" Test island")
                    }
                    TextButton(onClick = viewModel::clearToIdle, modifier = Modifier.weight(1f)) { Text("Clear") }
                }
            }
        }

        Card {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Diagnostics", style = MaterialTheme.typography.titleMedium)
                Text("Overlay permission: ${if (diagnostics.overlayPermissionGranted) "Granted" else "Missing"}")
                Text("Notification access: ${if (diagnostics.notificationAccessGranted) "Granted" else "Missing"}")
                Text("Battery optimization ignored: ${if (diagnostics.batteryOptimizationIgnored) "Yes" else "No"}")
                Text("Overlay service running: ${if (diagnostics.overlayServiceRunning) "Yes" else "No"}")
                Text("Island enabled setting: ${if (diagnostics.islandEnabled) "On" else "Off"}")
                Button(onClick = viewModel::refreshDiagnostics, modifier = Modifier.fillMaxWidth()) { Text("Refresh diagnostics") }
            }
        }

        Card { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Control Center", style = MaterialTheme.typography.titleMedium)
            Text(summary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = { viewModel.triggerPreviewScenario(PreviewScenario.Notification) }, label = { Text("Ping") }, leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null) })
                AssistChip(onClick = onSettings, label = { Text("Tune UI") }, leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) })
            }
            Text("Developer: Maks • Version 1.0.0", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } }

        Card { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Quick Preview Scenarios", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PreviewScenario.entries.forEach {
                    AssistChip(onClick = { viewModel.triggerPreviewScenario(it) }, label = { Text(it.name) })
                }
            }
        } }

        Card { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Demo Actions", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.pushNotification(NotificationItem("com.demo", "Demo", "Ride arriving", "Your taxi is 2 min away")) }) { Text("Notification") }
                Button(onClick = viewModel::setMediaDemo) { Text("Media") }
                Button(onClick = viewModel::setChargingDemo) { Text("Charging") }
                Button(onClick = viewModel::setTimerDemo) { Text("Timer") }
                Button(onClick = viewModel::setCallDemo) { Text("Call") }
            }
            Button(onClick = viewModel::clearToIdle, modifier = Modifier.fillMaxWidth()) { Text("Back to Idle") }
        } }
    }
}
