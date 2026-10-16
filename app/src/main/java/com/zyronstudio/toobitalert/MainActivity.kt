package com.zyronstudio.toobitalert

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {
    private val bg = Color.rgb(247, 250, 249); private val dark = Color.rgb(16, 21, 21); private val teal = Color.rgb(8, 167, 125); private val muted = Color.rgb(104, 123, 118)
    private lateinit var statusText: TextView
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); window.statusBarColor = dark; if (Build.VERSION.SDK_INT >= 33) requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 20); setContentView(buildScreen()) }
    override fun onResume() { super.onResume(); if (::statusText.isInitialized) updateStatus() }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun txt(s: String, z: Float, c: Int, b: Boolean = false) = TextView(this).apply { text=s; textSize=z; setTextColor(c); if(b) setTypeface(typeface, 1) }
    private fun bg(c: Int, r: Int, stroke: Int?=null) = GradientDrawable().apply { setColor(c); cornerRadius=dp(r).toFloat(); stroke?.let { setStroke(dp(1),it) } }
    private fun buildScreen(): View {
        val root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setBackgroundColor(bg); setPadding(0,0,0,dp(18)) }
        val head=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(20),dp(22),dp(20),dp(24)); setBackgroundColor(dark) }
        head.addView(txt("TOOBIT ALERT",11f,Color.rgb(117,216,195),true)); head.addView(txt("Official posts, always noticed.",25f,Color.WHITE,true).apply{setPadding(0,dp(7),0,dp(4))}); head.addView(txt("Custom alert untuk setiap post dari @Toobit_official",13f,Color.rgb(170,185,181))); root.addView(head)
        val status=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setPadding(dp(14),dp(14),dp(14),dp(14));background=bg(Color.rgb(227,247,241),14,Color.rgb(184,232,219))}; status.addView(txt("●",18f,teal).apply{setPadding(0,0,dp(10),0)}); statusText=txt("",14f,Color.rgb(19,32,30),true); status.addView(statusText); root.addView(status,LinearLayout.LayoutParams(-1,-2).apply{setMargins(dp(16),dp(18),dp(16),dp(8))}); updateStatus()
        root.addView(txt("PREVIEW NOTIFIKASI",12f,muted,true).apply{letterSpacing=.08f},LinearLayout.LayoutParams(-1,-2).apply{setMargins(dp(16),dp(4),dp(16),dp(9))})
        val card=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(15),dp(15),dp(15),dp(15));background=bg(Color.WHITE,16,Color.rgb(220,231,227))}; val top=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}; top.addView(txt("T",17f,Color.WHITE,true).apply{gravity=Gravity.CENTER;background=bg(teal,12)},LinearLayout.LayoutParams(dp(37),dp(37))); val ident=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(10),0,0,0)};ident.addView(txt("Toobit Official",14f,Color.rgb(19,32,30),true));ident.addView(txt("@Toobit_official · X",12f,Color.rgb(123,139,135)));top.addView(ident,LinearLayout.LayoutParams(0,-2,1f));top.addView(txt("PINNED",9f,Color.rgb(8,123,97),true).apply{setPadding(dp(6),dp(4),dp(6),dp(4));background=bg(Color.rgb(227,247,241),6)});card.addView(top);card.addView(txt("TOOBIT OFFICIAL • POST BARU\nNew update from Toobit. Tap to view the post on X.",14f,Color.rgb(40,55,51)).apply{setPadding(0,dp(13),0,dp(12))}); val tags=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL};listOf("Suara khusus","Getar kuat","Tidak hilang").forEach{tags.addView(txt(it,11f,Color.rgb(83,101,96)).apply{setPadding(dp(9),dp(7),dp(9),dp(7));background=bg(Color.rgb(240,244,242),8)},LinearLayout.LayoutParams(-2,-2).apply{setMargins(0,0,dp(7),0)})};card.addView(tags);card.addView(txt("Toobit alert tone                                      ▂▅█▆▇▂",12f,Color.rgb(19,32,30),true).apply{setPadding(0,dp(12),0,0)});root.addView(card,LinearLayout.LayoutParams(-1,-2).apply{setMargins(dp(16),0,dp(16),dp(8))})
        val access=Button(this).apply{text="Aktifkan akses notifikasi";isAllCaps=false;setOnClickListener{startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))}};root.addView(access,LinearLayout.LayoutParams(-1,dp(48)).apply{setMargins(dp(16),dp(8),dp(16),dp(4))})
        val test=Button(this).apply{text="Test notifikasi";textSize=14f;setTextColor(Color.WHITE);isAllCaps=false;typeface=android.graphics.Typeface.DEFAULT_BOLD;background=bg(teal,13);setOnClickListener{AlertNotifier.show(this@MainActivity,"TOOBIT OFFICIAL • TEST","Tes berhasil: suara, getar, dan notifikasi persistent aktif.",9999);text="Test alert terkirim";postDelayed({text="Test notifikasi"},1800)}};root.addView(test,LinearLayout.LayoutParams(-1,dp(50)).apply{setMargins(dp(16),dp(4),dp(16),dp(22))});root.addView(txt("Terpicu dari notifikasi aplikasi X",11f,Color.rgb(138,153,149)).apply{gravity=Gravity.CENTER});return root
    }
    private fun updateStatus(){val enabled= getSystemService(NotificationManager::class.java).activeNotifications != null && isNotificationListenerEnabled(); statusText.text=if(enabled) "Monitoring aktif\nNotification access tersambung" else "Belum aktif\nTekan tombol akses notifikasi"}
    private fun isNotificationListenerEnabled(): Boolean { val flat=Settings.Secure.getString(contentResolver,"enabled_notification_listeners") ?: return false; return flat.contains(ComponentName(this,ToobitNotificationListener::class.java).flattenToString()) }
}
