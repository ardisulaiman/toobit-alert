package com.zyronstudio.toobitalert

import android.app.*
import android.content.*
import android.os.Bundle
import android.provider.Settings
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val open = Button(this).apply { text = "Aktifkan akses notifikasi"; setOnClickListener { startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) } }
        val info = TextView(this).apply { text = "Toobit Alert\n\nAktifkan akses notifikasi, lalu pastikan notifikasi post @Toobit_official aktif di aplikasi X."; textSize = 18f; setPadding(48, 64, 48, 32) }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; addView(info); addView(open) }
        setContentView(box)
    }
}
