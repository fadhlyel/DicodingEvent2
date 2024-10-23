package com.example.dicodingevent.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.dicodingevent.R
import com.example.dicodingevent.data.remote.response.ListEventsItem
import com.example.dicodingevent.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "dicoding_channel"
    }

    override fun doWork(): Result {
        return try {
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }
            val nearestEvent = getNearestEvent()
            if (nearestEvent != null) {
                showNotification(nearestEvent)
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun getNearestEvent(): ListEventsItem? {
        return runBlocking {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiConfig.getApiService().getNearestActiveEvent()
                }
                if (!response.error) {
                    response.listEvents.firstOrNull()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun showNotification(event: ListEventsItem) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Event Recomendation: ${event.name}")
            .setContentText("Mulai pada ${event.beginTime}")
            .setSmallIcon(R.drawable.baseline_favorite_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }
}