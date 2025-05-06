package ch.sthomas.sonar.protocol.model.play;

import ch.sthomas.sonar.protocol.model.Location;

import java.awt.geom.Location;

public enum Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    public Location vector() {
        return switch (this) {
            case NORTH -> new Location(0, -1);
            case EAST -> new Location(1, 0);
            case SOUTH -> new Location(0, 1);
            case WEST -> new Location(-1, 0);
        };
    }
}
