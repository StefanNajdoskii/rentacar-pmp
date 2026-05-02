package com.rentacar.messaging

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rentacar.R
import com.rentacar.data.remote.FirestoreRepository
import com.rentacar.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RentACarMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                FirestoreRepository().saveFcmToken(userId, token)
            } catch (_: Exception) { /* non-critical */ }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: getString(R.string.app_name)

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: ""

        val channelId = remoteMessage.data["channel"]
            ?: NotificationHelper.CHANNEL_BOOKINGS

        showNotification(title, body, channelId)
    }

    private fun showNotification(title: String, body: String, channelId: String) {
        NotificationHelper.createChannels(this)

        android.app.PendingIntent.getActivity(
            this, 0,
            android.content.Intent(this, com.rentacar.MainActivity::class.java).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
            },
            android.app.PendingIntent.FLAG_ONE_SHOT or android.app.PendingIntent.FLAG_IMMUTABLE
        ).let { pi ->
            val notification = androidx.core.app.NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .build()

            (getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager)
                .notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}
