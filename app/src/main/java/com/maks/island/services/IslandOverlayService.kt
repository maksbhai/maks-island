package com.maks.island.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import com.maks.island.domain.models.IslandSettings
import com.maks.island.domain.models.IslandVisualState
import com.maks.island.domain.models.NotificationItem
import com.maks.island.ui.components.IslandComposable

class IslandOverlayService : Service() {
    private var wm: WindowManager? = null
    private var view: ComposeView? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, persistentNotification())
        isRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!Settings.canDrawOverlays(this)) {
            Log.w(TAG, "Overlay permission missing. Stopping overlay service.")
            stopSelf()
            return START_NOT_STICKY
        }

        val forceTestIsland = intent?.getBooleanExtra(EXTRA_FORCE_TEST, false) == true
        Log.d(TAG, "Starting/refreshing overlay view. forceTestIsland=$forceTestIsland")
        showOverlay(forceTestIsland)
        return START_STICKY
    }

    private fun showOverlay(forceTestIsland: Boolean) {
        val windowManager = wm ?: (getSystemService(WINDOW_SERVICE) as WindowManager).also { wm = it }
        view?.let { existing ->
            windowManager.removeView(existing)
            view = null
        }

        val defaultSettings = IslandSettings(
            width = if (forceTestIsland) 300f else 240f,
            height = if (forceTestIsland) 58f else 48f,
            topOffset = 56f,
            transparency = if (forceTestIsland) 1f else 0.96f,
            shadowIntensity = 0.65f,
            glowIntensity = 0.45f,
        )
        val state = if (forceTestIsland) {
            IslandVisualState.Notification(
                NotificationItem(
                    packageName = packageName,
                    appName = "Dynamic Island Test",
                    title = "Overlay is visible",
                    body = "This is a guaranteed test island",
                )
            )
        } else {
            IslandVisualState.Idle
        }

        view = ComposeView(this).apply {
            setContent {
                IslandComposable(
                    state = state,
                    settings = defaultSettings,
                )
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 0
        }

        windowManager.addView(view, params)
        Log.d(TAG, "Overlay view attached to WindowManager.")
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.let { wm?.removeView(it) }
        view = null
        isRunning = false
        Log.d(TAG, "Overlay service destroyed.")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Maks Island", NotificationManager.IMPORTANCE_LOW))
        }
    }

    private fun persistentNotification(): Notification =
        Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Maks Island active")
            .setContentText("Keeping the floating island ready")
            .setSmallIcon(android.R.drawable.star_on)
            .build()

    companion object {
        private const val CHANNEL_ID = "island"
        private const val NOTIFICATION_ID = 7
        private const val EXTRA_FORCE_TEST = "force_test"
        private const val TAG = "IslandOverlayService"

        @Volatile
        var isRunning: Boolean = false
            private set

        fun start(context: Context, forceTestIsland: Boolean = false) {
            val intent = Intent(context, IslandOverlayService::class.java).putExtra(EXTRA_FORCE_TEST, forceTestIsland)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, IslandOverlayService::class.java))
        }

        fun overlaySettingsIntent(context: Context): Intent =
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}"),
            )
    }
}
