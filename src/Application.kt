package nl.giorgos

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.gson.JsonObject
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.*
import java.text.DateFormat


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient(Apache) {
    }

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    routing {
        //TODO limit acquiring existing username to last 24 hours
        post("/token") {
            val body = call.receive<HashMap<String, String>>()

            val username = body.get("username") ?: ""
            val token = body.get("token") ?: ""

            if (username.isEmpty() or token.isEmpty()) {
                call.respondText(text = "{}", contentType = ContentType.Application.Json, status = HttpStatusCode.BadRequest)
            } else {
                val user = User(username, UserDescription(token, ""))
                FireDatabase.addUser(user)
                call.respondText("{}", contentType = ContentType.Application.Json)
            }
        }

        post("/sdp") {
            val body = call.receive<HashMap<String, String>>()

            val username = body.get("username") ?: ""
            val sdp = body.get("sdp") ?: ""

            if (username.isEmpty() or sdp.isEmpty()) {
                call.respondText(text = "{}", contentType = ContentType.Application.Json, status = HttpStatusCode.BadRequest)
            } else {
                FireDatabase.updateSdp(username, sdp)

                call.respondText("{}", contentType = ContentType.Application.Json)
            }
        }

        get("/users") {

            //TODO get from db all users with valid fcm token and sdp

            call.respondText("{}", contentType = ContentType.Application.Json)
        }

        post("/invite") {
            val body = call.receive<HashMap<String, String>>()
            val usernameFrom = body.get("from") ?: ""
            val usernameTo = body.get("to") ?: ""
            val sdpFrom = body.get("sdp") ?: ""

            if (usernameFrom.isEmpty() or usernameTo.isEmpty()) {
                call.respondText(text = "{}", contentType = ContentType.Application.Json, status = HttpStatusCode.BadRequest)
            }

            FireDatabase.getTokenByUsername(usernameTo) { tokenTo ->
                if (tokenTo != null) {
                    PushNotificationSender.send(tokenTo, sdpFrom, usernameFrom)
                }
            }
            call.respondText("{}", contentType = ContentType.Application.Json)
        }

        get("/test") {
            call.respondText("{\"key\":\"value\"d}", contentType = ContentType.Application.Json)
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
        }
    }
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
