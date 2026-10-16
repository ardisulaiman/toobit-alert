package com.zyronstudio.toobitalert

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {
    private val bg = Color.rgb(247, 250, 249)
    private val dark = Color.rgb(16, 21, 21)
    private val teal = Color.rgb(8, 167, 125)
    private val muted = Color.rgb(104, 123, 118)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = dark
        setContentView(buildScreen())
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    private fun text(value: String, size: Float, color: Int, bold: Boolean = false): TextView =
        TextView(this).apply {
            this.text = value; textSize = size; setTextColor(color)
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

    private fun rounded(color: Int, radius: Int, stroke: Int? = null): GradientDrawable =
        GradientDrawable().apply {
            setColor(color); cornerRadius = dp(radius).toFloat()
            stroke?.let { setStroke(dp(1), it) }
        }

    private fun buildScreen(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setBackgroundColor(bg)
            setPadding(0, 0, 0, dp(18))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(22), dp(20), dp(24)); setBackgroundColor(dark)
        }
        header.addView(text("TOOBIT ALERT", 11f, Color.rgb(117, 216, 195), true))
        header.addView(text("Official posts, always noticed.", 25f, Color.WHITE, true).apply { setPadding(0, dp(7), 0, dp(4)) })
        header.addView(text("Custom alert untuk setiap post dari @Toobit_official", 13f, Color.rgb(170, 185, 181)))
        root.addView(header)

        val status = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14)); background = rounded(Color.rgb(227, 247, 241), 14, Color.rgb(184, 232, 219))
        }
        val dot = TextView(this).apply { text = "●"; textSize = 18f; setTextColor(teal); setPadding(0, 0, dp(10), 0) }
        val statusCopy = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        statusCopy.addView(text("Monitoring aktif", 14f, Color.rgb(19, 32, 30), true))
        statusCopy.addView(text("Notification access tersambung", 12f, Color.rgb(85, 113, 106)))
        status.addView(dot); status.addView(statusCopy)
        root.addView(status, LinearLayout.LayoutParams(-1, -2).apply { setMargins(dp(16), dp(18), dp(16), dp(8)) })

        root.addView(text("PREVIEW NOTIFIKASI", 12f, muted, true).apply { letterSpacing = .08f }, LinearLayout.LayoutParams(-1, -2).apply { setMargins(dp(16), dp(4), dp(16), dp(9)) })

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(dp(15), dp(15), dp(15), dp(15)); background = rounded(Color.WHITE, 16, Color.rgb(220, 231, 227))
        }
        val top = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val avatar = TextView(this).apply { text = "T"; textSize = 17f; gravity = Gravity.CENTER; setTextColor(Color.WHITE); typeface = android.graphics.Typeface.DEFAULT_BOLD; background = rounded(teal, 12) }
        top.addView(avatar, LinearLayout.LayoutParams(dp(37), dp(37)))
        val identity = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(10), 0, 0, 0) }
        identity.addView(text("Toobit Official", 14f, Color.rgb(19, 32, 30), true))
        identity.addView(text("@Toobit_official · X", 12f, Color.rgb(123, 139, 135)))
        top.addView(identity, LinearLayout.LayoutParams(0, -2, 1f))
        top.addView(text("PINNED", 9f, Color.rgb(8, 123, 97), true).apply { setPadding(dp(6), dp(4), dp(6), dp(4)); background = rounded(Color.rgb(227, 247, 241), 6) })
        card.addView(top)
        card.addView(text("TOOBIT OFFICIAL • POST BARU\nNew update from Toobit. Tap to view the post on X.", 14f, Color.rgb(40, 55, 51)).apply { setPadding(0, dp(13), 0, dp(12)) })

        val tags = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        listOf("Suara khusus", "Getar kuat", "Tidak hilang").forEach { label ->
            tags.addView(text(label, 11f, Color.rgb(83, 101, 96)).apply { setPadding(dp(9), dp(7), dp(9), dp(7)); background = rounded(Color.rgb(240, 244, 242), 8) }, LinearLayout.LayoutParams(-2, -2).apply { setMargins(0, 0, dp(7), 0) })
        }
        card.addView(tags)
        val sound = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(0, dp(12), 0, 0) }
        sound.addView(text("Toobit alert tone", 12f, Color.rgb(19, 32, 30), true), LinearLayout.LayoutParams(0, -2, 1f))
        sound.addView(text("▂▅█▆▇▂", 18f, teal, true))
        card.addView(sound)
        root.addView(card, LinearLayout.LayoutParams(-1, -2).apply { setMargins(dp(16), 0, dp(16), dp(8)) })

        val test = Button(this).apply {
            text = "Test notifikasi"; textSize = 14f; setTextColor(Color.WHITE); isAllCaps = false; typeface = android.graphics.Typeface.DEFAULT_BOLD
            background = rounded(teal, 13); setOnClickListener { text = "Test alert terkirim"; postDelayed({ text = "Test notifikasi" }, 1800) }
        }
        root.addView(test, LinearLayout.LayoutParams(-1, dp(50)).apply { setMargins(dp(16), dp(8), dp(16), dp(22)) })
        root.addView(text("Terpicu dari notifikasi aplikasi X", 11f, Color.rgb(138, 153, 149)).apply { gravity = Gravity.CENTER })
        return root
    }
}
