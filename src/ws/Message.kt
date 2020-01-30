package pasteng.ws

enum class Message(val text: String) {
    OPEN("open"),
    NEW("new"),
    ECHO("echo");

    companion object {
        fun parse(text: String): Message? = Message.values().firstOrNull { m -> m.text == text }
    }
}
