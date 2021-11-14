package com.avinabadalal.adaptivelights.core

import android.graphics.Bitmap
import android.graphics.Color
import androidx.palette.graphics.Palette

object BitmapUtils {

    /**
     * Generates dominant/vibrant color from the bitmap. Color generation is async.
     *
     * @param bitmap Album art bitmap.
     * @param andThen [ColorConfig] containing the generated dominant/vibrant color. White is used if
     *                dominant/vibrant color could not be determined.
     */
    fun dominantColorFrom(bitmap: Bitmap, andThen: (ColorConfig) -> Unit) {
        Palette.from(bitmap).generate {
            if (it != null) {
                val dom = it.getDominantColor(Color.WHITE)
                val v = it.getVibrantColor(Color.WHITE)
                val l = it.getLightVibrantColor(Color.WHITE)
                val darkVib = it.getDarkVibrantColor(Color.WHITE)
                val color = getPreferredColor(intArrayOf(
                    dom, v, l ,darkVib
                ))

                val hsv = FloatArray(3)
                Color.colorToHSV(color, hsv)

                andThen(
                    ColorConfig(
                        ((hsv[0]/360) * 100).toInt(),
                        (hsv[1] * 100).toInt(),
                        color
                    )
                )
            }
        }
    }

    private fun getPreferredColor(colors: IntArray): Int {
        for (color in colors) {
            if (color != Color.WHITE) {
                return color
            }
        }

        return Color.WHITE
    }
}