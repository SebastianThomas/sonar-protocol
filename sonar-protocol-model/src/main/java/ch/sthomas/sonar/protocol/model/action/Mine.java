package ch.sthomas.sonar.protocol.model.action;

import ch.sthomas.sonar.protocol.model.Path;
import ch.sthomas.sonar.protocol.model.exception.CannotCrossOwnPathException;
import ch.sthomas.sonar.protocol.model.exception.OutOfReachException;
import ch.sthomas.sonar.protocol.model.play.Location;
import ch.sthomas.sonar.protocol.model.util.VectorUtils;

public record Mine(Location location) {
    public static final int MAX_MINE_DISTANCE = 1;

    public static void checkValidLocation(final Location ship, final Location mine, final Path path)
            throws OutOfReachException, CannotCrossOwnPathException {
        final var distance = VectorUtils.distance(ship, mine);
        if (distance > MAX_MINE_DISTANCE || distance == 0) {
            throw new OutOfReachException(ship, mine, Torpedo.MAX_TORPEDO_DISTANCE);
        }
        if (path.isOnPath(mine)) {
            throw new CannotCrossOwnPathException(mine);
        }
    }
}
