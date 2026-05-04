package com.rentacar.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.rentacar.MainActivity
import com.rentacar.R

object NotificationHelper {

    const val CHANNEL_BOOKINGS = "rentacar_bookings"
    const val CHANNEL_REMINDERS = "rentacar_reminders"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_BOOKINGS,
                    context.getString(R.string.notification_channel_bookings),
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_REMINDERS,
                    context.getString(R.string.notification_channel_reminders),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    fun showBookingConfirmation(context: Context, carName: String, pickupDate: String) {
        show(
            context = context,
            channelId = CHANNEL_BOOKINGS,
            title = context.getString(R.string.notification_booking_confirmed_title),
            body = context.getString(R.string.notification_booking_confirmed_body, carName, pickupDate),
            id = System.currentTimeMillis().toInt()
        )
    }

    fun showReturnReminder(context: Context, carName: String, returnDate: String) {
        show(
            context = context,
            channelId = CHANNEL_REMINDERS,
            title = context.getString(R.string.notification_reminder_title),
            body = context.getString(R.string.notification_reminder_body, carName, returnDate),
            id = (System.currentTimeMillis() + 1).toInt()
        )
    }

    private fun show(
        context: Context,
        channelId: String,
        title: String,
        body: String,
        id: Int
    ) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pi = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        nm.notify(id, notification)
    }
}
