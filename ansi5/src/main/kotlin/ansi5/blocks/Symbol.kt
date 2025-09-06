package ansi5.blocks

import ansi5.ansi.ANSI_PREFIX
import ansi5.ansi.ANSI_RESET
import ansi5.ansi.AnsiColor
import ansi5.ansi.AnsiText
import ansi5.ansi.Color16

data class Symbol(
    val value: String,
    val bg: AnsiColor = Color16.BLACK,
    val fg: AnsiColor = Color16.WHITE,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
) {
    companion object {
        val BLANK = Symbol(" ")
    }

    override fun toString(): String {
        val ansiBold = if (bold) AnsiText.BOLD.command else ""
        val ansiItalic = if (italic) AnsiText.ITALIC.command else ""
        val ansiUnderline = if (underline) AnsiText.UNDERLINE.command else ""
        val result = "$ansiBold$ansiItalic$ansiUnderline${fg.fgCommand}${bg.bgCommand}${value}$ANSI_RESET"
        return result
    }
}

fun String.symbols() = this.map { Symbol(it.toString()) }
