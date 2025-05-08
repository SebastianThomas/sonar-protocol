package ch.sthomas.sonar.protocol.model.exception;

import java.text.MessageFormat;

public class GameNotFoundException extends GameException {
    public GameNotFoundException(final long id) {
        super(MessageFormat.format("Game not found: {0}.", id));
    }
}
