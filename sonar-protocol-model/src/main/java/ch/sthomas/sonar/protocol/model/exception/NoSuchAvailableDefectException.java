package ch.sthomas.sonar.protocol.model.exception;

import ch.sthomas.sonar.protocol.model.play.DefectLocation;
import ch.sthomas.sonar.protocol.model.play.Direction;

public class NoSuchAvailableDefectException extends GameException {
    private final Direction direction;
    private final DefectLocation defectLocation;

    public NoSuchAvailableDefectException(
            final Direction direction, final DefectLocation defectLocation) {
        super("There is no such non-crossed defect for this ship.");
        this.direction = direction;
        this.defectLocation = defectLocation;
    }
}
