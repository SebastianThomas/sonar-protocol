package ch.sthomas.sonar.protocol.service;

import ch.sthomas.sonar.protocol.data.service.GameDataService;
import ch.sthomas.sonar.protocol.model.*;
import ch.sthomas.sonar.protocol.model.api.*;
import ch.sthomas.sonar.protocol.model.event.GameEvent;
import ch.sthomas.sonar.protocol.model.event.GameEventListeners;
import ch.sthomas.sonar.protocol.model.exception.*;

import jakarta.annotation.Nullable;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {
    private final GameDataService gameDataService;
    private final GameEventListeners listeners;

    public GameService(final GameDataService gameDataService, final GameEventListeners listeners) {
        this.gameDataService = gameDataService;
        this.listeners = listeners;
    }

    public Game findGame(final long gameId) throws GameNotFoundException {
        return gameDataService.findGameById(gameId).orElseThrow(GameNotFoundException::new);
    }

    public Player createPlayer(final PlayerPayload payload, @Nullable final String wsSessionId) {
        return gameDataService.createPlayer(payload.name(), payload.role(), wsSessionId);
    }

    public Game createGame(final long gameMasterId) throws PlayerNotFoundException {
        final var gameMaster =
                gameDataService
                        .findPlayerById(gameMasterId)
                        .orElseThrow(PlayerNotFoundException::new);
        return gameDataService.createGame(List.of(gameMaster), List.of());
    }

    public Game joinGame(final JoinGamePayload payload)
            throws GameNotFoundException, PlayerNotFoundException {
        final var playerId = payload.playerId();
        final var gameId = payload.gameId();
        final var team = payload.team();
        return listeners.sendMessage(
                gameId,
                GameEvent.JOIN,
                gameDataService.joinGame(
                        gameDataService
                                .findPlayerById(playerId)
                                .orElseThrow(PlayerNotFoundException::new),
                        gameId,
                        team));
    }

    public Game startGame(final long gameId) throws GameNotFoundException, GameException {
        return listeners.sendMessage(gameId, GameEvent.START, gameDataService.startGame(gameId));
    }

    public Path move(final MovePayload payload) throws GameNotFoundException, GameException {
        return listeners.sendMessage(
                payload.gameId(),
                GameEvent.MOVE,
                gameDataService.move(payload.gameId(), payload.teamId(), payload.direction()));
    }

    public Path surface(final GameIdTeamPayload payload)
            throws GameException, GameNotFoundException {
        return listeners.sendMessage(
                payload.gameId(),
                GameEvent.SURFACE,
                gameDataService.surface(payload.gameId(), payload.team()));
    }

    public Path submerge(final GameIdTeamPayload payload)
            throws GameException, GameNotFoundException {
        return listeners.sendMessage(
                payload.gameId(),
                GameEvent.SUBMERGE,
                gameDataService.submerge(payload.gameId(), payload.team()));
    }

    public Path setStartPosition(final SetLocationPayload payload) throws GameNotFoundException {
        return listeners.sendMessage(
                payload.gameId(),
                GameEvent.SET_START_POSITION,
                gameDataService.setStartPosition(
                        payload.gameId(), payload.team(), payload.location()));
    }

    public Optional<Player> updatePlayerWsSessionId(final long playerId, final String wsSessionId) {
        return gameDataService.updatePlayerWsSessionId(playerId, wsSessionId);
    }

    public Optional<Game> findGameWithPlayer(final Player player) {
        return gameDataService.findGameWithPlayerId(player.id());
    }
}
