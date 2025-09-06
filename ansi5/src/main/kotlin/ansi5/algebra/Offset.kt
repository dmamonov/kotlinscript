package ansi5.algebra

@JvmInline
value class Offset<AXIS: Axis>(val value: Int): Comparable<Offset<AXIS>> {
    override fun compareTo(other: Offset<AXIS>): Int = this.value.compareTo(other.value)

    override fun toString(): String = "$value"
}

typealias RowOffset = Offset<Vertical>
typealias ColOffset = Offset<Horizontal>

data class Delta(val rowOffset: RowOffset, val colOffset: ColOffset) {
}