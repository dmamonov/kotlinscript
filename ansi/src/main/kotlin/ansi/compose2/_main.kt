package ansi.compose2


import teya.ansi.compose2.AlignLeft
import teya.ansi.compose2.AlignLineCenter
import teya.ansi.compose2.EMPTY_BOX
import teya.ansi.compose2.FixedSpec
import teya.ansi.compose2.VerticalRepeatSpec
import teya.ansi.compose2.TextBlock
import teya.ansi.compose2.lineSpec
import teya.ansi.compose2.stackSpec


fun main() {
    if (false) {
        println(FixedSpec(TextBlock("One Word")).arrange(EMPTY_BOX))
        println()
        println(
            lineSpec(
                FixedSpec(TextBlock("Left")),
                FixedSpec(TextBlock("Right"))
            ).arrange(EMPTY_BOX)
        )

    }

    val topBottom = stackSpec(
        FixedSpec(TextBlock("Top")),
        FixedSpec(TextBlock("Bottom"))
    )
    if (false) {
        println()
        println(
            topBottom.arrange(EMPTY_BOX)
        )
    }
    if (true) {

        println()
        println(
            lineSpec(
                FixedSpec(TextBlock("Left")),
                topBottom,
                FixedSpec(TextBlock("Right"))
            ).arrange(EMPTY_BOX)
        )
    }

    if (false) {
        println()
        println(
            lineSpec(
                VerticalRepeatSpec("|"),
                FixedSpec(TextBlock("Hello")),
                VerticalRepeatSpec("|"),
                stackSpec(
                    FixedSpec(TextBlock("1")),
                    AlignLeft(FixedSpec(TextBlock("L"))),
                    FixedSpec(TextBlock("Hello")),
                    AlignLineCenter(FixedSpec(TextBlock("X")))
                ),
                VerticalRepeatSpec("|"),
                FixedSpec(TextBlock("World")),
                VerticalRepeatSpec("|")
            ).arrange(EMPTY_BOX)
        )
    }
}