package ch.sthomas.sonar.protocol.model.exception;

import java.text.MessageFormat;

public class NoSuchEventException extends GameException {
    public NoSuchEventException(final String event) {
        super(MessageFormat.format("No such event: {0}", event));
    }
}
