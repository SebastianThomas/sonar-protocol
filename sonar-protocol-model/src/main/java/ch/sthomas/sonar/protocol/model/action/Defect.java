package ch.sthomas.sonar.protocol.model.action;

import ch.sthomas.sonar.protocol.model.play.DefectLocation;
import ch.sthomas.sonar.protocol.model.play.Direction;

public record Defect(
        DefectLocation location, ActionCategory action, Direction direction, boolean critical) {
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Defect other)) {
            return false;
        }

        return location.equals(other.location) && direction == other.direction;
    }
}
