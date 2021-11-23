package com.example.to_doapp.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.to_doapp.R
import com.example.to_doapp.ui.MainActivity
import com.example.to_doapp.utils.Constants
import java.util.*

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val todoTitle = intent.getStringExtra("todoTitle")
        val todoId = Random().nextInt(System.currentTimeMillis().toInt())
//        val todoId = intent.getIntExtra("todoId", System.currentTimeMillis().toInt())

        val allTodoIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, todoId, allTodoIntent, 0)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                "Channel 1",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "This is Channel 1"
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle("To-Do App")
            .setContentText("Remainder for $todoTitle")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationCompat = NotificationManagerCompat.from(context)
        notificationCompat.notify(todoId, builder.build())

    }

}