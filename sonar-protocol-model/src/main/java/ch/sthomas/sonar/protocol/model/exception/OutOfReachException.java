package ch.sthomas.sonar.protocol.model.exception;

import ch.sthomas.sonar.protocol.model.play.Location;

import java.text.MessageFormat;

public class OutOfReachException extends GameException {
    public OutOfReachException(
            final Location ship, final Location target, final int maxAllowedDistance) {
        super(
                MessageFormat.format(
                        "Location is too far for allowed maximum distance {0}: {1} <-> {2}",
                        maxAllowedDistance, ship, target));
    }
}
