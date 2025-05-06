package ch.sthomas.sonar.protocol.model.exception;

public class NotSurfacedException extends GameException {
    public NotSurfacedException() {
        super("Not surfaced, cannot perform action.");
    }
}
