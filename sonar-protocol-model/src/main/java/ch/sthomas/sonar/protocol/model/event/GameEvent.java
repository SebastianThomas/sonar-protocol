package ch.sthomas.sonar.protocol.model.event;

import static ch.sthomas.sonar.protocol.model.event.GameEventNotificationPolicy.*;

import ch.sthomas.sonar.protocol.model.exception.NoSuchEventException;

import org.apache.commons.lang3.EnumUtils;

import java.util.Optional;

public enum GameEvent {
    CREATE_PLAYER(NONE),
    REJOIN_GAME(GAME),
    JOIN(GAME),
    SET_START_POSITION(TEAM),
    START(GAME),
    MOVE(GAME),
    SURFACE(GAME),
    SUBMERGE(GAME),
    ;

    private final GameEventNotificationPolicy notificationPolicy;

    GameEvent(final GameEventNotificationPolicy notificationPolicy) {
        this.notificationPolicy = notificationPolicy;
    }

    public static GameEvent fromString(final String topic) throws NoSuchEventException {
        return Optional.ofNullable(EnumUtils.getEnum(GameEvent.class, topic.toUpperCase()))
                .orElseThrow(() -> new NoSuchEventException(topic));
    }

    public <T> GameEventMessage<T> createMessage(final long gameId, final T data) {
        return new GameEventMessage<>(gameId, notificationPolicy, this, data);
    }
}
