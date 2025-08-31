package ansi.compose2

abstract class Spec {
    abstract fun minFit(): Box
    abstract fun arrange(minSlot: Box): Block
}

class EmptySpec : Spec() {
    override fun minFit(): Box = EMPTY_BOX

    override fun arrange(minSlot: Box): Block = EmptyBlock(minSlot)
}

class FixedSpec(val block: Block) : Spec() {
    override fun minFit(): Box = block.box

    override fun arrange(minSlot: Box): Block {
        return block
    }
}

class VerticalRepeatSpec(val symbol: String) : Spec() {
    override fun minFit(): Box = UNIT_BOX

    override fun arrange(minSlot: Box): Block {
        val fitBounds = minFit().minHeight(minSlot.height)
        return RepeatBlock(fitBounds, symbol)
    }
}

class HorizontalRepeatSpec(val symbol: String) : Spec() {
    override fun minFit(): Box = UNIT_BOX

    override fun arrange(minSlot: Box): Block {
        val fitBounds = minFit().minWidth(minSlot.width)
        return RepeatBlock(fitBounds, symbol)
    }
}

class AlignLeft(private val spec: Spec) : Spec() {
    override fun minFit(): Box = spec.minFit()

    override fun arrange(minSlot: Box): Block {
        val block = spec.arrange(minSlot.minBox(minFit()))
        val padding: Box = minSlot - block.box.width
        return if (padding.hasArea) {
            LineBlock(block, EmptyBlock(padding))
        } else block
    }
}

class AlignRight(private val spec: Spec) : Spec() {
    override fun minFit(): Box = spec.minFit()

    override fun arrange(minSlot: Box): Block {
        val block = spec.arrange(minSlot)
        val padding: Box = minSlot - block.box.width
        return if (padding.hasArea) {
            LineBlock(EmptyBlock(padding), block)
        } else block
    }
}

class AlignLineCenter(private val spec: Spec) : Spec() {
    override fun minFit(): Box = spec.minFit()

    override fun arrange(minSlot: Box): Block {
        val block = spec.arrange(minSlot)
        val padding: Box = minSlot - block.box.width
        val leftPadding = padding.withWidth(padding.width / 2)
        val rightPadding = padding - leftPadding.width
        return if (leftPadding.hasArea) {
            LineBlock(
                EmptyBlock(leftPadding),
                LineBlock(
                    block,
                    EmptyBlock(rightPadding)
                )
            )
        } else if (rightPadding.hasArea) {
            LineBlock(
                block,
                EmptyBlock(rightPadding)
            )
        } else block
    }
}


class StackSpec(private val top: Spec, private val bottom: Spec) : Spec() {
    val topFit = top.minFit()
    val bottomFit = bottom.minFit()

    override fun minFit(): Box {
        return Box(
            maxOf(
                topFit.width,
                bottomFit.width
            ),
            topFit.height + bottomFit.height
        )
    }

    override fun arrange(minSlot: Box): Block {
        val fitBounds = minFit().minHeight(minSlot.height)
        return StackBlock(
            top.arrange(fitBounds.withHeight(topFit.height)),
            bottom.arrange(fitBounds.withHeight(bottomFit.height))
        )
    }
}


fun stackSpec(vararg specs: Spec): Spec {
    return when (specs.size) {
        0 -> FixedSpec(EmptyBlock())
        1 -> specs[0]
        else -> StackSpec(specs[0], stackSpec(*specs.toList().subList(1, specs.size).toTypedArray()))
    }
}

class LineSpec(private val left: Spec, private val right: Spec) : Spec() {
    val leftFit = left.minFit()
    val rightFit = right.minFit()

    override fun minFit(): Box {
        return Box(
            leftFit.width + rightFit.width,
            maxOf(
                leftFit.height,
                rightFit.height
            )
        )
    }

    override fun arrange(minSlot: Box): Block {
        val fitBounds = minFit().minHeight(minSlot.height)
        return LineBlock(
            left.arrange(fitBounds.withWidth(leftFit.width)),
            right.arrange(fitBounds.withWidth(rightFit.width))
        )
    }
}

fun lineSpec(vararg specs: Spec): Spec {
    return when (specs.size) {
        0 -> FixedSpec(EmptyBlock())
        1 -> specs[0]
        else -> LineSpec(specs[0], lineSpec(*specs.toList().subList(1, specs.size).toTypedArray()))
    }
}