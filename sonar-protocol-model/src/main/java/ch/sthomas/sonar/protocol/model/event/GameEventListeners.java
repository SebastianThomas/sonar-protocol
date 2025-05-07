package ch.sthomas.sonar.protocol.model.event;

import java.util.Collection;

public record GameEventListeners(Collection<GameEventListener> listeners) {
    public <T> T sendMessage(final long gameId, final GameEvent event, final T data) {
        final var message = event.createMessage(gameId, data);
        listeners.forEach(l -> l.pushEvent(message));
        return data;
    }
}
