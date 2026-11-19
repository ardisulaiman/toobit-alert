package com.zyronstudio.toobitalert

import android.content.Context
import android.net.Uri

object AlertSettings {
    private const val PREFS = "toobit_alert_settings"
    private const val SOUND_URI = "sound_uri"
    private const val SOUND_NAME = "sound_name"
    private const val VOLUME = "volume"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun soundUri(context: Context): Uri? =
        prefs(context).getString(SOUND_URI, null)?.let(Uri::parse)

    fun soundName(context: Context): String =
        prefs(context).getString(SOUND_NAME, null) ?: "Toobit alert bawaan"

    fun setSound(context: Context, uri: Uri, name: String) {
        prefs(context).edit()
            .putString(SOUND_URI, uri.toString())
            .putString(SOUND_NAME, name)
            .apply()
    }

    fun resetSound(context: Context) {
        prefs(context).edit().remove(SOUND_URI).remove(SOUND_NAME).apply()
    }

    fun volume(context: Context): Int = prefs(context).getInt(VOLUME, 100).coerceIn(0, 100)

    fun setVolume(context: Context, volume: Int) {
        prefs(context).edit().putInt(VOLUME, volume.coerceIn(0, 100)).apply()
    }
}

