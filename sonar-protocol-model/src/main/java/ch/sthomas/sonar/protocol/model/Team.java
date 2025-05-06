package ch.sthomas.sonar.protocol.model;

import java.util.List;

public record Team(long id, List<Player> players, Ship ship) {
    public enum ID {
        A,
        B;
    }
}
