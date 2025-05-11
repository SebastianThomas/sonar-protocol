package ch.sthomas.sonar.protocol.model.play;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Sector {
    NORTH_WEST(1),
    NORTH(2),
    NORTH_EAST(3),
    WEST(4),
    MIDDLE(5),
    EAST(6),
    SOUTH_WEST(7),
    SOUTH(8),
    SOUTH_EAST(9),
    ;
    private static final Map<Integer, Sector> VALUES =
            Arrays.stream(Sector.values())
                    .map(s -> Map.entry(s.value, s))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private final int value;

    Sector(final int value) {
        this.value = value;
    }

    @JsonCreator
    public static Sector fromValue(final int value) {
        return VALUES.get(value);
    }

    public boolean contains(final Location location) {
        return this == fromLocation(location);
    }

    public static Sector fromLocation(final Location location) {
        return fromValue(location.x() / 5 + 1 + 3 * (location.y() / 5));
    }
}
