package ch.sthomas.sonar.protocol.model.exception;

import ch.sthomas.sonar.protocol.model.play.Location;

import java.text.MessageFormat;

public class CannotCrossOwnPathException extends GameException {
    public CannotCrossOwnPathException(final Location location) {
        super(MessageFormat.format("Cannot cross own path on location {0}.", location));
    }
}
