package ch.sthomas.sonar.protocol.model.exception;

import ch.sthomas.sonar.protocol.model.play.XYAccessor;

import java.text.MessageFormat;

public class OutOfBoundsException extends GameException {
    public OutOfBoundsException(final XYAccessor point) {
        super(MessageFormat.format("Coordinate {0} is out of bounds.", point.getPointString()));
    }
}
