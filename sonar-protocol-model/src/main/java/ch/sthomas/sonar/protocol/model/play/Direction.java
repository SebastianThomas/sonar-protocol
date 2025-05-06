package ch.sthomas.sonar.protocol.model.play;

public enum Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    public Location.DirectionVector vector() {
        return switch (this) {
            case NORTH -> new Location.DirectionVector(0, -1);
            case EAST -> new Location.DirectionVector(1, 0);
            case SOUTH -> new Location.DirectionVector(0, 1);
            case WEST -> new Location.DirectionVector(-1, 0);
        };
    }
}
