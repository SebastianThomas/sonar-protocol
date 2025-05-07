package ch.sthomas.sonar.protocol.ws.handler;

import ch.sthomas.sonar.protocol.model.Player;
import ch.sthomas.sonar.protocol.model.event.GameEventListener;
import ch.sthomas.sonar.protocol.model.event.GameEventMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

@Service
public class WebSocketGameEventListener implements GameEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketGameEventListener.class);
    private final ObjectMapper objectMapper;
    private final WSSessionStore sessionStore;

    public WebSocketGameEventListener(
            final ObjectMapper objectMapper, final WSSessionStore sessionStore) {
        this.objectMapper = objectMapper;
        this.sessionStore = sessionStore;
    }

    @Override
    public <T> void pushEvent(final GameEventMessage<T> event) {
        getSessions(event.notifiedPlayers()).forEach(player -> sendMessage(player, event));
    }

    public Collection<WebSocketSession> getSessions(final Collection<Player> players) {
        return players.stream()
                .map(Player::id)
                .map(playerId -> sessionStore.playerToSessionId().get(playerId))
                .filter(Objects::nonNull)
                .map(sessionId -> sessionStore.sessions().get(sessionId))
                .filter(Objects::nonNull)
                .toList();
    }

    public <T> void sendMessage(final WebSocketSession session, final GameEventMessage<T> event) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(event)));
        } catch (final JsonProcessingException e) {
            logger.error("Error while sending event", e);
        } catch (final IOException e) {
            logger.error("Error while sending event to the server", e);
        }
    }
}
