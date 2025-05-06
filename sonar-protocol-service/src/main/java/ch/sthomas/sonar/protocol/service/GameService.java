package ch.sthomas.sonar.protocol.service;

import ch.sthomas.sonar.protocol.data.service.GameDataService;
import ch.sthomas.sonar.protocol.model.*;
import ch.sthomas.sonar.protocol.model.api.*;
import ch.sthomas.sonar.protocol.model.exception.*;

import jakarta.annotation.Nullable;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {
    private final GameDataService gameDataService;

    public GameService(final GameDataService gameDataService) {
        this.gameDataService = gameDataService;
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
        return gameDataService.joinGame(
                gameDataService.findPlayerById(playerId).orElseThrow(PlayerNotFoundException::new),
                gameId,
                team);
    }

    public Game startGame(final long gameId) throws GameNotFoundException, GameException {
        return gameDataService.startGame(gameId);
    }

    public Path move(final MovePayload payload) throws GameNotFoundException, GameException {
        return gameDataService.move(payload.gameId(), payload.teamId(), payload.direction());
    }

    public Path surface(final GameIdTeamPayload payload)
            throws GameException, GameNotFoundException {
        return gameDataService.surface(payload.gameId(), payload.team());
    }

    public Path submerge(final GameIdTeamPayload payload)
            throws GameException, GameNotFoundException {
        return gameDataService.submerge(payload.gameId(), payload.team());
    }

    public Path setStartPosition(final SetLocationPayload payload) throws GameNotFoundException {
        return gameDataService.setStartPosition(
                payload.gameId(), payload.team(), payload.location());
    }

    protected Game findGameById(final long gameId) throws GameNotFoundException {
        return gameDataService.findGameById(gameId).orElseThrow(GameNotFoundException::new);
    }

    protected Team findTeam(final Game game, final Team.ID team) {
        return switch (team) {
            case A -> game.a();
            case B -> game.b();
        };
    }

    public Optional<Player> findPlayer(final long playerId) {
        return gameDataService.findPlayerById(playerId);
    }

    public Optional<Player> updatePlayerWsSessionId(final long playerId, final String wsSessionId) {
        return gameDataService.updatePlayerWsSessionId(playerId, wsSessionId);
    }

    public Optional<Game> findGameWithPlayer(final Player player) {
        return gameDataService.findGameWithPlayerId(player.id());
    }
}
