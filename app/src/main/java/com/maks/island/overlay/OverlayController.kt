package com.maks.island.overlay

import android.content.Context
import android.content.Intent
import com.maks.island.services.IslandOverlayService

class OverlayController(private val context: Context) {
    fun start() {
        context.startForegroundService(Intent(context, IslandOverlayService::class.java))
    }

    fun stop() {
        context.stopService(Intent(context, IslandOverlayService::class.java))
    }
}
