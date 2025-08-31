package teya.ansi.compose2

abstract class Spec {
    abstract fun fit(): Box
    abstract fun arrange(bounds: Box): Block
}

class EmptySpec : Spec() {
    override fun fit(): Box = EMPTY_BOX

    override fun arrange(bounds: Box): Block = EmptyBlock(bounds)
}

class FixedSpec(val block: Block) : Spec() {
    override fun fit(): Box = block.box

    override fun arrange(bounds: Box): Block {
        return block
    }
}

class RepeatSpec(val symbol: String) : Spec() {
    override fun fit(): Box = UNIT_BOX

    override fun arrange(bounds: Box): Block {
        val fitBounds = bounds.minBox(fit())
        return RepeatBlock(fitBounds, symbol)
    }
}

class AlignLeft(private val spec: Spec) : Spec() {
    override fun fit(): Box = spec.fit()

    override fun arrange(bounds: Box): Block {
        val block = spec.arrange(bounds)
        val padding: Box = bounds - block.box.width
        return if (padding.hasArea) {
            line(block, EmptyBlock(padding))
        } else block
    }
}

class AlignRight(private val spec: Spec) : Spec() {
    override fun fit(): Box = spec.fit()

    override fun arrange(bounds: Box): Block {
        val block = spec.arrange(bounds)
        val padding: Box = bounds - block.box.width
        return if (padding.hasArea) {
            line(EmptyBlock(padding), block)
        } else block
    }
}

class AlignLineCenter(private val spec: Spec) : Spec() {
    override fun fit(): Box = spec.fit()

    override fun arrange(bounds: Box): Block {
        val block = spec.arrange(bounds)
        val padding: Box = bounds - block.box.width
        val leftPadding = padding.toWidth(padding.width / 2)
        val rightPadding = padding - leftPadding.width
        return if (leftPadding.hasArea) {
            line(
                EmptyBlock(leftPadding),
                block,
                EmptyBlock(rightPadding)
            )
        } else if (rightPadding.hasArea) {
            line(
                block,
                EmptyBlock(rightPadding)
            )
        } else block
    }
}


class StackSpec(private val top: Spec, private val bottom: Spec) : Spec() {
    override fun fit(): Box {
        val topFit = top.fit()
        val bottomFit = bottom.fit()
        return Box(
            maxOf(
                topFit.width,
                bottomFit.width
            ),
            topFit.height + bottomFit.height
        )
    }

    override fun arrange(bounds: Box): Block {
        val fitBounds = bounds.minBox(fit())
        return StackBlock(
            top.arrange(fitBounds),
            bottom.arrange(fitBounds)
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
    override fun fit(): Box {
        val leftFit = left.fit()
        val rightFit = right.fit()
        return Box(
            leftFit.width + rightFit.width,
            maxOf(
                leftFit.height,
                rightFit.height
            )
        )
    }

    override fun arrange(bounds: Box): Block {
        val fitBounds = bounds.minBox(fit())
        return LineBlock(
            left.arrange(fitBounds),
            right.arrange(fitBounds)
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