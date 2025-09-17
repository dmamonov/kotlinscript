package ansi5.ansi

import java.io.InputStream

fun setRawMode() {
    Runtime.getRuntime().exec(arrayOf("sh", "-c", "stty raw -echo < /dev/tty")).waitFor()
}

sealed interface Key {
    data class CharKey(val ch: Char) : Key {
        override fun toString(): String = "$ch : ${ch.code}"
    }

    object Nothing : Key
    object Null : Key //(Null): Code 0
    object StartOfHeading : Key //(Start of Heading): Code 1
    object StartOfText : Key //(Start of Text): Code 2
    object EndOfText : Key //(End of Text): Code 3
    object EndOfTransmission : Key //(End of Transmission): Code 4
    object Enquiry : Key //(Enquiry): Code 5
    object Acknowledge : Key //(Acknowledge): Code 6
    object Bell : Key //(Bell): Code 7
    object Backspace : Key //(Backspace): Code 8
    object HorizontalTab : Key //(Horizontal Tab): Code 9
    object LineFeed : Key //(Line Feed): Code 10
    object VerticalTab : Key //(Vertical Tab): Code 11
    object FormFeed : Key //(Form Feed): Code 12
    object CarriageReturn : Key //(Carriage Return): Code 13
    object ShiftOut : Key //(Shift Out): Code 14
    object ShiftIn : Key //(Shift In): Code 15
    object DataLinkEscape : Key //(Data Link Escape): Code 16
    object DeviceControl1 : Key //(Device Control 1): Code 17
    object DeviceControl2 : Key //(Device Control 2): Code 18
    object DeviceControl3 : Key //(Device Control 3): Code 19
    object DeviceControl4 : Key //(Device Control 4): Code 20
    object NegativeAcknowledge : Key //(Negative Acknowledge): Code 21
    object SynchronousIdle : Key //(Synchronous Idle): Code 22
    object EndOfTransmissionBlock : Key //(End of Transmission Block): Code 23
    object Cancel : Key //(Cancel): Code 24
    object EndOfMedium : Key //(End of Medium): Code 25
    object Substitute : Key //(Substitute): Code 26
    object Escape : Key //(Escape): Code 27
    object FileSeparator : Key //(File Separator): Code 28
    object GroupSeparator : Key //(Group Separator): Code 29
    object RecordSeparator : Key //(Record Separator): Code 30
    object UnitSeparator : Key //(Unit Separator): Code 31
    object Up : Key
    object Down : Key
    object Left : Key
    object Right : Key
    object Home : Key
    object End : Key
    object Insert : Key
    object Delete : Key
    object PageUp : Key
    object PageDown : Key
    data class Function(val functionNumber: Int) : Key
    data class Unknown(val seq: String) : Key
}

data class InputEvent(
    val key: Key,
    val shift: Boolean = false,
    val alt: Boolean = false,
    val ctrl: Boolean = false,
)

fun readInputEvent(inputStream: InputStream = System.`in`): InputEvent {
    val buffer = mutableListOf<Byte>()
    fun unknownInput() = InputEvent(Key.Unknown(buffer.map {
        if (it > 32) {
            "`${it.toInt().toChar()}`"
        } else it.toString()
    }.toString()))

    fun readByte(delegate: (Int) -> InputEvent): InputEvent {
        if (inputStream.available() == 0) {
            return InputEvent(Key.Nothing)
        } else {
            val inputByte = inputStream.read()
            if (inputByte < 0) {
                return InputEvent(Key.EndOfTransmission)
            } else {
                buffer.add(inputByte.toByte())
                return delegate(inputByte)
            }
        }
    }

    fun processEscape(): InputEvent {
        return readByte { second ->
            when (second) {
                '['.code -> readByte { third ->
                    when (third) {
                        'A'.code -> InputEvent(Key.Up)
                        'B'.code -> InputEvent(Key.Down)
                        'C'.code -> InputEvent(Key.Right)
                        'D'.code -> InputEvent(Key.Left)
                        else -> unknownInput()
                    }
                }

                'O'.code -> readByte { third ->
                    when (third) {
                        'P'.code -> InputEvent(Key.Function(1))
                        'Q'.code -> InputEvent(Key.Function(2))
                        'R'.code -> InputEvent(Key.Function(3))
                        'S'.code -> InputEvent(Key.Function(4))
                        '1'.code -> readByte { fourth ->
                            if (fourth == '~'.code) {
                                InputEvent(Key.Home)
                            } else if (fourth == ';'.code) {
                                readByte { fifth ->
                                    if (fifth == '2'.code) {
                                        readByte { sixth ->
                                            when (sixth) {
                                                'A'.code -> InputEvent(Key.Up, shift = true)
                                                'B'.code -> InputEvent(Key.Down, shift = true)
                                                'C'.code -> InputEvent(Key.Right, shift = true)
                                                'D'.code -> InputEvent(Key.Left, shift = true)
                                                else -> unknownInput()
                                            }
                                        }
                                    } else {
                                        unknownInput()
                                    }
                                }
                            } else {
                                unknownInput()
                            }
                        }
                        'H'.code -> InputEvent(Key.Home)
                        'F'.code -> InputEvent(Key.End)
                        else -> unknownInput()
                    }
                }
                else -> unknownInput()
            }
        }
    }

    fun processAscii(inputByte: Int): InputEvent {
        return InputEvent(Key.CharKey(inputByte.toChar()))
    }

    fun processUnicode(firstByte: Int): InputEvent = TODO()

    return readByte { firstByte ->
        when (firstByte) {
            0 -> InputEvent(Key.Null)
            1 -> InputEvent(Key.StartOfHeading)
            2 -> InputEvent(Key.StartOfText)
            3 -> InputEvent(Key.EndOfText)
            4 -> InputEvent(Key.EndOfTransmission)
            5 -> InputEvent(Key.Enquiry)
            6 -> InputEvent(Key.Acknowledge)
            7 -> InputEvent(Key.Bell)
            8 -> InputEvent(Key.Backspace)
            9 -> InputEvent(Key.HorizontalTab)
            10 -> InputEvent(Key.LineFeed)
            11 -> InputEvent(Key.VerticalTab)
            12 -> InputEvent(Key.FormFeed)
            13 -> InputEvent(Key.CarriageReturn)
            14 -> InputEvent(Key.ShiftOut)
            15 -> InputEvent(Key.ShiftIn)
            16 -> InputEvent(Key.DataLinkEscape)
            17 -> InputEvent(Key.DeviceControl1)
            18 -> InputEvent(Key.DeviceControl2)
            19 -> InputEvent(Key.DeviceControl3)
            20 -> InputEvent(Key.DeviceControl4)
            21 -> InputEvent(Key.NegativeAcknowledge)
            22 -> InputEvent(Key.SynchronousIdle)
            23 -> InputEvent(Key.EndOfTransmissionBlock)
            24 -> InputEvent(Key.Cancel)
            25 -> InputEvent(Key.EndOfMedium)
            26 -> InputEvent(Key.Substitute)
            27 -> processEscape()
            28 -> InputEvent(Key.FileSeparator)
            29 -> InputEvent(Key.GroupSeparator)
            30 -> InputEvent(Key.RecordSeparator)
            31 -> InputEvent(Key.UnitSeparator)
            else -> {
                if (firstByte in 32..127) {
                    processAscii(firstByte)
                } else processUnicode(firstByte)
            }
        }
    }
}


fun main() {
    setRawMode()
    while (true) {
        val input = readInputEvent()
        when (input.key) {
            Key.Nothing -> {
                Thread.sleep(50)
            }

            Key.EndOfTransmission -> break
            else -> {
                print(input)
                print("\r\n")

            }
        }
    }
    println("END")
}