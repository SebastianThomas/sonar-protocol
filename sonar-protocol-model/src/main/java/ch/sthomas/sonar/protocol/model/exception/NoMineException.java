package ch.sthomas.sonar.protocol.model.exception;

import ch.sthomas.sonar.protocol.model.play.Location;

import java.text.MessageFormat;

public class NoMineException extends GameException {
    public NoMineException(final Location location) {
        super(MessageFormat.format("No mine at location {0}", location));
    }
}
