package ch.sthomas.sonar.protocol.ws.handler;

import ch.sthomas.sonar.protocol.model.event.GameEventListener;
import ch.sthomas.sonar.protocol.model.event.GameEventMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class WebSocketGameEventListener implements GameEventListener {
    private final GameWSHandler gameWSHandler;
    Logger logger = LoggerFactory.getLogger(WebSocketGameEventListener.class);

    public WebSocketGameEventListener(final GameWSHandler gameWSHandler) {
        this.gameWSHandler = gameWSHandler;
    }

    @Override
    public <T> void pushEvent(final GameEventMessage<T> event) {
        gameWSHandler
                .getSessions(event.notifiedPlayers())
                .forEach(player -> sendMessage(player, event));
    }

    public <T> void sendMessage(final WebSocketSession player, final GameEventMessage<T> event) {
        gameWSHandler.sendMessage(player, event);
    }
}
