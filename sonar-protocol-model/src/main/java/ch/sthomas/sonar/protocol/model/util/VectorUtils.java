package ch.sthomas.sonar.protocol.model.util;

import ch.sthomas.sonar.protocol.model.Location;

public class VectorUtils {
    private VectorUtils() {}

    public static Location add(final Location a, final Location b) {
        return new Location(a.x() + b.x(), a.y() + b.y());
    }
}
