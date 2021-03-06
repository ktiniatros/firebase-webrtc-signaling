package nl.giorgos

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.*
import java.io.FileInputStream

class FireDatabase {
    companion object {
        private val database: FirebaseDatabase by lazy {
            val databaseName = System.getenv("FIREBASE_DATABASE_NAME") ?: throw Exception("No firebase database name found in your environment variables. Please do it, eg export FIREBASE_DATABASE_NAME=fdatab-wf4he")
            println("\n\n\n****************\n\n\n\n")
            println("path: $databaseName")
            println("\n\n\n****************\n\n\n\n")

            val serviceAccount = FileInputStream("resources/$databaseName.json")

            val options = FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://$databaseName.firebaseio.com")
                    .build()

            FirebaseApp.initializeApp(options)

            FirebaseDatabase.getInstance()
        }

        fun addUser(user: User) {
            val table = database.getReference("webrtc/users")

            val me = User(user.username, user.description)

            val newRecords = mapOf(me.username to me.description)

            table.updateChildren(newRecords) { error, _ ->
                //TODO monitor
                error?.toException()?.printStackTrace()
            }
        }

        fun updateSdp(username: String, sdp: String) {
            val table = database.getReference("webrtc/users/$username")

            val newRecord = mapOf("sdp" to sdp, "lastUpdatedAt" to ServerValue.TIMESTAMP)

            table.updateChildren(newRecord) { error, _ ->
                //TODO monitor
                error?.toException()?.printStackTrace()
            }
        }

        fun getTokenByUsername(username: String, cb: (String?) -> Unit) {
            val table = database.getReference("webrtc/users/$username")
            table.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(snapshot: DataSnapshot?) {
                    val token = snapshot?.child("token")?.value as? String
                    cb(token)
                }
            })
        }

        fun getSdpByUsername(username: String, cb: (String?) -> Unit) {
            val table = database.getReference("webrtc/users/$username")
            table.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(snapshot: DataSnapshot?) {
                    val sdp = snapshot?.child("sdp")?.value as? String
                    cb(sdp)
                }
            })
        }

        fun retrieveUserByToken(token: String, cb: (Any?) -> Unit) {
            val table = database.getReference("webrtc/users")

            table.orderByChild("token").equalTo(token).addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError?) {
                   error?.toException()?.printStackTrace()
                }

                override fun onDataChange(snapshot: DataSnapshot?) {
                    cb(snapshot?.value)
//                    val data = snapshot?.value as? HashMap<*, *>?
//
//                    val username = data?.keys?.firstOrNull() as? String
//
//                    if (username != null) {
//                        val userDescription = data.get(username) as HashMap<*, *>
//                        val user = User(username, UserDescription(userDescription.get("sdp") as String, userDescription.get("token") as String))
//                        cb(user)
//                    } else {
//                        cb(null)
//                    }

                }
            })
        }
    }
}