package pasteng.ws

enum class Error(val text: String) {
    UNRECOGNIZED_COMMAND("badcmd"),
    INTERNAL_ERROR("interr")
}
