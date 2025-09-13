package ansi

import ansi.compose1.nothing

object UiSymbols {
    // Basic Shapes
    const val BULLET = "•"
    const val DOT = "·"
    const val TRIANGLE_RIGHT = "▶"
    const val TRIANGLE_LEFT = "◀"
    const val TRIANGLE_UP = "▲"
    const val TRIANGLE_DOWN = "▼"
    const val CIRCLE = "○"
    const val FILLED_CIRCLE = "●"
    const val SQUARE = "□"
    const val FILLED_SQUARE = "■"
    const val DIAMOND = "◇"
    const val FILLED_DIAMOND = "◆"
    const val STAR = "☆"
    const val FILLED_STAR = "★"
    const val HEART = "♡"
    const val FILLED_HEART = "♥"

    // Arrows
    const val ARROW_LEFT = "←"
    const val ARROW_UP = "↑"
    const val ARROW_RIGHT = "→"
    const val ARROW_DOWN = "↓"
    const val ARROW_LEFT_RIGHT = "↔"
    const val ARROW_UP_DOWN = "↕"
    const val DOUBLE_ARROW_LEFT = "⇐"
    const val DOUBLE_ARROW_RIGHT = "⇒"
    const val DOUBLE_ARROW_UP = "⇑"
    const val DOUBLE_ARROW_DOWN = "⇓"
    const val ARROW_RIGHT_HOOK = "↪"
    const val ARROW_LEFT_HOOK = "↩"
    const val ARROW_CURVE_RIGHT = "↷"
    const val ARROW_CURVE_LEFT = "↶"

    // Box Drawing (great for text UI)
    const val BOX_H = "─"
    const val BOX_V = "│"
    const val BOX_TL = "┌"
    const val BOX_TR = "┐"
    const val BOX_BL = "└"
    const val BOX_BR = "┘"
    const val BOX_T = "┬"
    const val BOX_B = "┴"
    const val BOX_L = "├"
    const val BOX_R = "┤"
    const val BOX_CROSS = "┼"
    const val BOX_DOUBLE_H = "═"
    const val BOX_DOUBLE_V = "║"
    const val BOX_DOUBLE_TL = "╔"
    const val BOX_DOUBLE_TR = "╗"
    const val BOX_DOUBLE_BL = "╚"
    const val BOX_DOUBLE_BR = "╝"
    const val BOX_DOUBLE_T = "╦"
    const val BOX_DOUBLE_B = "╩"
    const val BOX_DOUBLE_L = "╠"
    const val BOX_DOUBLE_R = "╣"
    const val BOX_DOUBLE_CROSS = "╬"

    fun box(left:Boolean, right: Boolean, top: Boolean, bottom: Boolean, isDouble: Boolean=false):String {
        return if (left && right && top && bottom) {
            SQUARE
        } else if (left || right) {
            if (left && top) {
                if (isDouble) BOX_DOUBLE_TL else BOX_TL
            } else if (left && bottom) {
                if (isDouble) BOX_DOUBLE_BL else BOX_BL
            } else if (right && top) {
                if (isDouble) BOX_DOUBLE_TR else BOX_TR
            } else if (right && bottom) {
                if (isDouble) BOX_DOUBLE_BR else BOX_BR
            } else {
                if (isDouble) BOX_DOUBLE_V else BOX_V
            }
        } else {
            if (top ||bottom) {
                if (isDouble) BOX_DOUBLE_H else BOX_H
            } else {
                nothing
            }
        }
    }

    // Block Elements
    const val BLOCK_FULL = "█"
    const val BLOCK_DARK = "▓"
    const val BLOCK_MEDIUM = "▒"
    const val BLOCK_LIGHT = "░"
    const val SHADE_TOP = "▀"
    const val SHADE_BOTTOM = "▄"

    // Misc UI Icons
    const val CHECK = "✓"
    const val CROSS = "✗"
    const val PLUS = "✚"
    const val MINUS = "−"
    const val WARNING = "⚠"
    const val INFO = "ℹ"
    const val DOT_SMALL = "‧"
    const val ELLIPSIS = "…"
    const val SECTION = "§"
    const val PARAGRAPH = "¶"

    // Playing Card Symbols
    const val SPADE = "♠"
    const val CLUB = "♣"
    const val DIAMOND_CARD = "♦"
    const val HEART_CARD = "♥"

    // Math Symbols (useful in menus/UI)
    const val INFINITY = "∞"
    const val APPROX = "≈"
    const val NOT_EQUAL = "≠"
    const val LESS_EQUAL = "≤"
    const val GREATER_EQUAL = "≥"
    const val PLUS_MINUS = "±"
    const val MULTIPLY = "×"
    const val DIVIDE = "÷"

    // Misc Decorations
    const val SUN = "☀"
    const val CLOUD = "☁"
    const val UMBRELLA = "☂"
    const val SNOWFLAKE = "❄"
    const val MUSIC_NOTE = "♪"
    const val DOUBLE_MUSIC_NOTE = "♫"
    const val YIN_YANG = "☯"
    const val PEACE = "☮"
    const val SKULL = "☠"
    const val ANCHOR = "⚓"
    const val GEAR = "⚙"
}