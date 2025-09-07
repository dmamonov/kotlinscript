package org.example.org.game1.physics

class Simulation(val world: World) {
    val bodies = mutableListOf<Body>()


}


data class World(
    val gravity: Acceleration2D,
    val groundFriction: Double, // dimensionless factor μ
    val airDrag: Double = 0.0   // optional damping coefficient
)

data class BodyControl(
    val runAccelerationX: Acceleration, // ± for left/right
    val jumpRequested: Boolean,
    val velocity: Velocity2D,
)


data class NextForce(
    val body: Body,
    val force: Force2D
) {
    fun applyConstrains(): NextForce = TODO()
}

data class NextAcceleration(
    val body: Body,
    val force: Force2D,
    val acceleration: Acceleration2D,
) {
    fun applyConstrains(): NextAcceleration = TODO()
}

data class NextVelocity(
    val body: Body,
    val force: Force2D,
    val acceleration: Acceleration2D,
    val velocity: Velocity2D,
) {
    fun applyConstrains(): NextVelocity = TODO()
}

data class NextPosition(
    val body: Body,
    val force: Force2D,
    val acceleration: Acceleration2D,
    val velocity: Velocity2D,
    val position: Position2D,
) {
    fun applyConstrains(): NextPosition = TODO()
}

fun activeBodies(): List<Body> = TODO()

fun computeControl(body: Body): BodyControl = TODO()

fun computeNextForce(body: Body, world: World, control: BodyControl): NextForce {
    return NextForce(body, Force2D(0.0, 0.0))
}

fun computeNextAcceleration(prev: NextForce, world: World, control: BodyControl): NextAcceleration {
    return NextAcceleration(
        prev.body,
        prev.force,
        Acceleration2D(0.0, 0.0)
    )
}

fun computeNextVelocity(prev: NextAcceleration, world: World, control: BodyControl): NextVelocity {
    return NextVelocity(
        prev.body,
        prev.force,
        prev.acceleration,
        Velocity2D(0.0, 0.0)
    )
}

fun computeNextPosition(
    prev: NextVelocity, world: World, control: BodyControl, deltaTime: Second
): NextPosition {
    val nextPosition: Position2D = prev.body.kinematics.position + control.velocity * deltaTime
    return NextPosition(
        prev.body,
        prev.force,
        prev.acceleration,
        prev.velocity,
        nextPosition
    )
}

fun computeNextBody(prev: NextPosition): Body? {
    val kinematics = prev.body.kinematics
    return if (kinematics.position != prev.position
        || kinematics.velocity != prev.velocity
    ) {
        prev.body.copy(
            kinematics = BodyKinematics(
                position = prev.position,
                velocity = prev.velocity,
            )
        )
    } else null
}

fun simulate(world: World, deltaTime: Second) {
    val updatedBodies = mutableListOf<Body>()
    activeBodies().forEach { body ->
        val control = computeControl(body)
        val nextForce = computeNextForce(body, world, control).applyConstrains() //TODO joints
        val nextAcceleration = computeNextAcceleration(nextForce, world, control).applyConstrains()
        val nextVelocity = computeNextVelocity(nextAcceleration, world, control).applyConstrains()
        val nextPosition = computeNextPosition(nextVelocity, world, control, deltaTime).applyConstrains()
        val nextBody = computeNextBody(nextPosition)
        if (nextBody != null) {
            updatedBodies.add(nextBody)
        }
    }
}
