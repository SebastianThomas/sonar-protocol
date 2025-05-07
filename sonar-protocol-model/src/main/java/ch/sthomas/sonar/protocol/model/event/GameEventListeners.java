package ch.sthomas.sonar.protocol.model.event;

import ch.sthomas.sonar.protocol.model.Game;
import ch.sthomas.sonar.protocol.model.Player;
import ch.sthomas.sonar.protocol.model.Team;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public record GameEventListeners(Collection<GameEventListener> listeners) {
    public Game sendMessage(final GameEvent event, final Game game) {
        final var notifiedPlayers = getPlayers(game, event.notificationPolicy());
        final var message = event.createMessage(game.id(), notifiedPlayers, game);
        listeners.forEach(l -> l.pushEvent(message));
        return game;
    }

    public Game sendMessage(final Team.ID team, final GameEvent event, final Game game) {
        final var notifiedPlayers = getPlayers(game, team, event.notificationPolicy());
        final var message = event.createMessage(game.id(), notifiedPlayers, game);
        listeners.forEach(l -> l.pushEvent(message));
        return game;
    }

    public <T> T sendMessage(
            final Game game, final Team.ID team, final GameEvent event, final T data) {
        final var notifiedPlayers = getPlayers(game, team, event.notificationPolicy());
        final var message = event.createMessage(game.id(), notifiedPlayers, data);
        listeners.forEach(l -> l.pushEvent(message));
        return data;
    }

    private Collection<Player> getPlayers(
            final Game game, final GameEventNotificationPolicy policy) {
        return switch (policy) {
            case NONE -> List.of();
            case GAME ->
                    Stream.concat(game.a().players().stream(), game.b().players().stream())
                            .toList();
            case TEAM ->
                    throw new IllegalArgumentException(
                            "Cannot send message to team without knowing team.");
        };
    }

    private Collection<Player> getPlayers(
            final Game game, final Team.ID team, final GameEventNotificationPolicy policy) {
        return switch (policy) {
            case NONE -> List.of();
            case GAME ->
                    Stream.concat(game.a().players().stream(), game.b().players().stream())
                            .toList();
            case TEAM ->
                    switch (team) {
                        case A -> game.a().players();
                        case B -> game.b().players();
                    };
        };
    }
}
