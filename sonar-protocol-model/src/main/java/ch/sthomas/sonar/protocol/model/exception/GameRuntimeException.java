package ch.sthomas.sonar.protocol.model.exception;

public class GameRuntimeException extends RuntimeException {
    public GameRuntimeException(String message) {
        super(message);
    }
}
