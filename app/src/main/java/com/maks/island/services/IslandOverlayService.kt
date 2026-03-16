package com.maks.island.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import com.maks.island.ui.components.IslandComposable
import com.maks.island.domain.models.IslandSettings
import com.maks.island.domain.models.IslandVisualState

class IslandOverlayService : Service() {
    private var wm: WindowManager? = null
    private var view: ComposeView? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(7, persistentNotification())

        // Android restricts overlays unless SYSTEM_ALERT_WINDOW is granted by user in settings.
        if (!Settings.canDrawOverlays(this)) return

        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        view = ComposeView(this).apply {
            setContent { IslandComposable(state = IslandVisualState.Idle, settings = IslandSettings()) }
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP
            y = 0
        }
        wm?.addView(view, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.let { wm?.removeView(it) }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(NotificationChannel("island", "Maks Island", NotificationManager.IMPORTANCE_LOW))
        }
    }

    private fun persistentNotification(): Notification =
        Notification.Builder(this, "island")
            .setContentTitle("Maks Island active")
            .setContentText("Keeping the floating island ready")
            .setSmallIcon(android.R.drawable.star_on)
            .build()
}
