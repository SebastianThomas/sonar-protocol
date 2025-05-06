package ch.sthomas.sonar.protocol.model.exception;

public class NotSubmergedException extends GameException {
    public NotSubmergedException() {
        super("Not submerged, cannot perform action.");
    }
}
