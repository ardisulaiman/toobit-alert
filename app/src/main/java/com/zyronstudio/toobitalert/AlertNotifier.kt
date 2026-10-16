package com.zyronstudio.toobitalert

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build

object AlertNotifier {
    private const val CHANNEL = "toobit_official_alert_v2"
    private const val CHANNEL_NAME = "Toobit Official Alerts"

    fun show(context: Context, title: String, body: String, id: Int = (System.currentTimeMillis() and 0x7fffffff).toInt()) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val sound = Uri.parse("android.resource://${context.packageName}/${com.zyronstudio.toobitalert.R.raw.toobit_alert}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Alert post baru dari @Toobit_official"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 120, 500, 120, 700)
                setSound(sound, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build())
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            manager.createNotificationChannel(channel)
        }
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL)
        } else {
            Notification.Builder(context).setSound(sound).setVibrate(longArrayOf(0, 300, 120, 500, 120, 700))
        }.setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(Notification.BigTextStyle().bigText(body))
            .setColor(0xFF00A884.toInt())
            .setCategory(Notification.CATEGORY_EVENT)
            .setPriority(Notification.PRIORITY_MAX)
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(false)
            .build()
        manager.notify(id, notification)
    }
}
