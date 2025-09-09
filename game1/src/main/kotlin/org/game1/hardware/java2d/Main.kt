package org.example.org.game1.hardware.java2d

import org.example.org.game1.algebra.dxy
import org.example.org.game1.algebra.xy
import org.example.org.game1.hardware.Button
import org.example.org.game1.hardware.ColorIndex
import org.example.org.game1.hardware.GamepadDevice
import javax.swing.Timer

fun main() {
    val hardware = java2d()
    val gamepad = hardware.gamepad(GamepadDevice.Gamepad1)
    val myImage = hardware.parseImage(
        """
        P2
        8 8 
        1
        0 0 1 1 1 1 0 0 
        0 0 0 1 1 0 0 0
        0 0 1 1 1 1 0 0
        0 1 0 1 1 0 1 0
        1 0 0 1 1 0 0 1
        1 0 1 0 0 1 0 1
        0 0 1 0 0 1 0 0
        0 1 1 0 0 1 1 0
    """.trimIndent()
    )
    val display = hardware.initDisplay()


    var frameNumber = 0
    var lastNow = System.currentTimeMillis()
    val fpsStep = 25
    var myPoint = xy(10, 10)
    Timer(1000 / 60) {  // ~60 FPS
        frameNumber++

        if (Button.LEFT in gamepad) {
            myPoint -= dxy(dx = 1)
        }
        if (Button.RIGHT in gamepad) {
            myPoint += dxy(dx = 1)
        }
        if (Button.UP in gamepad) {
            myPoint -= dxy(dy = 1)
        }
        if (Button.DOWN in gamepad) {
            myPoint += dxy(dy = 1)
        }

        display.render { canvas ->
            canvas.clear(ColorIndex(4))
            (0 until 24 step 2).forEach { y ->
                (0 until 32 step 2).forEach { x ->
                    canvas[xy(x * 8, y * 8)] = myImage
                }
            }
            canvas[myPoint] = myImage
        }
        if (frameNumber % fpsStep == 0) {
            val now = System.currentTimeMillis()
            val deltaTime = now - lastNow
            val fps = fpsStep * 1000.0 / deltaTime
            println("FPS: ${fps}")
            lastNow = now
        }
    }.start()
    println("Window initialized")
}