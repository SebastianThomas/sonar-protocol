package ch.sthomas.sonar.protocol.model.event;

import static ch.sthomas.sonar.protocol.model.event.GameEventNotificationPolicy.*;

import ch.sthomas.sonar.protocol.model.Player;
import ch.sthomas.sonar.protocol.model.api.PerformedActionData;
import ch.sthomas.sonar.protocol.model.exception.NoSuchEventException;

import org.apache.commons.lang3.EnumUtils;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public enum GameEvent {
    // Game
    CREATE_PLAYER(NONE),
    REJOIN_GAME(GAME),
    JOIN(GAME),
    SET_START_POSITION(TEAM),

    // Captain
    START(GAME),
    MOVE(GAME),
    SURFACE(GAME),
    SUBMERGE(GAME),

    SWITCH(TEAM),
    CROSS_DEFECT(TEAM),

    // Actions
    ACTION(FROM_ACTION),
    SONAR_ANSWER(GAME),
    EXPLODE_MINE(GAME),
    ;

    private final GameEventNotificationPolicy notificationPolicy;

    GameEvent(final GameEventNotificationPolicy notificationPolicy) {
        this.notificationPolicy = notificationPolicy;
    }

    public GameEventNotificationPolicy notificationPolicy() {
        return notificationPolicy;
    }

    public static GameEvent fromString(final String topic) throws NoSuchEventException {
        return Optional.ofNullable(EnumUtils.getEnum(GameEvent.class, topic.toUpperCase()))
                .orElseThrow(() -> new NoSuchEventException(topic));
    }

    public <T> GameEventMessage<T> createMessage(
            final long gameId, final Collection<Player> notifiedPlayers, final T data) {
        return new GameEventMessage<>(
                gameId, Instant.now(), notifiedPlayers, notificationPolicy(), this, data);
    }

    public <T, R> Collection<GameEventMessage<PerformedActionData<?, ?>>> createMessage(
            final long gameId,
            final Collection<Player> teamThis,
            final Collection<Player> teamOther,
            final PerformedActionData<T, R> data) {
        return switch (this) {
            case ACTION -> createMessageForTeamAction(gameId, teamThis, teamOther, data);
            default ->
                    throw new UnsupportedOperationException(
                            "Cannot create message for performed Action of type "
                                    + this
                                    + ", required: ACTION");
        };
    }

    private <T, R>
            Collection<GameEventMessage<PerformedActionData<?, ?>>> createMessageForTeamAction(
                    final long gameId,
                    final Collection<Player> teamThis,
                    final Collection<Player> teamOther,
                    final PerformedActionData<T, R> data) {
        final var time = Instant.now();
        return switch (data.action().notificationPolicy()) {
            case GAME ->
                    List.of(
                            new GameEventMessage<>(
                                    gameId,
                                    time,
                                    Stream.concat(teamThis.stream(), teamOther.stream()).toList(),
                                    data.action().notificationPolicy(),
                                    this,
                                    data));
            case TEAM ->
                    List.of(
                            new GameEventMessage<>(
                                    gameId, time, teamThis, TEAM, this, data.teamThis()),
                            new GameEventMessage<>(
                                    gameId, time, teamThis, GAME, this, data.teamOther()));
            case NONE -> List.of();
            case FROM_ACTION ->
                    throw new UnsupportedOperationException(
                            "Action Notification Policy cannot be " + FROM_ACTION);
        };
    }
}
