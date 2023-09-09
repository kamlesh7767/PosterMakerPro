package com.garudpuran.postermakerpro.firebasemessaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle incoming FCM message here
        // You can extract the notification data and show a notification using NotificationManager
    }

    override fun onNewToken(token: String) {
        // Handle token refresh here
        // Send the new token to your server for sending notifications

        Log.d("FCMTTTT",token)
    }
}
