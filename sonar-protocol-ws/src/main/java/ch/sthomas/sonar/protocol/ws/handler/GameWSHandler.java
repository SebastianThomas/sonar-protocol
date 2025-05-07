package ch.sthomas.sonar.protocol.ws.handler;

import ch.sthomas.sonar.protocol.model.Player;
import ch.sthomas.sonar.protocol.model.api.*;
import ch.sthomas.sonar.protocol.model.event.GameEvent;
import ch.sthomas.sonar.protocol.model.event.GameEventMessage;
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

@Component
public class GameWSHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(GameWSHandler.class);
    private final ObjectMapper objectMapper;
    private final GameService gameService;
    private final WSSessionStore sessionStore;

    public GameWSHandler(
            final ObjectMapper objectMapper,
            final GameService gameService,
            final WSSessionStore sessionStore) {
        this.objectMapper = objectMapper;
        this.gameService = gameService;
        this.sessionStore = sessionStore;
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
                        sessionStore.playerToSessionId().put(player.id(), session.getId());
                        yield player;
                    }
                    case REJOIN_GAME -> {
                        final var playerId = objectMapper.readValue(content, Long.class);
                        sessionStore.playerToSessionId().put(playerId, session.getId());
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
        sessionStore.sessions().put(session.getId(), session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status)
            throws Exception {
        sessionStore.sessions().remove(session.getId());
        super.afterConnectionClosed(session, status);
    }
}
