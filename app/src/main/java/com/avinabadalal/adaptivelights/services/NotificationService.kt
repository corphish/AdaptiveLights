package com.avinabadalal.adaptivelights.services

import android.annotation.SuppressLint
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.util.Log
import com.avinabadalal.adaptivelights.core.BitmapUtils
import com.avinabadalal.adaptivelights.core.LightsSession

// Service that listens for notification updates.
// Notification access must be enabled manually for this app.
class NotificationService : NotificationListenerService() {

    // Supported list of media players.
    private val supportedPackages = listOf(
        "com.spotify.music",
    )

    @SuppressLint("ServiceCast")
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        // Package name check to filter out other notifications.
        // You can remove this check to support all media players.
        // However, this will be hit every time a notification comes, including non-media
        // notification.
        if (sbn?.packageName !in supportedPackages) {
            return
        }

        val mm: MediaSessionManager = this.getSystemService(
            Context.MEDIA_SESSION_SERVICE
        ) as MediaSessionManager


        val controllers: List<MediaController> = mm.getActiveSessions(
            ComponentName(
                this,
                NotificationService::class.java
            )
        )

        if (controllers.isNotEmpty()) {
            for (controller in controllers) {
                // Consider the media that is being played
                if (controller.playbackState?.state == PlaybackState.STATE_PLAYING) {
                    val metadata = controller.metadata
                    if (metadata != null) {
                        // Get the album art of the current media.
                        val bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)

                        // Generate the dominant color for the given bitmap.
                        BitmapUtils.dominantColorFrom(bitmap) {
                            // Update the light with generated dominant color.
                            LightsSession.updateColors(this, it)
                        }
                    }
                }
            }
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()

        LightsSession.create(this)
    }
}