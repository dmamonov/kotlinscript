package org.example.org.game1.hardware.java2d

import org.example.org.game1.algebra.*
import org.example.org.game1.hardware.*
import java.awt.Container
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel
import javax.swing.JFrame
import javax.swing.SwingUtilities

private class Java2DPalette : Palette {
    val paletteSize = 256
    val red = ByteArray(paletteSize)
    val green = ByteArray(paletteSize)
    val blue = ByteArray(paletteSize)
    val a = ByteArray(paletteSize)

    init {
        // Example: grayscale ramp
        for (i in 0 until paletteSize) {
            red[i] = (i).toByte()  // 0..252
            green[i] = (i).toByte()
            blue[i] = (i).toByte()
            a[i] = (-1).toByte()     // opaque (0xFF)
        }
    }

    val indexColorModel = IndexColorModel(
        8,                // bits per pixel
        paletteSize,      // size of palette
        red, green, blue, a
    )

    override fun get(index: ColorIndex): ColorRGB {
        val v = index.value.toInt() and 0xFF
        return ColorRGB(v.toUByte(), v.toUByte(), v.toUByte())
    }
}

private val javaPalette = Java2DPalette()

private class Java2DSurface(override val size: Size) : Surface {
    val image = BufferedImage(
        size.width.value, size.height.value, BufferedImage.TYPE_BYTE_INDEXED, javaPalette.indexColorModel
    )
    private val data: ByteArray = (image.raster.dataBuffer as java.awt.image.DataBufferByte).data

    override fun clear(color: ColorIndex) {
        java.util.Arrays.fill(data, color.value)
    }

    override fun set(xy: XY, color: ColorIndex) {
        if (color.value != 0.toByte() && xy in size) {
            data[xy.y.value * size.width.value + xy.x.value] = color.value
        }
    }

    override fun set(xy: XY, image: Image) {
        val delta = xy.toDelta
        val surfaceBox = ((image.size.box + delta) intersect size.box) ?: return
        surfaceBox.sy.coordinates.forEach { surfaceY ->
            val yOffset = surfaceY.value * size.width.value
            surfaceBox.sx.coordinates.forEach { surfaceX ->
                val imageXY = XY(surfaceX - delta.dx, surfaceY - delta.dy)
                val color = image[imageXY]
                if (color.isColor) {
                    data[yOffset + surfaceX.value] = color.value
                }
            }
        }
    }

    override fun crop(box: Box): Surface = object : Surface {
        override val size: Size = box.size
        private val delta = box.min.toDelta

        override fun clear(color: ColorIndex) {
            (size.box + delta).points.forEach { point ->
                this@Java2DSurface[point] = color
            }
        }

        override fun set(xy: XY, color: ColorIndex) {
            if (xy in size) {
                this@Java2DSurface[xy + delta] = color
            }
        }

        override fun set(xy: XY, image: Image) {
            if (xy in size) {
                this@Java2DSurface[xy + delta] = image
            }
        }

        override fun crop(box: Box): Surface {
            val effectiveBox = (size.box intersect box) ?: ZERO_BOX
            return this@Java2DSurface.crop(effectiveBox + delta)
        }

        override fun get(xy: XY): ColorIndex {
            return this@Java2DSurface[xy + delta]
        }
    }

    override fun get(xy: XY): ColorIndex {
        return if (xy in size) {
            ColorIndex(data[xy.y.value * size.width.value + xy.x.value])
        } else {
            ColorIndex.TRANSPARENT
        }
    }
}


private class PixelPanel(
    val size: Size, val scale: Int
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
                    val intIndex = index.value.toInt() and 0xFF
                    return ColorRGB(
                        red = javaPalette.red[intIndex].toUByte(),
                        green = javaPalette.green[intIndex].toUByte(),
                        blue = javaPalette.blue[intIndex].toUByte(),
                    )
                }
            }

        override fun render(renderer: (Surface) -> Unit) {
            fun renderImpl() {
                renderer(panel.surface)
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
            pixels.forEachIndexed { y, row ->
                row.forEachIndexed { x, color ->
                    this@apply[xy(x, y)] = color
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
    val display = java2d().initDisplay()

    var frameNumber = 0
    var lastNow = System.currentTimeMillis()
    val fpsStep = 60
    javax.swing.Timer(1000 / 60) {  // ~60 FPS
        frameNumber++

        display.render { surface ->
            surface.clear(ColorIndex(1))

            val y = frameNumber % 100
            for (x in 0 until surface.size.width.value) {
                surface[xy(x, x)] = ColorIndex(Byte.MAX_VALUE)
            }
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