package pasteng.ws

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText

import kotlinx.coroutines.channels.SendChannel

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("pasteng-ws-messaging")

private const val DEFAUT_MESSAGE_PREFIX = "pasteng:"

private suspend fun baseSend(channel: SendChannel<Frame>, text: String, content: String?)
        = channel.send(Frame.Text("$DEFAUT_MESSAGE_PREFIX$text" + (content?.let { " $it" } ?: "")))

suspend fun SendChannel<Frame>.send(message: Pair<Message, String?>) = baseSend(this, message.first.text, message.second)

suspend fun SendChannel<Frame>.send(error: Error) = baseSend(this, error.text, null)

suspend fun SendChannel<Frame>.send(text: String) = this.send(Frame.Text(text))

private fun baseReceive(frame: Frame.Text): Pair<Message, String>? {
    val raw = frame.readText()
    return if (raw.startsWith(DEFAUT_MESSAGE_PREFIX)) {
        val rawMessage = raw.substringAfter(DEFAUT_MESSAGE_PREFIX).substringBefore(" ")

        Message.parse(rawMessage)?.let {
            it to raw.substringAfter(" ")
        }.also {
            if (it != null)
                logger.debug("recv message '${it.first}'")
            else
                logger.debug("recv failed")
        }
    } else null
}

suspend fun Frame.Text.receiveMessage(): Pair<Message, String>? = baseReceive(this)
