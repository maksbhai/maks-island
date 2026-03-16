package com.maks.island.overlay

import android.content.Context
import android.util.Log
import com.maks.island.services.IslandOverlayService

class OverlayController(private val context: Context) {
    fun start(forceTestIsland: Boolean = false) {
        Log.d(TAG, "Requesting overlay start. forceTestIsland=$forceTestIsland")
        val started = IslandOverlayService.start(context, forceTestIsland)
        if (!started) {
            Log.e(TAG, "Overlay start request failed safely. App will remain open; check IslandOverlayService logs for details.")
        }
    }

    fun stop() {
        Log.d(TAG, "Requesting overlay stop.")
        IslandOverlayService.stop(context)
    }

    companion object {
        private const val TAG = "OverlayController"
    }
}
