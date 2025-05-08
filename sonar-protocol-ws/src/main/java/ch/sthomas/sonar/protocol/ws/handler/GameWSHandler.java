package ch.sthomas.sonar.protocol.ws.handler;

import ch.sthomas.sonar.protocol.model.action.Action;
import ch.sthomas.sonar.protocol.model.api.*;
import ch.sthomas.sonar.protocol.model.event.GameEvent;
import ch.sthomas.sonar.protocol.model.exception.*;
import ch.sthomas.sonar.protocol.service.GameService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class GameWSHandler extends TextWebSocketHandler {
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
     * Message format: {@code [EVENT]PAYLOAD} or {@code [action][ACTION]PAYLOAD} where EVENT is one
     * of the cases below and PAYLOAD is either a JSON-Object with the schema from the models
     * `ch.sthomas.sonar.protocol.model.api` or a number, depending on the class that is the second
     * parameter in the {@link ObjectMapper#readValue(String, Class)}-call. If EVENT=action then
     * another bracketed ACTION follows ({@link ch.sthomas.sonar.protocol.model.action.Action}).
     */
    @Override
    protected void handleTextMessage(
            @NotNull @org.springframework.lang.NonNull final WebSocketSession session,
            final TextMessage message)
            throws IOException {
        try {
            final var payload = message.getPayload();
            final var topicAndContent = readBracketsAndPayload(payload);
            final var topic = GameEvent.fromString(topicAndContent.getKey());
            final var content = topicAndContent.getValue();
            final var result = handleTopicAndContent(session, topic, content);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(result)));
        } catch (final GameException | GameRuntimeException e) {
            session.sendMessage(new TextMessage(e.getMessage()));
        }
    }

    private Record handleTopicAndContent(
            final WebSocketSession session, final GameEvent topic, final String content)
            throws JsonProcessingException, GameException {
        return switch (topic) {
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
                yield gameService
                        .updatePlayerWsSessionId(playerId, session.getId())
                        .orElseThrow(() -> new PlayerNotFoundException(playerId));
            }
            case JOIN ->
                    gameService.joinGame(objectMapper.readValue(content, JoinGamePayload.class));
            case SET_START_POSITION ->
                    gameService.setStartPosition(
                            objectMapper.readValue(content, GameIdTeamLocationPayload.class));
            case START -> gameService.startGame(objectMapper.readValue(content, Long.class));
            case MOVE ->
                    gameService.move(
                            objectMapper.readValue(content, GameIdTeamDirectionPayload.class));
            case SURFACE ->
                    gameService.surface(objectMapper.readValue(content, GameIdTeamPayload.class));
            case SUBMERGE ->
                    gameService.submerge(objectMapper.readValue(content, GameIdTeamPayload.class));
            case SWITCH ->
                    gameService.moveSwitch(
                            objectMapper.readValue(content, GameIdTeamActionPayload.class));
            case ACTION -> readPerformActionPayload(content);
            case EXPLODE_MINE ->
                    gameService.explodeMine(
                            objectMapper.readValue(content, GameIdTeamLocationPayload.class));
        };
    }

    @Override
    public void afterConnectionEstablished(
            @NotNull @org.springframework.lang.NonNull final WebSocketSession session)
            throws Exception {
        sessionStore.sessions().put(session.getId(), session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(
            final WebSocketSession session,
            @NotNull @org.springframework.lang.NonNull final CloseStatus status)
            throws Exception {
        // Do not use try-with-resources, otherwise it will be closed twice
        //noinspection resource
        sessionStore.sessions().remove(session.getId());
        super.afterConnectionClosed(session, status);
    }

    private Pair<String, String> readBracketsAndPayload(final String payload)
            throws NoSuchEventException {
        if (payload.indexOf('[') != 0 || payload.indexOf(']') == -1) {
            throw new NoSuchEventException("");
        }
        return Pair.of(
                payload.substring(1, payload.indexOf(']')),
                payload.substring(payload.indexOf(']') + 2));
    }

    private Record readPerformActionPayload(final String content)
            throws NoSuchEventException, JsonProcessingException {
        final var actionAndPayload = readBracketsAndPayload(content);
        final var action = Action.fromString(actionAndPayload.getKey());
        final var payload = actionAndPayload.getValue();
        // TODO: Listeners
        return switch (action) {
            case MINE ->
                    gameService.placeMine(
                            objectMapper.readValue(payload, GameIdTeamLocationPayload.class));
            case DRONE ->
                    gameService.torpedo(
                            objectMapper.readValue(payload, GameIdTeamSectorPayload.class));
            case TORPEDO ->
                    gameService.torpedo(objectMapper.readValue(payload, GameIdTeamPayload.class));
            case SONAR ->
                    gameService.sonar(objectMapper.readValue(payload, GameIdTeamPayload.class));
            case STEALTH ->
                    gameService.stealth(
                            objectMapper.readValue(
                                    payload, GameIdTeamDirectionAmountPayload.class));
            case SZENARIO -> throw new NoSuchEventException("SZENARIO");
        };
    }
}
