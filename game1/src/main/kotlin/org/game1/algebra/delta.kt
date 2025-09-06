package org.example.org.game1.algebra

@JvmInline
value class Delta<AXIS : Axis>(val value: Int) {}

typealias DX = Delta<XAxis>
typealias DY = Delta<YAxis>

data class DXY(val dx: DX, val dy: DY)