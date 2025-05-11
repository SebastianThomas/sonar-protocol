package ch.sthomas.sonar.protocol.model.exception;

public abstract class GameException extends RuntimeException {
    protected GameException(final String message) {
        super(message);
    }
}
