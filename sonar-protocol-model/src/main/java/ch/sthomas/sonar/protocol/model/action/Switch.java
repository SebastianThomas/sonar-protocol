package ch.sthomas.sonar.protocol.model.action;

import ch.sthomas.sonar.protocol.model.Ship;
import ch.sthomas.sonar.protocol.model.exception.SwitchValueInvalidException;

public record Switch(Ship ship, Action action, int position) {
    /**
     * @throws SwitchValueInvalidException if the given {@code position} is out of bounds for {@code
     *     action}
     */
    public Switch {
        if (!action().positionIsValid(position)) {
            throw new SwitchValueInvalidException(position, action);
        }
    }
}
