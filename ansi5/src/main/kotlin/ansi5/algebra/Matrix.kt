package ansi5.algebra

interface Matrix<CELL> {
    val size: Size
    operator fun get(point: Point): CELL?

    val transpose: Matrix<CELL>
        get() = object : Matrix<CELL> {
            override val size: Size = this@Matrix.size.transpose()

            override fun get(point: Point): CELL? = this@Matrix[point.transpose()]
        }

    operator fun plus(delta: Delta): Matrix<CELL> = object : Matrix<CELL> {
        override val size: Size = this@Matrix.size + delta

        override fun get(point: Point): CELL? {
            val deltaPoint = point - delta
            return if (deltaPoint in this@Matrix.size) {
                this@Matrix[deltaPoint]
            } else null
        }
    }

    fun crop(size: Size): Matrix<CELL> = object : Matrix<CELL> {
        override val size: Size = size
        override fun get(point: Point): CELL? = if (point in size) {
            this@Matrix[point]
        } else {
            null
        }
    }

    operator fun div(bottom: Matrix<CELL>): Matrix<CELL> = object : Matrix<CELL> {
        override val size: Size = Size(
            width = maxOf(this@Matrix.size.width, bottom.size.width),
            height = this@Matrix.size.height + bottom.size.height
        )

        override fun get(point: Point): CELL? = if (point in this@Matrix.size) {
            this@Matrix[point]
        } else {
            val bottomPoint = point minusRows this@Matrix.size.height.asOffset
            if (bottomPoint in bottom.size) {
                bottom[bottomPoint]
            } else null
        }
    }

    operator fun plus(right: Matrix<CELL>): Matrix<CELL> = object : Matrix<CELL> {
        override val size: Size = Size(
            width = this@Matrix.size.width + right.size.width,
            height = maxOf(
                this@Matrix.size.height,
                right.size.height
            )
        )

        override fun get(point: Point): CELL? = if (point in this@Matrix.size) {
            this@Matrix[point]
        } else {
            val rightPoint = point minusCols this@Matrix.size.width.asOffset
            if (rightPoint in right.size) {
                right[rightPoint]
            } else null
        }
    }

    infix fun over(under: Matrix<CELL>): Matrix<CELL> = object : Matrix<CELL> {
        override val size: Size = Size(
            width = maxOf(this@Matrix.size.width, under.size.width),
            height = maxOf(this@Matrix.size.height, under.size.height),
        )

        override fun get(point: Point): CELL? = this@Matrix[point.transpose()] ?: under[point]
    }

    fun fill(cell: CELL): Matrix<CELL> = object : Matrix<CELL> {
        override val size: Size get() = this@Matrix.size
        override fun get(point: Point): CELL? = if (point in this@Matrix.size) {
            this@Matrix[point] ?: cell
        } else null
    }

    fun clear(skip: (CELL) -> Boolean): Matrix<CELL> = object : Matrix<CELL> {
        override val size: Size get() = this@Matrix.size
        override fun get(point: Point): CELL? = this@Matrix[point]?.let { if (skip(it)) null else it }
    }

    fun process(processor: (CELL) -> CELL?): Matrix<CELL> = object : Matrix<CELL> {
        override val size: Size = this@Matrix.size

        override fun get(point: Point): CELL? {
            val cell = this@Matrix[point] ?: return null
            return processor(cell)
        }
    }
}