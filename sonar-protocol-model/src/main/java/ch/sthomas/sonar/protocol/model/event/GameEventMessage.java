package ch.sthomas.sonar.protocol.model.event;

import ch.sthomas.sonar.protocol.model.Player;

import java.util.Collection;

public record GameEventMessage<T>(
        long gameId,
        Collection<Player> notifiedPlayers,
        GameEventNotificationPolicy policy,
        GameEvent event,
        T data) {}
