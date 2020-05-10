package nl.giorgos

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification

class PushNotificationSender(private val firebaseMessaging: FirebaseMessaging = FirebaseMessaging.getInstance()) {

    private fun notificationBuilder() = Notification.builder().build()

    companion object {
        private val pushNotificationSender by lazy { PushNotificationSender() }

        fun send(token: String, sdp: String) {
            val message = Message.builder()
                    .setToken(token)
                    .putData("sdp", sdp)
                    .build()

            pushNotificationSender.firebaseMessaging.send(message)
        }
    }
}