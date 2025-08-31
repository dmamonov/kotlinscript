package ansi

object Ansi256 {
    // --- Core SGR pieces ---
    private const val ESC = "\u001B["
    const val RESET = "${ESC}0m"

    // --- 8-bit / 256-color (palette index 0..255) ---
    /** Foreground by 8-bit palette index (0..255). */
    fun fg8(index: Int): String = "${ESC}38;5;${index.coerceIn(0, 255)}m"

    /** Background by 8-bit palette index (0..255). */
    fun bg8(index: Int): String = "${ESC}48;5;${index.coerceIn(0, 255)}m"

    // --- RGB → nearest 8-bit palette index ---
    /**
     * Convert 24-bit RGB (0..255) to the nearest 8-bit (256-color) palette index.
     * Uses the standard xterm 6×6×6 color cube (16..231) plus grayscale (232..255).
     */
    fun rgbTo8(r: Int, g: Int, b: Int): Int {
        val R = r.coerceIn(0, 255)
        val G = g.coerceIn(0, 255)
        val B = b.coerceIn(0, 255)

        // Map each channel to 0..5 on the cube
        fun toCube(v: Int): Int = Math.round((v / 255.0) * 5).toInt().coerceIn(0, 5)

        val r6 = toCube(R)
        val g6 = toCube(G)
        val b6 = toCube(B)
        val cubeIndex = 16 + 36 * r6 + 6 * g6 + b6

        // Candidate color from cube back to 0..255 per channel
        fun fromCube(n: Int): Int = Math.round(n * 255 / 5.0).toInt()

        val cr = fromCube(r6)
        val cg = fromCube(g6)
        val cb = fromCube(b6)
        val cubeDist = sq(R - cr) + sq(G - cg) + sq(B - cb)

        // Grayscale ramp: 24 steps from 232..255 (approx 8..248)
        val avg = (R + G + B) / 3.0
        val grayLevel = Math.round((avg - 8) / 247.0 * 24).toInt().coerceIn(0, 23)
        val grayIndex = 232 + grayLevel
        val grayValue = Math.round((8 + 10.7 * grayLevel)).toInt() // ≈8..255
        val grayDist = 3 * sq(avg - grayValue)

        // Choose whichever is closer
        return if (grayDist < cubeDist) grayIndex else cubeIndex
    }

    /** Foreground by 24-bit RGB, approximated to 8-bit palette. */
    fun fgRgb8(r: Int, g: Int, b: Int): String = fg8(rgbTo8(r, g, b))

    /** Background by 24-bit RGB, approximated to 8-bit palette. */
    fun bgRgb8(r: Int, g: Int, b: Int): String = bg8(rgbTo8(r, g, b))

    // --- Convenience: wrap text and auto-reset ---
    fun paint(text: String, vararg codes: String): String =
        buildString {
            codes.forEach { append(it) }
            append(text)
            append(RESET)
        }

    // --- Small math helpers ---
    private fun sq(x: Double): Double = x * x
    private fun sq(x: Int): Double = (x * x).toDouble()


    fun grayIndex(step: Int): Int = 232 + step.coerceIn(0, 23)

    fun grayFg(step: Int): String = fg8(grayIndex(step))
    fun grayBg(step: Int): String = bg8(grayIndex(step))

}
