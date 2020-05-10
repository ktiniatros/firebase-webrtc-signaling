package nl.giorgos

import com.google.firebase.database.ServerValue

data class UserDescription(val token: String, val sdp: String, val lastUpdatedAt: Map<String, String> = ServerValue.TIMESTAMP)
data class User(val username: String, val description: UserDescription)