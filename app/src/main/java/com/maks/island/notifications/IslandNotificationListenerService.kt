package com.maks.island.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class IslandNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        // Android limitation: third-party apps cannot globally replace heads-up notifications.
        // We mirror allowed notifications into Maks Island experience rather than suppress system UI.
    }
}
