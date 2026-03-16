package com.maks.island.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = listOf(
        "Welcome to Maks Island",
        "Overlay permission keeps the island visible around Pixel camera area.",
        "Notification access powers premium routing into island cards.",
        "Battery optimization exemption helps keep background service reliable."
    )
    var page by remember { mutableIntStateOf(0) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceContainer)))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LinearProgressIndicator(progress = { (page + 1f) / pages.size }, modifier = Modifier.fillMaxWidth())
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(pages[page], style = MaterialTheme.typography.headlineSmall)
                Text("Maks Island builds a tasteful Dynamic Island-like overlay within Android limitations.")
            }
        }
        Button(onClick = {
            if (page == pages.lastIndex) onFinish() else page += 1
        }, modifier = Modifier.fillMaxWidth()) { Text(if (page == pages.lastIndex) "Start Maks Island" else "Continue") }
    }
}
