package org.example.org.game1.hardware.java2d

import org.example.org.game1.algebra.*
import org.example.org.game1.hardware.*
import java.awt.Color
import java.awt.Container
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GraphicsConfiguration
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints
import java.awt.Transparency
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.image.VolatileImage
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.Timer
import kotlin.Array
import kotlin.Boolean
import kotlin.Byte
import kotlin.Int
import kotlin.TODO
import kotlin.Unit
import kotlin.apply
import kotlin.collections.forEach
import kotlin.collections.forEachIndexed
import kotlin.collections.minusAssign
import kotlin.collections.mutableSetOf
import kotlin.collections.plusAssign
import kotlin.io.println
import kotlin.run

private class Java2DSurface(override val size: Size, gc: GraphicsConfiguration) : Surface {
    val image: VolatileImage = gc.createCompatibleVolatileImage(
        size.width.value,
        size.height.value,
        Transparency.TRANSLUCENT
    )

    override fun render(renderer: (Canvas) -> Unit) {
        val graphics2D = image.createGraphics()
        try {
            renderer(object : Canvas {
                override val size: Size
                    get() = this@Java2DSurface.size

                override fun clear(color: ColorIndex) {
                    graphics2D.color = Color.YELLOW
                    graphics2D.fillRect(0, 0, size.width.value, size.height.value)
                }

                override fun set(
                    xy: XY,
                    color: ColorIndex
                ) {
                    graphics2D.color = if (color.isTransparent) Color.BLUE else Color(0, 0, 0, 255)
                    graphics2D.fillRect(xy.x.value, xy.y.value, 1, 1)

                }

                override fun set(xy: XY, image: Image) {
                    graphics2D.drawImage(
                        (image as Java2DSurface).image,
                        xy.x.value, xy.y.value,
                        null
                    )
                }

            })
        } finally {
            graphics2D.dispose()
        }
    }

    override fun get(xy: XY): ColorIndex {
        return if (xy in size) {
            TODO() //ColorIndex(data[xy.y.value * size.width.value + xy.x.value])
        } else {
            ColorIndex.TRANSPARENT
        }
    }
}

private class PixelPanel(
    val size: Size, val scale: Int,
    gc: GraphicsConfiguration
) : Container() {
    val surface = Java2DSurface(size, gc)

    override fun paint(g: Graphics) {
        (g as Graphics2D).apply {
            setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
            )
            drawImage(
                surface.image,
                0, 0,
                size.width.value * scale,
                size.height.value * scale,
                null
            )
        }
    }
}

fun java2d(): Hardware = object : Hardware {
    val gc: GraphicsConfiguration = GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .defaultScreenDevice
        .defaultConfiguration

    val gamepad1 = object : Gamepad {
        val buttons = mutableSetOf<Button>()

        override fun contains(button: Button): Boolean = button in buttons
    }

    override fun initDisplay(size: Size): Display = object : Display {
        override val size: Size = size
        val pixelSize = 3
        val panel = PixelPanel(size, pixelSize, gc)

        val window = JFrame("$size").apply {
            contentPane = panel
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            isResizable = false
            setSize(
                size.width.value * pixelSize, size.height.value * pixelSize
            )
            isVisible = true
            addKeyListener(object : KeyAdapter() {
                fun keyToButton(keyCode: Int): Button? = when (keyCode) {
                    KeyEvent.VK_UP -> Button.UP
                    KeyEvent.VK_DOWN -> Button.DOWN
                    KeyEvent.VK_LEFT -> Button.LEFT
                    KeyEvent.VK_RIGHT -> Button.RIGHT
                    KeyEvent.VK_Z -> Button.A
                    KeyEvent.VK_X -> Button.B
                    KeyEvent.VK_A -> Button.X
                    KeyEvent.VK_S -> Button.Y
                    KeyEvent.VK_ENTER -> Button.START
                    KeyEvent.VK_SPACE -> Button.SELECT
                    else -> null
                }

                override fun keyPressed(e: KeyEvent) {
                    if (e.keyCode == KeyEvent.VK_ESCAPE) {
                        dispose()
                    }

                    keyToButton(e.keyCode)?.run {
                        println(this)
                        gamepad1.buttons += this
                    }
                }

                override fun keyReleased(e: KeyEvent) {
                    keyToButton(e.keyCode)?.run {
                        gamepad1.buttons -= this
                    }
                }
            })
        }


        override val palette: Palette
            get() = object : Palette {
                override fun get(index: ColorIndex): ColorRGB {
                    TODO()
                }
            }

        override fun render(renderer: (Canvas) -> Unit) {
            fun renderImpl() {
                panel.surface.render { canvas ->
                    renderer(canvas)
                }
                panel.repaint()
            }
            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater {
                    renderImpl()
                }
            } else {
                renderImpl()
            }
        }


        override fun exit() {
            window.dispose()
        }
    }

    override fun createImage(pixels: Array<Array<ColorIndex>>): Image {
        return Java2DSurface(
            Size(
                Width(pixels[0].size),
                Height(pixels.size)
            ),
            gc
        ).apply {
            render { canvas ->
                pixels.forEachIndexed { y, row ->
                    row.forEachIndexed { x, color ->
                        canvas[xy(x, y)] = color
                    }
                }
            }

        }
    }

    override fun gamepad(device: GamepadDevice): Gamepad {
        return when (device) {
            GamepadDevice.Gamepad1 -> gamepad1
            GamepadDevice.Gamepad2 -> gamepad1
            GamepadDevice.Gamepad3 -> gamepad1
            GamepadDevice.Gamepad4 -> gamepad1
        }
    }
}

fun main() {
    val hardware = java2d()
    val gamepad = hardware.gamepad(GamepadDevice.Gamepad1)
    val myImage = hardware.parseImage(
        """
        P2
        8 8 
        1 1 1 1 1 1 1 1 
        1 0 0 0 0 0 0 1
        1 0 0 0 0 0 0 1
        1 0 0 0 0 0 0 1
        1 0 0 0 0 0 0 1
        1 0 0 0 0 0 0 1
        1 0 0 0 0 0 0 1
        1 1 1 1 1 1 1 1
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
            canvas.clear(ColorIndex(1))

            (0 until 24).forEach { y ->
                (0 until 32).forEach { x ->
                    canvas[xy(x * 8, y * 8)] = myImage
                }
            }

            val y = frameNumber % 100
            for (x in 0 until canvas.size.width.value) {
                canvas[xy(x, x)] = ColorIndex(Byte.MAX_VALUE)
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