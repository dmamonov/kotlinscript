package org.example.org.game1.hardware

import org.example.org.game1.algebra.*

interface Hardware {
    fun initDisplay(size: Size = size(256, 192)): Display
    fun createImage(pixels: Array<Array<ColorIndex>>): Image

    fun parseImage(image: String): Image {
        val lines = image.lines()
        val header = lines[0].trim()
        require(header == "P2") { "Unsupported format `$header`" }
        var sizeIndex = 1
        while (sizeIndex < lines.size) {
            val comment = lines[sizeIndex].trim()
            if (comment.startsWith("#")) {
                sizeIndex += 1
            } else break
        }
        val bySpaces = Regex("\\s+")
        val size = lines[sizeIndex].trim().split(bySpaces)
        require(size.size == 2) { "Wrong size format, expecting width and height" }

        val firstRow = sizeIndex + 1
        val width = size[0].toInt().also { check(it > 0) { "Wrong width $it" } }
        val height = size[1].toInt().also { check(it > 0) { "Wrong height $it" } }
        val matrix = (0 until height).map { rowIndex ->
            val pixels = lines[firstRow + rowIndex].trim().split(bySpaces)
            check(pixels.size == width) { "Wrong number of pixels in row-index $rowIndex, expected $width, actual ${pixels.size}" }

            pixels.map { ColorIndex(if (it=="0") 0 else 255.toByte()) }.toTypedArray()
        }.toTypedArray()

        return createImage(matrix)
    }

    fun gamepad(device: GamepadDevice): Gamepad
}