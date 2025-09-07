package org.example.org.game1.physics

import org.example.org.game1.algebra.SX
import org.example.org.game1.algebra.SY
import org.example.org.game1.algebra.X
import org.example.org.game1.algebra.Y


sealed interface BodySpec {
    val constraints: DynamicBodyConstraints
}

data class DynamicBodyConstraints(
    val maxForce: Force,
    val maxAcceleration: Acceleration,
    val maxVelocity: Velocity,
)

data class DynamicBodySpec(
    val mass: Kilogram,
    override val constraints: DynamicBodyConstraints,
) : BodySpec

object StaticBodySpec : BodySpec {
    override val constraints: DynamicBodyConstraints = DynamicBodyConstraints(
        maxForce = Force(0.0),
        maxAcceleration = Acceleration(0.0),
        maxVelocity = Velocity(0.0),
    )
}

data class BodyKinematics(
    val position: Position2D,
    val velocity: Velocity2D,
)

@JvmInline
value class BodyId(private val value: Int = ++sequence) {
    companion object {
        private var sequence: Int = 0
    }
}

interface Joint {

}

data class HorizontalContact(
    val y: Y,
    val sx: SX
) : Joint


data class VerticalContact(
    val x: X,
    val sy: SY,
) : Joint


/** Complete immutable body */
data class Body(
    val id: BodyId,
    val spec: BodySpec,
    val kinematics: BodyKinematics,
    val joints: List<Joint>
)
