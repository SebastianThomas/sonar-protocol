package ch.sthomas.sonar.protocol.model.exception;

import ch.sthomas.sonar.protocol.model.action.Action;

import java.text.MessageFormat;

public class SwitchValueInvalidException extends GameRuntimeException {
    public SwitchValueInvalidException(final int position, final Action action) {
        super(
                MessageFormat.format(
                        "Position with value {0} is greater than allowed for {1} ({2}).",
                        position, action.name(), action.maxPosition()));
    }
}
