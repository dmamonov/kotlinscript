package org.example.org.game1.geometry.box

import org.example.org.game1.algebra.Box
import org.example.org.game1.algebra.Size
import org.example.org.game1.collisions.BoxId
import java.util.stream.Collectors
import java.util.stream.Stream


private typealias SpatialHash = Long

class HashBoxContainer(val block: Size, val bounds: Size) : BoxContainer {
    private val boxes: MutableMap<BoxId, Box> = mutableMapOf()
    private val space: MutableMap<SpatialHash, MutableSet<BoxId>> = mutableMapOf()

    private fun hash(box: Box): Stream<SpatialHash> {
        check(!box.isEmpty) { "Can't hash an empty box: $box" }

        val xSlices = box.sx.split(block.width).toList()
        val ySlices = box.sy.split(block.height).toList()

        var xIndex = 0
        var yIndex = 0

        fun hash(): SpatialHash {
            val result = (ySlices[yIndex].value.toLong() shl 32) or xSlices[xIndex].value.toLong()
            if (++xIndex == xSlices.size) {
                xIndex = 0
                yIndex++
            }
            return result
        }

        return Stream.iterate(
            hash(),
            { yIndex < ySlices.size },
            { hash() }
        )
    }

    override fun insert(id: BoxId, box: Box) {
        check(id !in boxes) { "Box $id is already in the container, please use `update`" }
        check(box in bounds) { "Box $box is out of Bounds $bounds" }

        hash(box).forEach { spaceHash -> space.getOrPut(spaceHash) { HashSet() }.add(id) }
    }

    override fun update(id: BoxId, box: Box) {
        check(id in boxes) { "Box $id is not in the container, please use `insert`" }
        delete(id)
        insert(id, box)
    }

    override fun delete(id: BoxId) {
        check(id in boxes) { "Can't delete Box $id because is not in the container" }
        hash(boxes.getValue(id)).forEach { spaceHash ->
            check(space.getValue(spaceHash).remove(id)) { "Box $id not found in space hash $spaceHash" }
        }
    }

    override fun query(box: Box): Set<BoxId> {
        return hash(box).flatMap { spaceHash ->
            space.getOrDefault(spaceHash, emptySet()).stream()
        }.collect(Collectors.toSet())
    }
}
