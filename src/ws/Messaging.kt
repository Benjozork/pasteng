package pasteng.ws

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText

import kotlinx.coroutines.channels.SendChannel

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("pasteng-ws-messaging")

private const val DEFAUT_MESSAGE_PREFIX = "pasteng:"

private suspend fun baseSend(channel: SendChannel<Frame>, text: String) = channel.send(Frame.Text("$DEFAUT_MESSAGE_PREFIX$text"))

suspend fun SendChannel<Frame>.send(message: Message) = baseSend(this, message.text)

suspend fun SendChannel<Frame>.send(error: Error) = baseSend(this, error.text)

suspend fun SendChannel<Frame>.send(text: String) = this.send(Frame.Text(text))

private fun baseReceive(frame: Frame.Text): Pair<Message, String>? {
    val raw = frame.readText()
    return if (raw.startsWith(DEFAUT_MESSAGE_PREFIX)) {
        val rawMessage = raw.substringAfter(DEFAUT_MESSAGE_PREFIX).substringBefore(" ")

        Message.parse(rawMessage)?.let {
            it to raw.substringAfter(" ")
        }.also {
            it ?.let { logger.debug("recv message '${it.first}'") }
                ?: logger.debug("recv failed")
        }
    } else null
}

suspend fun Frame.Text.receiveMessage(): Pair<Message, String>? = baseReceive(this)
