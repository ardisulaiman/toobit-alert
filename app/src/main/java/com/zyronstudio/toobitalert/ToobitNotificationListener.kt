package com.zyronstudio.toobitalert

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.media.AudioAttributes
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator

class ToobitNotificationListener : NotificationListenerService() {
    private val xPackages = setOf("com.twitter.android", "com.twitter.android.lite")
    private val channelId = "toobit_official_alert"
    private var lastKey = ""

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName !in xPackages || sbn.key == lastKey) return
        val extras = sbn.notification.extras
        val text = (extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: "").trim()
        val title = (extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: "").trim()
        val combined = "$title $text"
        if (!combined.contains("Toobit", ignoreCase = true) && !combined.contains("@Toobit_official", ignoreCase = true)) return
        lastKey = sbn.key
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val sound = Uri.parse("android.resource://$packageName/raw/toobit_alert")
        if (Build.VERSION.SDK_INT >= 26) {
            val ch = NotificationChannel(channelId, "Toobit Official", NotificationManager.IMPORTANCE_HIGH)
            ch.setSound(sound, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build())
            ch.enableVibration(true); ch.vibrationPattern = longArrayOf(0, 250, 120, 450)
            nm.createNotificationChannel(ch)
        }
        val n = Notification.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("TOOBIT OFFICIAL • POST BARU")
            .setContentText(if (text.isNotEmpty()) text else "Ada post baru dari @Toobit_official")
            .setStyle(Notification.BigTextStyle().bigText(if (text.isNotEmpty()) text else combined))
            .setOngoing(true).setAutoCancel(false).setOnlyAlertOnce(false)
            .setCategory(Notification.CATEGORY_EVENT).setColor(0xFF00A884.toInt()).build()
        nm.notify(7001, n)
    }
}
