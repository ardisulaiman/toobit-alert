package com.zyronstudio.toobitalert

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : Activity() {
    private val bgColor = Color.rgb(247, 250, 249)
    private val dark = Color.rgb(16, 21, 21)
    private val teal = Color.rgb(8, 167, 125)
    private val muted = Color.rgb(104, 123, 118)
    private lateinit var statusText: TextView
    private lateinit var soundNameText: TextView
    private lateinit var volumeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = dark
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 20)
        }
        setContentView(buildScreen())
    }

    override fun onResume() {
        super.onResume()
        if (::statusText.isInitialized) updateStatus()
        if (::soundNameText.isInitialized) soundNameText.text = AlertSettings.soundName(this)
    }

    @Deprecated("Kept for the system audio document picker")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != PICK_SOUND || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        runCatching {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        AlertSettings.setSound(this, uri, displayName(uri))
        soundNameText.text = AlertSettings.soundName(this)
        AlertNotifier.show(this, "SUARA NOTIF DIPILIH", "Preview suara baru Toobit Alert", PREVIEW_NOTIFICATION_ID)
    }

    private fun buildScreen(): ScrollView {
        val scroll = ScrollView(this).apply { setBackgroundColor(bgColor) }
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(root)

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(22), dp(20), dp(22))
            setBackgroundColor(dark)
        }
        header.addView(txt("TOOBIT ALERT", 11f, Color.rgb(117, 216, 195), true))
        header.addView(txt("Official posts, always noticed.", 25f, Color.WHITE, true).apply {
            setPadding(0, dp(7), 0, dp(4))
        })
        header.addView(txt("Notif resmi Toobit dari semua platform", 13f, Color.rgb(170, 185, 181)))
        root.addView(header)

        val status = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = rounded(Color.rgb(227, 247, 241), 14, Color.rgb(184, 232, 219))
        }
        status.addView(txt("●", 18f, teal).apply { setPadding(0, 0, dp(10), 0) })
        statusText = txt("", 14f, Color.rgb(19, 32, 30), true)
        status.addView(statusText)
        root.addView(status, margins(dp(16), dp(18), dp(16), dp(8)))
        updateStatus()

        root.addView(sectionTitle("SUMBER NOTIFIKASI"), margins(dp(16), dp(8), dp(16), dp(9)))
        val platformCard = card()
        platformCard.addView(txt("X  •  Instagram  •  TikTok  •  YouTube\nThreads  •  Facebook  •  Reddit", 14f, dark, true))
        platformCard.addView(txt("Instagram & Threads: @toobitofficial\nTikTok, X & YouTube: @Toobit_official\nReddit: r/toobit\nFacebook: halaman dan grup resmi", 12f, muted).apply {
            setPadding(0, dp(9), 0, 0)
        })
        platformCard.addView(txt("Follow akun resminya lalu aktifkan notifikasi post di tiap aplikasi.", 12f, muted).apply {
            setPadding(0, dp(9), 0, 0)
        })
        root.addView(platformCard, margins(dp(16), 0, dp(16), dp(8)))

        root.addView(sectionTitle("SUARA NOTIFIKASI"), margins(dp(16), dp(8), dp(16), dp(9)))
        val soundCard = card()
        soundNameText = txt(AlertSettings.soundName(this), 14f, dark, true)
        soundCard.addView(soundNameText)
        soundCard.addView(txt("Pilih file audio dari HP, atau gunakan suara bawaan.", 12f, muted).apply {
            setPadding(0, dp(4), 0, dp(10))
        })
        val soundButtons = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        soundButtons.addView(actionButton("Pilih suara") { pickSound() }, LinearLayout.LayoutParams(0, dp(46), 1f).apply {
            setMargins(0, 0, dp(6), 0)
        })
        soundButtons.addView(actionButton("Pakai bawaan") {
            AlertSettings.resetSound(this)
            soundNameText.text = AlertSettings.soundName(this)
            AlertNotifier.show(this, "SUARA BAWAAN", "Preview suara bawaan Toobit Alert", PREVIEW_NOTIFICATION_ID)
        }, LinearLayout.LayoutParams(0, dp(46), 1f).apply { setMargins(dp(6), 0, 0, 0) })
        soundCard.addView(soundButtons)

        val savedVolume = AlertSettings.volume(this)
        volumeText = txt("Volume alert: $savedVolume%", 13f, dark, true).apply { setPadding(0, dp(18), 0, dp(4)) }
        soundCard.addView(volumeText)
        soundCard.addView(SeekBar(this).apply {
            max = 100
            progress = savedVolume
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    volumeText.text = "Volume alert: $progress%"
                    if (fromUser) AlertSettings.setVolume(this@MainActivity, progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    AlertNotifier.show(this@MainActivity, "TEST VOLUME", "Volume alert ${seekBar?.progress ?: 0}%", PREVIEW_NOTIFICATION_ID)
                }
            })
        })
        soundCard.addView(txt("Mengikuti volume notifikasi utama HP.", 11f, muted))
        root.addView(soundCard, margins(dp(16), 0, dp(16), dp(8)))

        root.addView(actionButton("Aktifkan akses notifikasi") {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }, margins(dp(16), dp(12), dp(16), dp(4), dp(50)))
        root.addView(actionButton("Test notifikasi") {
            AlertNotifier.show(this, "TOOBIT • TEST ALERT", "Suara dan volume siap dipakai untuk semua akun resmi Toobit.", PREVIEW_NOTIFICATION_ID)
        }, margins(dp(16), dp(4), dp(16), dp(24), dp(50)))

        return scroll
    }

    private fun pickSound() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        @Suppress("DEPRECATION")
        startActivityForResult(intent, PICK_SOUND)
    }

    private fun displayName(uri: Uri): String {
        var cursor: Cursor? = null
        return try {
            cursor = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            } else {
                uri.lastPathSegment ?: "Suara custom"
            }
        } catch (_: Exception) {
            uri.lastPathSegment ?: "Suara custom"
        } finally {
            cursor?.close()
        }
    }

    private fun updateStatus() {
        statusText.text = if (isNotificationListenerEnabled()) {
            "Monitoring aktif\n7 platform resmi Toobit"
        } else {
            "Belum aktif\nTekan tombol akses notifikasi"
        }
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners") ?: return false
        return flat.contains(ComponentName(this, ToobitNotificationListener::class.java).flattenToString())
    }

    private fun card() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(15), dp(15), dp(15), dp(15))
        background = rounded(Color.WHITE, 16, Color.rgb(220, 231, 227))
    }

    private fun sectionTitle(value: String) = txt(value, 12f, muted, true).apply { letterSpacing = .08f }

    private fun actionButton(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        isAllCaps = false
        setTextColor(Color.WHITE)
        background = rounded(teal, 12)
        setOnClickListener { action() }
    }

    private fun txt(value: String, size: Float, color: Int, bold: Boolean = false) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(typeface, 1)
    }

    private fun rounded(color: Int, radius: Int, strokeColor: Int? = null) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
        if (strokeColor != null) setStroke(dp(1), strokeColor)
    }

    private fun margins(left: Int, top: Int, right: Int, bottom: Int, height: Int = -2) =
        LinearLayout.LayoutParams(-1, height).apply { setMargins(left, top, right, bottom) }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    companion object {
        private const val PICK_SOUND = 1001
        private const val PREVIEW_NOTIFICATION_ID = 9999
    }
}

