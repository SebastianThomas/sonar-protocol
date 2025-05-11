package ch.sthomas.sonar.protocol.model.action;

import ch.sthomas.sonar.protocol.model.exception.OutOfReachException;
import ch.sthomas.sonar.protocol.model.play.Location;
import ch.sthomas.sonar.protocol.model.util.VectorUtils;

public record Torpedo(Location location) {
    public static final int MAX_TORPEDO_DISTANCE = 4;

    public static void checkValidLocation(final Location ship, final Location torpedo)
            throws OutOfReachException {
        final var distance = VectorUtils.distance(ship, torpedo);
        if (distance > MAX_TORPEDO_DISTANCE || distance == 0) {
            throw new OutOfReachException(ship, torpedo, Torpedo.MAX_TORPEDO_DISTANCE);
        }
    }
}
