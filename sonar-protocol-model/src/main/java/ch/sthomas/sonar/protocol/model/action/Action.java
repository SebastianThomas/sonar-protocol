package ch.sthomas.sonar.protocol.model.action;

import static ch.sthomas.sonar.protocol.model.action.ActionCategory.*;

import ch.sthomas.sonar.protocol.model.event.GameEventNotificationPolicy;
import ch.sthomas.sonar.protocol.model.exception.NoSuchEventException;

import org.apache.commons.lang3.EnumUtils;

import java.util.Optional;

public enum Action {
    MINE(ATTACK, 3, GameEventNotificationPolicy.TEAM),
    TORPEDO(ATTACK, 4, GameEventNotificationPolicy.GAME),
    DRONE(INTELLIGENCE, 4, GameEventNotificationPolicy.GAME),
    SONAR(INTELLIGENCE, 3, GameEventNotificationPolicy.TEAM),
    STEALTH(SPECIAL, 6, GameEventNotificationPolicy.TEAM),
    SZENARIO(SPECIAL, 4, GameEventNotificationPolicy.GAME),
    ;

    private final ActionCategory category;
    private final int maxPosition;
    private final GameEventNotificationPolicy notificationPolicy;

    Action(
            final ActionCategory category,
            final int maxPosition,
            final GameEventNotificationPolicy notificationPolicy) {
        this.category = category;
        this.maxPosition = maxPosition;
        this.notificationPolicy = notificationPolicy;
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

    public GameEventNotificationPolicy notificationPolicy() {
        return notificationPolicy;
    }
}
