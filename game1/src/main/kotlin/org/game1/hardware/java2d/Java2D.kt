package org.example.org.game1.hardware.java2d

import org.example.org.game1.algebra.*
import org.example.org.game1.hardware.*
import org.example.org.game1.hardware.ColorIndex.Companion.TRANSPARENT
import java.awt.AlphaComposite
import java.awt.Container
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints
import java.awt.Transparency
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.image.VolatileImage
import javax.swing.JFrame
import javax.swing.SwingUtilities
import kotlin.Array
import kotlin.Boolean
import kotlin.Int
import kotlin.TODO
import kotlin.Unit
import kotlin.apply
import kotlin.collections.forEachIndexed
import kotlin.collections.minusAssign
import kotlin.collections.mutableSetOf
import kotlin.collections.plusAssign
import kotlin.io.println
import kotlin.run

private class Java2DSurface(override val size: Size) : Surface {
    val gc = GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .defaultScreenDevice
        .defaultConfiguration
    val image: VolatileImage = gc
        .createCompatibleVolatileImage(
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
                    graphics2D.composite = AlphaComposite.SrcOver
                    graphics2D.color = color.javaColor
                    graphics2D.fillRect(0, 0, size.width.value, size.height.value)
                }

                override fun set(xy: XY, image: Image) {
                    graphics2D.drawImage(
                        (image as Java2DSurface).image,
                        xy.x.value, xy.y.value,
                        null
                    )
                }

                override operator fun set(
                    xy: XY,
                    color: ColorIndex
                ) {
                    if (xy in size) {
                        if (color==TRANSPARENT){
                            graphics2D.composite = AlphaComposite.Clear
                        } else {
                            graphics2D.composite = AlphaComposite.SrcOver
                        }
                        graphics2D.color = color.javaColor
                        graphics2D.fillRect(xy.x.value, xy.y.value, 1, 1)
                    }
                }
            })
        } finally {
            graphics2D.dispose()
        }
    }
}

private class PixelPanel(
    val size: Size, val scale: Int,
) : Container() {
    val surface = Java2DSurface(size)

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
    val gamepad1 = object : Gamepad {
        val buttons = mutableSetOf<Button>()

        override fun contains(button: Button): Boolean = button in buttons
    }

    override fun initDisplay(size: Size): Display = object : Display {
        override val size: Size = size
        val pixelSize = 3
        val panel = PixelPanel(size, pixelSize)

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
            )
        ).apply {
            this.render { canvas ->
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