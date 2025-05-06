package ch.sthomas.sonar.protocol.ws.handler;

import ch.sthomas.sonar.protocol.model.api.*;
import ch.sthomas.sonar.protocol.model.exception.GameException;
import ch.sthomas.sonar.protocol.model.exception.GameNotFoundException;
import ch.sthomas.sonar.protocol.model.exception.NoSuchEventException;
import ch.sthomas.sonar.protocol.model.exception.PlayerNotFoundException;
import ch.sthomas.sonar.protocol.service.GameService;

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
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWSHandler extends TextWebSocketHandler {
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

    @Override
    protected void handleTextMessage(
            @NotNull final WebSocketSession session, final TextMessage message)
            throws IOException, PlayerNotFoundException, GameNotFoundException, GameException {
        final var payload = message.getPayload();
        if (payload.indexOf('[') != 0 || payload.indexOf(']') == -1) {
            throw new NoSuchEventException("");
        }
        final var topic = payload.substring(1, payload.indexOf(']'));
        final var content = payload.substring(payload.indexOf(']') + 2);
        final var result =
                switch (topic) {
                    case "create_player" -> {
                        final var player =
                                gameService.createPlayer(
                                        objectMapper.readValue(content, PlayerPayload.class),
                                        session.getId());
                        playerToSessionId.put(player.id(), session.getId());
                        yield player;
                    }
                    case "rejoin_player" -> {
                        final var playerId = objectMapper.readValue(content, Long.class);
                        playerToSessionId.put(playerId, session.getId());
                        final var player =
                                gameService
                                        .updatePlayerWsSessionId(playerId, session.getId())
                                        .orElseThrow(PlayerNotFoundException::new);
                        yield gameService.findGameWithPlayer(player);
                    }
                    case "join" ->
                            gameService.joinGame(
                                    objectMapper.readValue(content, JoinGamePayload.class));
                    case "set_start_position" ->
                            gameService.setStartPosition(
                                    objectMapper.readValue(content, SetLocationPayload.class));
                    case "start" ->
                            gameService.startGame(objectMapper.readValue(content, Long.class));
                    case "move" ->
                            gameService.move(objectMapper.readValue(content, MovePayload.class));
                    case "surface" ->
                            gameService.surface(
                                    objectMapper.readValue(content, GameIdTeamPayload.class));
                    case "submerge" ->
                            gameService.submerge(
                                    objectMapper.readValue(content, GameIdTeamPayload.class));
                    default -> throw new NoSuchEventException(topic);
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
}
