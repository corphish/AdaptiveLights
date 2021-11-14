package com.avinabadalal.adaptivelights.core

import android.graphics.Color
import androidx.annotation.ColorInt

// As per smart things API, it only supports hue and saturation for my light.
data class ColorConfig(
    /**
     * Hue.
     */
    val hue: Int,

    /**
     * Saturation
     */
    val saturation: Int,

    /**
     * RGB color int.
     * This parameter is not supported by smartthings API, but is present to show app level
     * UI changes.
     */
    @ColorInt val color: Int = Color.WHITE,
)
