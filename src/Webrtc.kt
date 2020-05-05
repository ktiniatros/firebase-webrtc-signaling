package nl.giorgos

data class UserDescription(val token: String, val sdp: String)
data class User(val username: String, val description: UserDescription)