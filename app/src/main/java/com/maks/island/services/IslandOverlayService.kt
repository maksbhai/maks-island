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
import android.os.DeadObjectException
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
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
        Log.d(TAG, "onCreate: preparing foreground overlay service.")
        createNotificationChannel()
        runCatching {
            startForeground(NOTIFICATION_ID, persistentNotification())
            isRunning = true
            Log.i(TAG, "Foreground service started successfully.")
        }.onFailure { throwable ->
            Log.e(TAG, "Failed to start foreground service; stopping self to avoid app crash.", throwable)
            isRunning = false
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: flags=$flags startId=$startId")
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
        safelyRemoveView(windowManager, view)
        view = null

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

        runCatching {
            windowManager.addView(view, params)
            Log.d(TAG, "Overlay view attached to WindowManager.")
        }.onFailure { throwable ->
            when (throwable) {
                is SecurityException -> Log.e(TAG, "Overlay attach blocked by system security policy.", throwable)
                is DeadObjectException -> Log.e(TAG, "Window token became invalid while adding overlay.", throwable)
                else -> Log.e(TAG, "Failed to attach overlay view.", throwable)
            }
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        safelyRemoveView(wm, view)
        view = null
        isRunning = false
        Log.d(TAG, "Overlay service destroyed.")
    }

    private fun safelyRemoveView(windowManager: WindowManager?, candidate: View?) {
        if (windowManager == null || candidate == null) return
        runCatching {
            if (candidate.isAttachedToWindow) {
                windowManager.removeViewImmediate(candidate)
            } else {
                windowManager.removeView(candidate)
            }
        }.onFailure { throwable ->
            Log.w(TAG, "Overlay view removal skipped due to stale window token.", throwable)
        }
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

        fun start(context: Context, forceTestIsland: Boolean = false): Boolean {
            val intent = Intent(context, IslandOverlayService::class.java).putExtra(EXTRA_FORCE_TEST, forceTestIsland)
            return runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
                Log.d(TAG, "Overlay service start intent dispatched.")
                true
            }.getOrElse { throwable ->
                Log.e(TAG, "Overlay service start failed; suppressed to keep app stable.", throwable)
                false
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
