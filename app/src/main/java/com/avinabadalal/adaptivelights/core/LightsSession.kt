package com.avinabadalal.adaptivelights.core

import android.content.Context

/**
 * Lights session that talks with the LightsApi to update light color.
 */
object LightsSession {
    // States
    const val STATE_UNREADY = 0
    const val STATE_READY = 1

    private var defaultColorConfig: ColorConfig = ColorConfig(100, 100)

    private var lastAppliedConfig = defaultColorConfig
    var state = STATE_UNREADY

    // UI updater
    private var uiUpdateDispatcher: (Int?) -> Unit = {  }

    /**
     * Updates the color of the light with given color config.
     *
     * @param context Context.
     * @param config Color config.
     */
    fun updateColors(context: Context, config: ColorConfig) {
        if (state == STATE_UNREADY) return
        if (config == lastAppliedConfig) return

        LightsApi.updateCurrentConfig(context, config)
        uiUpdateDispatcher(config.color)
        lastAppliedConfig = config
    }

    /**
     * Reverts the light back to its default state recorded at the start of
     * this session.
     *
     * @param context Context.
     */
    fun revertToDefault(context: Context) {
        updateColors(context, defaultColorConfig)
    }

    /**
     * Registers a dispatcher that is fired when some change happens in this session.
     * The changes are then fired to the app UI via this dispatcher.
     */
    fun registerUIUpdateDispatcher(dispatcher: (Int?) -> Unit) {
        this.uiUpdateDispatcher = dispatcher
        if (defaultColorConfig != lastAppliedConfig) {
            uiUpdateDispatcher(lastAppliedConfig.color)
        }
    }

    /**
     * Unregisters the UI update dispatcher.
     */
    fun unregisterUIUpdateDispatcher() {
        this.uiUpdateDispatcher = { }
    }

    /**
     * Creates a new light session, and records the default value for this session.
     *
     * @param context Context.
     */
    fun create(context: Context) {
        LightsApi.getCurrentConfig(context) {
            if (it != null) {
                defaultColorConfig = it
            }
        }

        state = STATE_READY
        uiUpdateDispatcher(null)
    }

    /**
     * Destroys session.
     */
    fun destroy() {
        state = STATE_UNREADY
    }
}