package cn.mapway.globe.server.gabage;

import jsinterop.base.Js;

/**
 * A rotation expressed as a heading, pitch, and roll. Heading is the rotation about the
 * negative z axis. Pitch is the rotation about the negative y axis. Roll is the rotation about
 * the positive x axis.
 */
public class HeadingPitchRoll {

    public double heading;
    public double pitch;
    public double roll;

    public HeadingPitchRoll() {
        heading = 0.0;
        pitch = 0.0;
        roll = 0.0;
    }

    /**
     * Creates a new HeadingPitchRoll.
     * @param heading The heading component in radians.
     * @param pitch The pitch component in radians.
     * @param roll The roll component in radians.
     */
    public HeadingPitchRoll(double heading, double pitch, double roll) {
        this.heading = heading;
        this.pitch = pitch;
        this.roll = roll;
    }

    /**
     * Returns a new HeadingPitchRoll instance from angles given in degrees.
     * @param heading the heading in degrees
     * @param pitch the pitch in degrees
     * @param roll the roll in degrees
     * @return
     */
    public HeadingPitchRoll fromDegrees(double heading, double pitch, double roll) {
        this.heading = Maths.toRadians(heading);
        this.pitch = Maths.toRadians(pitch);
        this.roll = Maths.toRadians(roll);
        return this;
    }

    public HeadingPitchRoll clone() {
        return new HeadingPitchRoll(heading, pitch, roll);
    }

    public boolean equals(HeadingPitchRoll other) {
        return Js.isTripleEqual(heading, other.heading)
                && Js.isTripleEqual(pitch, other.pitch)
                && Js.isTripleEqual(roll, other.roll);
    }

    public boolean equalsEpsilon(HeadingPitchRoll other, double relativeEpsilon, double absoluteEpsilon) {
        return Maths.equalsEpsilon(heading, other.heading, relativeEpsilon, absoluteEpsilon)
                && Maths.equalsEpsilon(pitch, other.pitch, relativeEpsilon, absoluteEpsilon)
                && Maths.equalsEpsilon(roll, other.roll, relativeEpsilon, absoluteEpsilon);
    }

    public String toString() {
        return "(" + heading + "," + pitch + "," + roll + ")";
    }


}
