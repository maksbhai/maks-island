package com.maks.island.overlay

import android.content.Context
import android.util.Log
import com.maks.island.services.IslandOverlayService

class OverlayController(private val context: Context) {
    fun start(forceTestIsland: Boolean = false) {
        Log.d(TAG, "Requesting overlay start. forceTestIsland=$forceTestIsland")
        IslandOverlayService.start(context, forceTestIsland)
    }

    fun stop() {
        Log.d(TAG, "Requesting overlay stop.")
        IslandOverlayService.stop(context)
    }

    companion object {
        private const val TAG = "OverlayController"
    }
}
