package com.maks.island.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Maks Island", style = MaterialTheme.typography.headlineMedium)
        Text("Developer: Maks")
        Text("A Pixel-focused Dynamic Island style utility with overlay, notification routing and persistent live states.")
        Text("Permissions: Overlay, notification access, foreground service, optional battery optimization exemption.")
        Text("Version: 1.0.0")
        Text("GitHub: https://github.com/placeholder/maks-island")
        Text("Feedback: support@placeholder.dev")
        Text("Privacy: Notification content is processed on-device and can be hidden with sensitive-content mode.")
        Button(onClick = onBack) { Text("Back") }
    }
}
