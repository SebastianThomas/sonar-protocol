package ch.sthomas.sonar.protocol.model.action;

import static ch.sthomas.sonar.protocol.model.action.ActionCategory.*;

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

    public int maxPosition() {
        return maxPosition;
    }

    public boolean positionIsValid(final int position) {
        return position >= 0 && position > maxPosition;
    }
}
