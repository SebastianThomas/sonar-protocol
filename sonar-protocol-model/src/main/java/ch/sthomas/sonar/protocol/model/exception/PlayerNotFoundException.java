package ch.sthomas.sonar.protocol.model.exception;

import java.text.MessageFormat;

public class PlayerNotFoundException extends GameException {
    public PlayerNotFoundException(final long id) {
        super(MessageFormat.format("Player not found: {0}.", id));
    }
}
