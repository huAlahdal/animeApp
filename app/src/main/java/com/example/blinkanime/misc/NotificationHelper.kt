package com.example.blinkanime.misc

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.blinkanime.MainActivity
import com.example.blinkanime.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class NotificationHelper(private val context: Context, private val channelId: String, private val channelName: String, private val channelDescription: String) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun sendNotification(title: String, message: String, image: String? = null) {
        val notificationId = System.currentTimeMillis().toInt()

        val notificationLayout = try {
            RemoteViews(context.packageName, R.layout.notification_small)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        val notificationLayoutExpanded = try {
            RemoteViews(context.packageName, R.layout.notification_large)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        notificationLayout.setTextViewText(R.id.notification_title, title)
        notificationLayout.setTextViewText(R.id.notification_message, message)

        val customNotification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setAutoCancel(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java).apply {
                        putExtra("fragment", channelId)
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

        // set content Intent based on channel ID



        if (image != null) {
            customNotification.setCustomBigContentView(notificationLayoutExpanded);
            notificationLayoutExpanded.setTextViewText(R.id.notification_title, title)
            notificationLayoutExpanded.setTextViewText(R.id.notification_message, message)
            Picasso.get()
                .load(image)
                .into(notificationLayoutExpanded, R.id.notification_image, intArrayOf(notificationId), object :
                    Callback {
                    override fun onSuccess() {
                        notificationManager.notify(notificationId, customNotification.build())
                    }

                    override fun onError(e: Exception?) {
                        // Handle error
                        notificationManager.notify(notificationId, customNotification.build())
                    }
                })
        } else {
            notificationManager.notify(notificationId, customNotification.build())
        }
    }
}
