package ch.sthomas.sonar.protocol.service;

import static ch.sthomas.sonar.protocol.model.game.GameOverState.*;

import ch.sthomas.sonar.protocol.data.service.GameDataService;
import ch.sthomas.sonar.protocol.model.*;
import ch.sthomas.sonar.protocol.model.action.ExplodedMineInfo;
import ch.sthomas.sonar.protocol.model.action.ExplodedTorpedoInfo;
import ch.sthomas.sonar.protocol.model.action.Mine;
import ch.sthomas.sonar.protocol.model.action.SwitchInfo;
import ch.sthomas.sonar.protocol.model.api.*;
import ch.sthomas.sonar.protocol.model.event.GameEvent;
import ch.sthomas.sonar.protocol.model.event.GameEventListeners;
import ch.sthomas.sonar.protocol.model.exception.*;
import ch.sthomas.sonar.protocol.model.game.GameOverInfo;
import ch.sthomas.sonar.protocol.model.play.Location;
import ch.sthomas.sonar.protocol.model.util.VectorUtils;

import com.google.common.collect.MoreCollectors;

import jakarta.annotation.Nullable;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class GameService {
    private final GameDataService gameDataService;
    private final GameEventListeners listeners;

    public GameService(final GameDataService gameDataService, final GameEventListeners listeners) {
        this.gameDataService = gameDataService;
        this.listeners = listeners;
    }

    public Game findGame(final long gameId) throws GameNotFoundException {
        return gameDataService
                .findGameById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }

    public Player createPlayer(final PlayerPayload payload, @Nullable final String wsSessionId) {
        return gameDataService.createPlayer(payload.name(), payload.role(), wsSessionId);
    }

    public Game createGame(final long gameMasterId) throws PlayerNotFoundException {
        final var gameMaster =
                gameDataService
                        .findPlayerById(gameMasterId)
                        .orElseThrow(() -> new PlayerNotFoundException(gameMasterId));
        return gameDataService.createGame(List.of(gameMaster), List.of());
    }

    public Game joinGame(final JoinGamePayload payload)
            throws GameNotFoundException, PlayerNotFoundException {
        final var playerId = payload.playerId();
        final var gameId = payload.gameId();
        final var team = payload.team();
        final var game =
                gameDataService.joinGame(
                        gameDataService
                                .findPlayerById(playerId)
                                .orElseThrow(() -> new PlayerNotFoundException(playerId)),
                        gameId,
                        team);
        return listeners.sendMessage(team, GameEvent.JOIN, game);
    }

    public Game startGame(final long gameId) throws GameNotFoundException, GameException {
        final var game = gameDataService.startGame(gameId);
        return listeners.sendMessage(GameEvent.START, game);
    }

    public Path move(final GameIdTeamDirectionPayload payload)
            throws GameNotFoundException, GameException {
        return listeners.sendMessage(
                gameDataService
                        .findGameById(payload.gameId())
                        .orElseThrow(() -> new GameNotFoundException(payload.gameId())),
                payload.team(),
                GameEvent.MOVE,
                gameDataService.move(payload.gameId(), payload.team(), payload.direction()));
    }

    public Path surface(final GameIdTeamPayload payload) throws GameException {
        return listeners.sendMessage(
                gameDataService
                        .findGameById(payload.gameId())
                        .orElseThrow(() -> new GameNotFoundException(payload.gameId())),
                payload.team(),
                GameEvent.SURFACE,
                gameDataService.surface(payload.gameId(), payload.team()));
    }

    public Path submerge(final GameIdTeamPayload payload) throws GameException {
        return listeners.sendMessage(
                gameDataService
                        .findGameById(payload.gameId())
                        .orElseThrow(() -> new GameNotFoundException(payload.gameId())),
                payload.team(),
                GameEvent.SUBMERGE,
                gameDataService.submerge(payload.gameId(), payload.team()));
    }

    public Path setStartPosition(final GameIdTeamLocationPayload payload)
            throws GameNotFoundException {
        return listeners.sendMessage(
                gameDataService
                        .findGameById(payload.gameId())
                        .orElseThrow(() -> new GameNotFoundException(payload.gameId())),
                payload.team(),
                GameEvent.SET_START_POSITION,
                gameDataService.setStartPosition(
                        payload.gameId(), payload.team(), payload.location()));
    }

    public ExplodedMineInfo explodeMine(final GameIdTeamLocationPayload gameIdTeamLocationPayload)
            throws GameException {
        final var game = findGame(gameIdTeamLocationPayload.gameId());
        final var existingMine =
                gameDataService.findMineEntity(
                        gameIdTeamLocationPayload.gameId(),
                        gameIdTeamLocationPayload.team(),
                        gameIdTeamLocationPayload.location());
        if (existingMine.isEmpty()) {
            throw new NoMineException(gameIdTeamLocationPayload.location());
        }
        final var mineEntity = existingMine.get();
        gameDataService.deleteMine(mineEntity);
        final var mine = mineEntity.toRecord();
        final var info = calculateDamage(gameIdTeamLocationPayload.gameId(), mine.location());
        return listeners.sendMessage(
                game,
                gameIdTeamLocationPayload.team(),
                GameEvent.EXPLODE_MINE,
                new ExplodedMineInfo(mine, info));
    }

    public ExplodedTorpedoInfo explodeTorpedo(
            final GameIdTeamLocationPayload gameIdTeamLocationPayload) {
        // TODO: Check torpedo within distance 4
    }

    public Mine placeMine(final GameIdTeamLocationPayload payload) {
        // TODO: Check mine not on path but next to current
    }

    public SwitchInfo moveSwitch(final GameIdTeamActionPayload gameIdSwitchPayload)
            throws GameNotFoundException {
        // TODO
        return listeners.sendMessage(
                findGame(gameIdSwitchPayload.gameId()),
                gameIdSwitchPayload.team(),
                GameEvent.SWITCH,
                new SwitchInfo(List.of()));
    }

    private GameOverInfo calculateDamage(final long gameId, final Location location)
            throws GameException {
        final var game = findGame(gameId);
        calculateDamage(game.a().ship(), location);
        calculateDamage(game.b().ship(), location);
        return checkGameOver(gameId);
    }

    private void calculateDamage(final Ship ship, final Location location) throws GameException {
        final var shipLocation = getLastPathNode(ship).location();
        final var distance = VectorUtils.distance(shipLocation, location);
        final var damage = Math.max(0, 2 - distance);
        if (damage >= 1) {
            gameDataService.addDamage(ship, damage);
        }
    }

    private GameOverInfo checkGameOver(final long gameId) throws GameNotFoundException {
        final var game = findGame(gameId);
        final var shipA = game.a().ship();
        final var shipB = game.b().ship();
        if (shipA.isHealthy() && shipB.isHealthy()) {
            return new GameOverInfo(NOT_FINISHED);
        }
        gameDataService.finishGame(game);
        if (shipA.isHealthy()) {
            return new GameOverInfo(TEAM_A_WINS);
        }
        if (shipB.isHealthy()) {
            return new GameOverInfo(TEAM_B_WINS);
        }
        return new GameOverInfo(DRAW);
    }

    private PathNode getLastPathNode(final Ship ship)
            throws IllegalGameStateException, NotSubmergedException {
        final var lastPath =
                ship.paths().stream()
                        .filter(Predicate.not(Path::surfaced))
                        .collect(MoreCollectors.toOptional())
                        .orElseThrow(NotSubmergedException::new);
        if (lastPath.nodes().isEmpty()) {
            throw new IllegalGameStateException("Path is empty.");
        }
        return lastPath.nodes().getLast();
    }

    public Optional<Player> updatePlayerWsSessionId(final long playerId, final String wsSessionId) {
        return gameDataService.updatePlayerWsSessionId(playerId, wsSessionId);
    }

    public Optional<Game> findGameWithPlayer(final Player player) {
        return gameDataService.findGameWithPlayerId(player.id());
    }
}
