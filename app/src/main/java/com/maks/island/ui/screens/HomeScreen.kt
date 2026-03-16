package com.maks.island.ui.screens

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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Maks Island", style = MaterialTheme.typography.headlineSmall)
                Text("Premium Pixel dynamic island", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onAbout) { Icon(Icons.Default.Info, contentDescription = null) }
            IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, contentDescription = null) }
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
            }
        }

        Card { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Control Center", style = MaterialTheme.typography.titleMedium)
            Text(summary)
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
