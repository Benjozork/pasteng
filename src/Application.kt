package pasteng

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.content.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.websocket.webSocket
import io.ktor.http.cio.websocket.Frame

import pasteng.ws.Message
import pasteng.ws.receiveMessage
import pasteng.ws.Error
import pasteng.ws.send

import org.slf4j.event.*

import java.time.*
import java.io.File

import com.fasterxml.jackson.databind.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {

        static("/") {
            staticRootFolder = File("client/src/app/dist")

            default("index.html")
        }

        webSocket("/") {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        frame.receiveMessage()?.let {
                            when (it.first) {
                                Message.ECHO -> {
                                    outgoing.send("${it.second}, received !")
                                }

                                Message.OPEN -> {
                                    outgoing.send("${it.second}, opened !")
                                }

                                Message.NEW -> {
                                    outgoing.send("${it.second}, created !")
                                }
                            }
                        } ?: outgoing.send(Error.UNRECOGNIZED_COMMAND)
                    }
                }
            }
        }

    }

}

