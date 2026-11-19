package com.zyronstudio.toobitalert

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.util.Locale

class ToobitNotificationListener : NotificationListenerService() {
    private val platformsByPackage = mapOf(
        "com.twitter.android" to "X",
        "com.twitter.android.lite" to "X",
        "com.x.android" to "X",
        "com.instagram.android" to "Instagram",
        "com.instagram.lite" to "Instagram",
        "com.zhiliaoapp.musically" to "TikTok",
        "com.ss.android.ugc.trill" to "TikTok",
        "com.ss.android.ugc.tiktok" to "TikTok",
        "com.google.android.youtube" to "YouTube",
        "com.instagram.barcelona" to "Threads",
        "com.facebook.katana" to "Facebook",
        "com.facebook.lite" to "Facebook",
        "com.reddit.frontpage" to "Reddit"
    )
    private val seen = LinkedHashMap<String, Long>()

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val platform = platformsByPackage[sbn.packageName] ?: return
        val extras = sbn.notification.extras
        val values = listOf(
            extras.getCharSequence(Notification.EXTRA_TITLE)?.toString(),
            extras.getCharSequence(Notification.EXTRA_TEXT)?.toString(),
            extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString(),
            extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString(),
            extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.joinToString(" ")
        ).filterNotNull().filter { it.isNotBlank() }

        val combined = values.joinToString(" ").lowercase(Locale.ROOT)
        if (TOOBIT_MARKERS.none(combined::contains)) return

        val fingerprint = "$platform|${values.joinToString("|")}".lowercase(Locale.ROOT)
        val now = System.currentTimeMillis()
        if (seen[fingerprint]?.let { now - it < 30_000 } == true) return
        seen[fingerprint] = now
        while (seen.size > 100) seen.remove(seen.keys.first())

        val body = values.joinToString(" ").ifBlank { "Ada post baru dari akun resmi Toobit" }
        AlertNotifier.show(this, "TOOBIT • POST BARU DI $platform", body)
    }

    companion object {
        private val TOOBIT_MARKERS = listOf(
            "toobitofficial",
            "toobit_official",
            "@toobit",
            "toobit official",
            "r/toobit",
            "toobit"
        )
    }
}

