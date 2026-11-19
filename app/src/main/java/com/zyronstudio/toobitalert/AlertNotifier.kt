package com.zyronstudio.toobitalert

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build

object AlertNotifier {
    // The old channel has an immutable sound. This new silent channel lets the
    // app play the selected file at the selected volume for every alert.
    private const val CHANNEL = "toobit_official_alert_v3"
    private const val CHANNEL_NAME = "Toobit Official Alerts"
    private val players = mutableSetOf<MediaPlayer>()

    fun show(
        context: Context,
        title: String,
        body: String,
        id: Int = (System.currentTimeMillis() and 0x7fffffff).toInt()
    ) {
        createChannel(context)
        playAlertSound(context.applicationContext)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL)
        } else {
            Notification.Builder(context).setSound(null).setVibrate(VIBRATION)
        }
        val notification = builder
            .setSmallIcon(android.R.drawable.ic_dialog_info)
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

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Alert post baru dari akun resmi Toobit"
            enableVibration(true)
            vibrationPattern = VIBRATION
            setSound(null, null)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        manager.createNotificationChannel(channel)
    }

    private fun playAlertSound(context: Context) {
        val volume = AlertSettings.volume(context) / 100f
        if (volume <= 0f) return
        val customUri = AlertSettings.soundUri(context)
        if (!startPlayer(context, customUri ?: defaultSound(context), volume) && customUri != null) {
            AlertSettings.resetSound(context)
            startPlayer(context, defaultSound(context), volume)
        }
    }

    private fun startPlayer(context: Context, sound: Uri, volume: Float): Boolean = try {
        val player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(context, sound)
            setVolume(volume, volume)
            setOnCompletionListener { releasePlayer(it) }
            setOnErrorListener { failed, _, _ ->
                releasePlayer(failed)
                true
            }
            prepare()
        }
        synchronized(players) { players.add(player) }
        player.start()
        true
    } catch (_: Exception) {
        false
    }

    private fun releasePlayer(player: MediaPlayer) {
        synchronized(players) { players.remove(player) }
        runCatching { player.release() }
    }

    private fun defaultSound(context: Context) =
        Uri.parse("android.resource://${context.packageName}/${R.raw.toobit_alert}")

    private val VIBRATION = longArrayOf(0, 300, 120, 500, 120, 700)
}

