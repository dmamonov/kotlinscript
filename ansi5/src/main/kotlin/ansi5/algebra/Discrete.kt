package ansi5.algebra

@JvmInline
value class Discrete<TYPE>(val value: Int) : Comparable<Discrete<TYPE>> {
    override fun compareTo(other: Discrete<TYPE>): Int = this.value.compareTo(other.value)

    override fun toString(): String = "$value"
}