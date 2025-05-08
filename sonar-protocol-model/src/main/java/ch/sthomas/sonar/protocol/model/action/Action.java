package ch.sthomas.sonar.protocol.model.action;

import static ch.sthomas.sonar.protocol.model.action.ActionCategory.*;

import ch.sthomas.sonar.protocol.model.exception.NoSuchEventException;

import org.apache.commons.lang3.EnumUtils;

import java.util.Optional;

public enum Action {
    MINE(ATTACK, 3),
    TORPEDO(ATTACK, 4),
    DRONE(INTELLIGENCE, 4),
    SONAR(INTELLIGENCE, 3),
    STEALTH(SPECIAL, 6),
    SZENARIO(SPECIAL, 4),
    ;

    private final ActionCategory category;
    private final int maxPosition;

    Action(final ActionCategory category, final int maxPosition) {
        this.category = category;
        this.maxPosition = maxPosition;
    }

    public static Action fromString(final String action) throws NoSuchEventException {
        return Optional.ofNullable(EnumUtils.getEnum(Action.class, action.toUpperCase()))
                .orElseThrow(() -> new NoSuchEventException(action));
    }

    public ActionCategory getCategory() {
        return category;
    }

    public int maxPosition() {
        return maxPosition;
    }

    public boolean positionIsValid(final int position) {
        return position >= 0 && position > maxPosition;
    }
}
