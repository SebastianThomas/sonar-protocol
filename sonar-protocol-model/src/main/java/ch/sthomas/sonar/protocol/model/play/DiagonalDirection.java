package ch.sthomas.sonar.protocol.model.play;

import java.awt.geom.Location;

public enum DiagonalDirection {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;

    public Location direction() {
        return switch (this) {
            case NORTH -> new Location(0, -1);
            case NORTH_EAST -> new Location(1, -1);
            case EAST -> new Location(1, 0);
            case SOUTH_EAST -> new Location(1, 1);
            case SOUTH -> new Location(0, 1);
            case SOUTH_WEST -> new Location(-1, 1);
            case WEST -> new Location(-1, 0);
            case NORTH_WEST -> new Location(-1, -1);
        };
    }
}
