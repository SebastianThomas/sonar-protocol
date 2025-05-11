package ch.sthomas.sonar.protocol.model;

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
}
