package nl.giorgos

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import java.io.FileInputStream

class FireDatabase {
    companion object {
        fun getInstance(): FirebaseDatabase {
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

            return FirebaseDatabase.getInstance()
        }

        fun addUser(user: User) {
            val table = getInstance().getReference("webrtc/users")

            val me = User(user.username, user.description)

            val newRecords = mapOf(me.username to me.description)

            table.setValue(newRecords) { error, ref ->
                error?.toException()?.printStackTrace()
            }
        }
    }
}