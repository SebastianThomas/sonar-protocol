package ch.sthomas.sonar.protocol.ws.handler;

import ch.sthomas.sonar.protocol.model.Player;
import ch.sthomas.sonar.protocol.model.api.*;
import ch.sthomas.sonar.protocol.model.event.GameEvent;
import ch.sthomas.sonar.protocol.model.event.GameEventMessage;
import ch.sthomas.sonar.protocol.model.event.WebSocketGameEventListener;
import ch.sthomas.sonar.protocol.model.exception.GameException;
import ch.sthomas.sonar.protocol.model.exception.GameNotFoundException;
import ch.sthomas.sonar.protocol.model.exception.NoSuchEventException;
import ch.sthomas.sonar.protocol.model.exception.PlayerNotFoundException;
import ch.sthomas.sonar.protocol.service.GameService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWSHandler extends TextWebSocketHandler
        implements WebSocketGameEventListener<WebSocketSession> {
    private static final Logger logger = LoggerFactory.getLogger(GameWSHandler.class);
    private final ObjectMapper objectMapper;
    private final GameService gameService;

    private final ConcurrentHashMap<String, WebSocketSession> sessions;
    private final ConcurrentHashMap<Long, String> playerToSessionId;

    public GameWSHandler(final ObjectMapper objectMapper, final GameService gameService) {
        this.objectMapper = objectMapper;
        this.gameService = gameService;
        this.sessions = new ConcurrentHashMap<>();
        this.playerToSessionId = new ConcurrentHashMap<>();
    }

    /**
     * Message format: {@code [EVENT]PAYLOAD} where EVENT is one of the cases below and PAYLOAD is
     * either a JSON-Object with the schema from the models `ch.sthomas.sonar.protocol.model.api` or
     * a number, depending on the class that is the second parameter in the {@link
     * ObjectMapper#readValue(String, Class)}-call.
     */
    @Override
    protected void handleTextMessage(
            @NotNull final WebSocketSession session, final TextMessage message)
            throws IOException, PlayerNotFoundException, GameNotFoundException, GameException {
        final var payload = message.getPayload();
        if (payload.indexOf('[') != 0 || payload.indexOf(']') == -1) {
            throw new NoSuchEventException("");
        }
        final var topic = GameEvent.fromString(payload.substring(1, payload.indexOf(']')));
        final var content = payload.substring(payload.indexOf(']') + 2);
        final var result =
                switch (topic) {
                    case CREATE_PLAYER -> {
                        final var player =
                                gameService.createPlayer(
                                        objectMapper.readValue(content, PlayerPayload.class),
                                        session.getId());
                        playerToSessionId.put(player.id(), session.getId());
                        yield player;
                    }
                    case REJOIN_GAME -> {
                        final var playerId = objectMapper.readValue(content, Long.class);
                        playerToSessionId.put(playerId, session.getId());
                        final var player =
                                gameService
                                        .updatePlayerWsSessionId(playerId, session.getId())
                                        .orElseThrow(PlayerNotFoundException::new);
                        yield gameService.findGameWithPlayer(player);
                    }
                    case JOIN ->
                            gameService.joinGame(
                                    objectMapper.readValue(content, JoinGamePayload.class));
                    case SET_START_POSITION ->
                            gameService.setStartPosition(
                                    objectMapper.readValue(content, SetLocationPayload.class));
                    case START ->
                            gameService.startGame(objectMapper.readValue(content, Long.class));
                    case MOVE ->
                            gameService.move(objectMapper.readValue(content, MovePayload.class));
                    case SURFACE ->
                            gameService.surface(
                                    objectMapper.readValue(content, GameIdTeamPayload.class));
                    case SUBMERGE ->
                            gameService.submerge(
                                    objectMapper.readValue(content, GameIdTeamPayload.class));
                };
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(result)));
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status)
            throws Exception {
        sessions.remove(session.getId());
        super.afterConnectionClosed(session, status);
    }

    @Override
    public Collection<WebSocketSession> getSessions(final Collection<Player> players) {
        return players.stream()
                .map(Player::id)
                .map(playerToSessionId::get)
                .filter(Objects::nonNull)
                .map(sessions::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
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
