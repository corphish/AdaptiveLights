package com.avinabadalal.adaptivelights

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.res.ColorStateList
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.avinabadalal.adaptivelights.services.NotificationService
import com.avinabadalal.adaptivelights.core.LightsSession
import com.corphish.colors.ktx.ColorSet
import com.corphish.colors.ktx.ColorUtils
import com.corphish.colors.ktx.darkerShade

// Main activity
// Basically reports the current media and the color applied.
class MainActivity : AppCompatActivity() {
    // Views
    private lateinit var backLayout: ConstraintLayout
    private lateinit var icon: AppCompatImageView
    private lateinit var heading: TextView
    private lateinit var desc: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serviceIntent = Intent(this, NotificationService::class.java)
        startService(serviceIntent)

        backLayout = findViewById(R.id.back)
        icon = findViewById(R.id.lightIcon)
        heading = findViewById(R.id.heading)
        desc = findViewById(R.id.desc)

        updateUI(null)
        registerUIUpdateListener()
    }

    private fun registerUIUpdateListener() {
        LightsSession.registerUIUpdateDispatcher {
            updateUI(it)
        }
    }

    private fun updateUI(color: Int?) {
        val set = ColorSet.of(color ?: resources.getColor(R.color.purple_500, theme))

        // App UI
        backLayout.setBackgroundColor(set.backgroundColor)
        icon.imageTintList = ColorStateList.valueOf(set.foregroundMutedColor)
        heading.setTextColor(set.foregroundColor)
        desc.setTextColor(set.foregroundMutedColor)

        // System UI
        window.statusBarColor = set.backgroundColor.darkerShade
        window.navigationBarColor = set.backgroundColor

        if (ColorUtils.isColorDark(set.backgroundColor)) {
            window.insetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        LightsSession.unregisterUIUpdateDispatcher()
    }
}