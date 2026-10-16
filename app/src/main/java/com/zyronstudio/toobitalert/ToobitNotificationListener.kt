package com.zyronstudio.toobitalert

import android.app.Notification
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.util.Locale

class ToobitNotificationListener : NotificationListenerService() {
    private val xPackages = setOf("com.twitter.android", "com.twitter.android.lite", "com.x.android")
    private val seen = LinkedHashMap<String, Long>()

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName !in xPackages) return
        val extras = sbn.notification.extras
        val values = listOf(
            extras.getCharSequence(Notification.EXTRA_TITLE)?.toString(),
            extras.getCharSequence(Notification.EXTRA_TEXT)?.toString(),
            extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString(),
            extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString(),
            extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.joinToString(" ")
        ).filterNotNull().filter { it.isNotBlank() }
        val combined = values.joinToString(" ").lowercase(Locale.ROOT)
        val fromToobit = combined.contains("toobit") || combined.contains("toobit_official") || combined.contains("@toobit")
        if (!fromToobit) return
        val fingerprint = values.joinToString("|").lowercase(Locale.ROOT)
        val now = System.currentTimeMillis()
        if (seen[fingerprint]?.let { now - it < 30_000 } == true) return
        seen[fingerprint] = now
        while (seen.size > 100) seen.remove(seen.keys.first())
        val body = values.joinToString(" ").ifBlank { "Ada post baru dari @Toobit_official" }
        AlertNotifier.show(this, "TOOBIT OFFICIAL • POST BARU", body)
    }
}
