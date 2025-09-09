package org.example

import java.awt.Color
import java.nio.file.Files
import java.nio.file.Path

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    fun parse(color: String): Color {

        fun cmyk(c: Int, m: Int, y: Int, k: Int): Color {
            check(c in 0..100)
            check(m in 0..100)
            check(y in 0..100)
            check(c in 0..100)
            val r = 255 * (1 - c / 100.0) * (1 - k / 100.0)
            val g = 255 * (1 - m / 100.0) * (1 - k / 100.0)
            val b = 255 * (1 - y / 100.0) * (1 - k / 100.0)
            return Color(r.toInt(), g.toInt(), b.toInt())
        }

        if (color.contains(Regex("[rgb]"))) {
            var r = 0
            var g = 0
            var b = 0
            color.trim().split(Regex("\\s+")).forEach { part ->
                val key = part[0]
                val value = part.substring(1).toInt()
                when (key) {
                    'r' -> r = value
                    'g' -> g = value
                    'b' -> b = value
                    else -> throw IllegalArgumentException("Unknown color part: $part")
                }
            }

            return Color(r, g, b)
        } else if (color.contains(Regex("[cmyk]"))) {
            var c = 0
            var m = 0
            var y = 0
            var k = 0
            color.trim().split(Regex("\\s+")).forEach { part ->
                val key = part[0]
                val value = part.substring(1).toInt()
                when (key) {
                    'c' -> c = value
                    'm' -> m = value
                    'y' -> y = value
                    'k' -> k = value
                    else -> throw IllegalArgumentException("Unknown color part: $part")
                }
            }

            return cmyk(c, m, y, k)
        } else if (color.matches(Regex("\\d+-\\d+-\\d+(-\\d+)?"))) {
            val parts = color.split("-").map { it.toInt() }
            return if (parts.size == 3) {
                cmyk(parts[0], parts[1], parts[2], 0)
            } else {
                cmyk(parts[0], parts[1], parts[2], parts[3])
            }
        } else throw IllegalArgumentException("Unknown color part: $color")
    }

    fun ansi(name: String, color: Color): String {
        return "\u001b[48;2;${color.red};${color.green};${color.blue}m${name}\u001b[0m"
    }

    Files.lines(Path.of("colors/palette.csv")).forEach { line ->
        val trim = line.trim()
        val items = trim.split(";").map { it.trim() }
        if (!trim.startsWith("#") && items.size>1) {
            try {
                val name = items[0]
                val colors = items.subList(1, items.size).map { parse(it) }
                val width = 12
                fun c(index: Int): String {
                    return if (index < colors.size) {
                        ansi("".padEnd(width), colors[index])
                    } else ".".padEnd(width)
                }
                println("${name.padStart(width)} ${c(0)}${c(1)}${c(2)}${c(3)}${c(4)}")
            } catch (e: Exception) {
                System.err.println("Error parsing line: $line")
                e.printStackTrace()
                System.exit(0)
            }
        } else println()
    }


}