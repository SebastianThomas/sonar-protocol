package ch.sthomas.sonar.protocol.model.exception;

public class PreviousRoundNotFinishedException extends GameException {
    public PreviousRoundNotFinishedException() {
        super("Previous round not finished, cannot proceed with next round.");
    }
}
