package ch.sthomas.sonar.protocol.model.util;

import ch.sthomas.sonar.protocol.model.exception.OutOfBoundsException;
import ch.sthomas.sonar.protocol.model.play.Location;
import ch.sthomas.sonar.protocol.model.play.XYAccessor;

public class VectorUtils {
    public static final int MIN_X = 0;
    public static final int MAX_X = 14;
    public static final int MIN_Y = 0;
    public static final int MAX_Y = 14;
    public static final Location MIN = new Location(MIN_X, MIN_Y);
    public static final Location MAX = new Location(MAX_X, MAX_Y);

    private VectorUtils() {}

    public static Location add(final Location a, final XYAccessor b) throws OutOfBoundsException {
        final var newLocation = addUnsafe(a, b);
        if (isInBounds(newLocation)) {
            return newLocation;
        }
        throw new OutOfBoundsException(newLocation);
    }

    public static boolean isInBounds(final Location location) {
        return location.x() >= MIN.x()
                && location.x() <= MAX.x()
                && location.y() >= MIN.y()
                && location.y() <= MAX.y();
    }

    static Location addUnsafe(final Location a, final XYAccessor b) {
        return new Location(a.x() + b.x(), a.y() + b.y());
    }
}
