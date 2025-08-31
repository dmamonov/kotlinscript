package ansi.compose2


import teya.ansi.compose2.AlignLineCenter
import teya.ansi.compose2.EMPTY_BOX
import teya.ansi.compose2.FixedSpec
import teya.ansi.compose2.RepeatBlock
import teya.ansi.compose2.RepeatSpec
import teya.ansi.compose2.TextBlock
import teya.ansi.compose2.line
import teya.ansi.compose2.lineSpec
import teya.ansi.compose2.stack
import teya.ansi.compose2.stackSpec


fun main() {
    println(TextBlock("Hello"))
    println()
    println(line(TextBlock("Hello"), TextBlock("World")))
    println()
    println(stack(TextBlock("Hello"), TextBlock("World")))
    println()
    println(
        lineSpec(
            FixedSpec(TextBlock("Hello")),
            RepeatSpec("|"),
            stackSpec(
                FixedSpec(TextBlock("Hello")),
                AlignLineCenter(FixedSpec(TextBlock("X")))
            ),
            RepeatSpec("|"),
            FixedSpec(TextBlock("World"))
        ).arrange(EMPTY_BOX)
    )
}