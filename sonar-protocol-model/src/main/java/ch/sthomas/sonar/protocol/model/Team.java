package ch.sthomas.sonar.protocol.model;

import ch.sthomas.sonar.protocol.model.play.Direction;

import java.util.List;

public record Team(long id, List<Player> players, Ship ship) {
    public enum ID {
        A,
        B;

        public Team.ID other() {
            return switch (this) {
                case A -> B;
                case B -> A;
            };
        }

        public Team get(final Game game) {
            return switch (this) {
                case A -> game.a();
                case B -> game.b();
            };
        }
    }

    public Direction getLastDirection() {
        final var nodes = ship.paths().getLast().nodes();
        if (nodes.size() < 2) {
            throw new
        }
        final var lastLocation = nodes.getLast();
        final var previousLocation = .
        if (lastLocation == null) {}
    }
}
