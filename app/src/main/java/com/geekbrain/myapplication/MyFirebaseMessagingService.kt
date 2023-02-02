package com.geekbrain.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "myFirebaseMessService"

    companion object{
        private const val PUSH_KEY_TITLE = "title"
        private const val PUSH_KEY_MESSAGE = "message"
        private const val PUSH_KEY_INFO = "info"
        private const val CHANNEL_ID = "channel_id"
        private const val NOTIFICATION_ID = 37
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.v(TAG, "onNewToken: $token")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.v(TAG, "onMessageReceived")

        val remoteMessageData = remoteMessage.data

        if(remoteMessageData.isNotEmpty()){
            handleDataMessage(remoteMessageData.toMap())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleDataMessage(data: Map<String, String>){
        Log.v(TAG, "handleDataMessage: ")

        val title = data[PUSH_KEY_TITLE]
        val message = data[PUSH_KEY_MESSAGE]
        val info = data[PUSH_KEY_INFO]
        if(!title.isNullOrBlank() && !message.isNullOrBlank()) {
            if (info != null) {
                showNotification(title, message, info)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(title: String, message: String, info: String){
        Log.v(TAG, "showNotification: ")
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_baseline_emergency_24)
            setContentTitle(title)
            setContentText(message)
            setContentInfo(info)

            color = getColor(R.color.notification)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        createNotificationChannel(notificationManager)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){

        Log.v(TAG, "createNotificationChannel: ")

        val name = "Channel name"
        val descriptionText = "Channel description"

        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

}