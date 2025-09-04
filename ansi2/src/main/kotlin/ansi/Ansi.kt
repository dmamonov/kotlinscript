package ansi
object Ansi {
    // Reset
    const val RESET = "\u001B[0m"

    // Styles
    const val BOLD = "\u001B[1m"
    const val DIM = "\u001B[2m"
    const val ITALIC = "\u001B[3m"
    const val UNDERLINE = "\u001B[4m"
    const val BLINK = "\u001B[5m"
    const val INVERSE = "\u001B[7m"
    const val HIDDEN = "\u001B[8m"
    const val STRIKETHROUGH = "\u001B[9m"

    // Foreground Colors
    const val BLACK = "\u001B[30m"
    const val RED = "\u001B[31m"
    const val GREEN = "\u001B[32m"
    const val YELLOW = "\u001B[33m"
    const val BLUE = "\u001B[34m"
    const val MAGENTA = "\u001B[35m"
    const val CYAN = "\u001B[36m"
    const val WHITE = "\u001B[37m"

    // Bright Foreground Colors
    const val BRIGHT_BLACK = "\u001B[90m"
    const val BRIGHT_RED = "\u001B[91m"
    const val BRIGHT_GREEN = "\u001B[92m"
    const val BRIGHT_YELLOW = "\u001B[93m"
    const val BRIGHT_BLUE = "\u001B[94m"
    const val BRIGHT_MAGENTA = "\u001B[95m"
    const val BRIGHT_CYAN = "\u001B[96m"
    const val BRIGHT_WHITE = "\u001B[97m"

    // Background Colors
    const val BG_BLACK = "\u001B[40m"
    const val BG_RED = "\u001B[41m"
    const val BG_GREEN = "\u001B[42m"
    const val BG_YELLOW = "\u001B[43m"
    const val BG_BLUE = "\u001B[44m"
    const val BG_MAGENTA = "\u001B[45m"
    const val BG_CYAN = "\u001B[46m"
    const val BG_WHITE = "\u001B[47m"

    // Bright Background Colors
    const val BG_BRIGHT_BLACK = "\u001B[100m"
    const val BG_BRIGHT_RED = "\u001B[101m"
    const val BG_BRIGHT_GREEN = "\u001B[102m"
    const val BG_BRIGHT_YELLOW = "\u001B[103m"
    const val BG_BRIGHT_BLUE = "\u001B[104m"
    const val BG_BRIGHT_MAGENTA = "\u001B[105m"
    const val BG_BRIGHT_CYAN = "\u001B[106m"
    const val BG_BRIGHT_WHITE = "\u001B[107m"

    // Cursor Control
    const val CURSOR_UP = "\u001B[1A"
    const val CURSOR_DOWN = "\u001B[1B"
    const val CURSOR_RIGHT = "\u001B[1C"
    const val CURSOR_LEFT = "\u001B[1D"
    const val CURSOR_HOME = "\u001B[H"
    const val CURSOR_SAVE = "\u001B[s"
    const val CURSOR_RESTORE = "\u001B[u"

    // Erasing
    const val CLEAR_SCREEN = "\u001B[2J"
    const val CLEAR_LINE = "\u001B[2K"
    const val CLEAR_LINE_END = "\u001B[0K"
    const val CLEAR_LINE_START = "\u001B[1K"

    // Other
    const val SCROLL_UP = "\u001B[S"
    const val SCROLL_DOWN = "\u001B[T"
}