package ch.sthomas.sonar.protocol.model.event;

import ch.sthomas.sonar.protocol.model.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public interface WebSocketGameEventListener<S> extends GameEventListener {
    Logger logger = LoggerFactory.getLogger(WebSocketGameEventListener.class);

    @Override
    default <T> void pushEvent(final GameEventMessage<T> event) {
        getSessions(event.notifiedPlayers()).forEach(player -> sendMessage(player, event));
    }

    Collection<S> getSessions(Collection<Player> players);

    <T> void sendMessage(S player, GameEventMessage<T> event);
}
