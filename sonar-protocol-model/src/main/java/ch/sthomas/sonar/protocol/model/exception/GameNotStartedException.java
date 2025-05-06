package ch.sthomas.sonar.protocol.model.exception;

public class GameNotStartedException extends GameException {
    public GameNotStartedException() {
        super("Game not started, cannot perform action.");
    }
}
