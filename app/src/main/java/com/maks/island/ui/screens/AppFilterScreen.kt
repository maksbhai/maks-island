package com.maks.island.ui.screens

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maks.island.viewmodel.IslandViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppFilterScreen(viewModel: IslandViewModel, onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val pm = context.packageManager
    val allowList by viewModel.appAllowList.collectAsStateWithLifecycle()
    val apps = remember { mutableStateListOf<ApplicationInfo>() }
    var search by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        apps.clear()
        apps.addAll(pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { pm.getLaunchIntentForPackage(it.packageName) != null })
        loading = false
    }

    val filtered = apps.filter { it.loadLabel(pm).toString().contains(search, true) }

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Per-App Notification Control") }) }) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(search, { search = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Search installed apps") })
            AssistChip(onClick = onBack, label = { Text("Back") })
            when {
                loading -> BoxFill { CircularProgressIndicator() }
                filtered.isEmpty() -> BoxFill { Text("No apps found.") }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtered) { app ->
                        val label = app.loadLabel(pm).toString()
                        val enabled = allowList.isEmpty() || allowList.contains(app.packageName)
                        Surface(shape = RoundedCornerShape(18.dp), tonalElevation = 2.dp) {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                BoxIcon()
                                Column(Modifier.weight(1f)) {
                                    Text(label)
                                    Text(app.packageName, maxLines = 1)
                                    Text(if (enabled) "Allowed in island" else "Blocked in island")
                                }
                                Switch(
                                    checked = enabled,
                                    onCheckedChange = { viewModel.setAppAllowed(app.packageName, it) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxIcon() {
    androidx.compose.foundation.layout.Box(
        Modifier.size(36.dp).background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Default.Apps, contentDescription = null)
    }
}

@Composable
private fun BoxFill(content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}
