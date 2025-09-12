package org.example

import java.awt.Color
import java.nio.file.Files
import java.nio.file.Path
import kotlin.times

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

    val colors: List<Pair<String, List<Color>>> = Files.lines(Path.of("colors/palette.csv"))
        .map { it.trim() }
        .filter { !it.isBlank() && !it.startsWith("#") }
        .map { line -> line.trim().split(";").map { it.trim() } }
        .map { items ->
            println(items)
            val name = items[0]
            val colors = items.subList(1, items.size).map { parse(it) }
            check(colors.isNotEmpty()) { "Color group must have at least 1 colors: $name" }
            name to colors
        }.toList()

    val names = colors.associate { it.second[0] to it.first }
    fun closestRgbColor(target: Color): Color {
        return names.filter { !it.value.startsWith("unknown") }.keys.minByOrNull { color ->
            val dr = color.red - target.red
            val dg = color.green - target.green
            val db = color.blue - target.blue
            dr * dr + dg * dg + db * db
        } ?: throw IllegalArgumentException("Color list is empty")
    }

    val missing = mutableSetOf<Color>()
    val width = 12
    for ((_, colorGroup) in colors) {
        val hasMissingColor = colorGroup.map { names[it] }.filter { it == null }.size > 0
        if (hasMissingColor || !hasMissingColor) {
            for (color in colorGroup) {
                val colorName = names[color]
                print(ansi((colorName ?: "<<????>>").padEnd(width), color))
                if (colorName == null) missing += color
            }
            println()
        }
    }

    names.entries.forEach { entry ->
        val color = entry.key
        val name = entry.value
        if (name.startsWith("unknown")) {
            val betterName = names[closestRgbColor(color)]!!
            print(name+" "+ansi(betterName.padEnd(width), entry.key))
            println()
        }
    }
    if (missing.isNotEmpty()) {
        println("Missing colors:")
        for (color in missing) {
            println(ansi("r${color.red} g${color.green} b${color.blue}", color))
        }

    }
}