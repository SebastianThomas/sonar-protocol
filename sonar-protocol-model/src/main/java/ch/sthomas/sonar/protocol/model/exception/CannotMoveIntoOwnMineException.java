package ch.sthomas.sonar.protocol.model.exception;

import ch.sthomas.sonar.protocol.model.play.Location;

import java.text.MessageFormat;

public class CannotMoveIntoOwnMineException extends GameException {
    public CannotMoveIntoOwnMineException(final Location location) {
        super(MessageFormat.format("Cannot move into own mine on location {0}.", location));
    }
}
