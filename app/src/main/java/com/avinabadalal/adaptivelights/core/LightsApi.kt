package com.avinabadalal.adaptivelights.core

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.avinabadalal.adaptivelights.BuildConfig

/**
 * Makes the API calls to SmartThings API.
 */
object LightsApi {

    /**
     * Gets the current color config of the light.
     *
     * @param context Context.
     * @param andThen Callback fired with the current light config. Can be null if current config is
     *                not available.
     */
    fun getCurrentConfig(context: Context, andThen: (ColorConfig?) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val url = "https://api.smartthings.com/v1/devices/${BuildConfig.ST_DEVICE_ID}/components/main/capabilities/colorControl/status"

        // Request a string response from the provided URL.
        val request = object: JsonObjectRequest(Method.GET, url, null,
            Response.Listener { response ->
                andThen(
                    ColorConfig(
                        response.getJSONObject("hue").getInt("value"),
                        response.getJSONObject("saturation").getInt("value"),
                    )
                )
            },
            Response.ErrorListener { andThen(null) })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${BuildConfig.ST_ACCESS_KEY}"
                return headers
            }
        }

        queue.add(request)
    }

    /**
     * Updates the light with the given color config.
     *
     * @param context Context.
     * @param colorConfig Color config data to update the light with.
     */
    fun updateCurrentConfig(context: Context, colorConfig: ColorConfig) {
        val queue = Volley.newRequestQueue(context)
        val url = "https://api.smartthings.com/v1/devices/${BuildConfig.ST_DEVICE_ID}/commands"

        // Request a string response from the provided URL.
        val request = object: JsonObjectRequest(Method.POST, url, null,
            Response.Listener { },
            Response.ErrorListener { })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${BuildConfig.ST_ACCESS_KEY}"
                return headers
            }

            override fun getBodyContentType() = "text/plain"

            override fun getBody(): ByteArray {
                return "[{\"component\":\"main\",\"capability\":\"colorControl\",\"command\":\"setColor\", arguments: [{hue: ${colorConfig.hue}, saturation: ${colorConfig.saturation}}]  }]".toByteArray()
            }
        }

        queue.add(request)
    }
}