package ansi.compose2

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
    if (false) {

        println()
        println(
            lineSpec(
                FixedSpec(TextBlock("Left")),
                topBottom,
                FixedSpec(TextBlock("Right"))
            ).arrange(EMPTY_BOX)
        )
    }

    if (true) {
        println()
        println(
            stackSpec(
                HorizontalRepeatSpec("-"),
                lineSpec(
                    VerticalRepeatSpec("|"),
                    FixedSpec(TextBlock("Hello")),
                    VerticalRepeatSpec("|"),
                    stackSpec(
                        FixedSpec(TextBlock("1")),
                        AlignLeft(FixedSpec(TextBlock("L"))),
                        FixedSpec(TextBlock("Hello")),
                        AlignLineCenter(FixedSpec(TextBlock("X"))),
                        //TODO right is not aligned and pull center (what?) to the left
                        AlignRight(FixedSpec(TextBlock("R")))
                    ),
                    VerticalRepeatSpec("|"),
                    FixedSpec(TextBlock("World")),
                    VerticalRepeatSpec("|")
                ),
                HorizontalRepeatSpec("-"),
            ).arrange(EMPTY_BOX)
        )
    }
}