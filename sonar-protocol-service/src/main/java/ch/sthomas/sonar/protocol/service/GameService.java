package ch.sthomas.sonar.protocol.service;

import static ch.sthomas.sonar.protocol.model.action.Action.*;
import static ch.sthomas.sonar.protocol.model.game.GameOverState.*;

import ch.sthomas.sonar.protocol.data.service.GameDataService;
import ch.sthomas.sonar.protocol.model.*;
import ch.sthomas.sonar.protocol.model.action.*;
import ch.sthomas.sonar.protocol.model.api.*;
import ch.sthomas.sonar.protocol.model.event.GameEvent;
import ch.sthomas.sonar.protocol.model.event.GameEventListeners;
import ch.sthomas.sonar.protocol.model.exception.*;
import ch.sthomas.sonar.protocol.model.game.GameOverInfo;
import ch.sthomas.sonar.protocol.model.play.Direction;
import ch.sthomas.sonar.protocol.model.play.Location;
import ch.sthomas.sonar.protocol.model.util.VectorUtils;

import com.google.common.collect.MoreCollectors;

import jakarta.annotation.Nullable;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;

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

    public Game startGame(final long gameId) throws GameException {
        final var game = gameDataService.startGame(gameId);
        return listeners.sendMessage(GameEvent.START, game);
    }

    public Path move(final GameIdTeamDirectionPayload payload) throws GameException {
        // TODO: Check Game Running
        final var ship = gameDataService.findTeamShip(payload.gameId(), payload.team());
        final var lastPath = getLastPathNotSurfaced(ship);
        final var lastNode = lastPath.nodes().getLast();
        if (!lastNode.finished()) {
            throw new PreviousRoundNotFinishedException();
        }
        final var newLocation = VectorUtils.add(lastNode.location(), payload.direction().vector());
        if (lastPath.isOnPath(newLocation)) {
            throw new CannotCrossOwnPathException(newLocation);
        }
        if (gameDataService.findMine(payload.gameId(), payload.team(), newLocation).isPresent()) {
            throw new CannotMoveIntoOwnMineException(newLocation);
        }
        final var path = gameDataService.savePathNode(lastPath.id(), newLocation, Instant.now());
        return listeners.sendMessage(
                gameDataService
                        .findGameById(payload.gameId())
                        .orElseThrow(() -> new GameNotFoundException(payload.gameId())),
                payload.team(),
                GameEvent.MOVE,
                path);
    }

    public Path surface(final GameIdTeamPayload payload) throws GameException {
        final var gameId = payload.gameId();
        final var team = payload.team();
        final var ship = gameDataService.findTeamShip(gameId, team);
        final var lastPath = getLastPathNotSurfaced(ship);
        if (lastPath.surfaced()) {
            throw new NotSubmergedException();
        }
        final var lastNode = lastPath.nodes().getLast();
        if (!lastNode.finished()) {
            throw new PreviousRoundNotFinishedException();
        }
        final var path = gameDataService.setSurfaced(lastPath.id());
        gameDataService.clearDefects(ship);
        return listeners.sendMessage(
                gameDataService
                        .findGameById(payload.gameId())
                        .orElseThrow(() -> new GameNotFoundException(payload.gameId())),
                payload.team(),
                GameEvent.SURFACE,
                path);
    }

    public Path submerge(final GameIdTeamPayload payload) throws GameException {
        final var gameId = payload.gameId();
        final var team = payload.team();
        final var ship = gameDataService.findTeamShip(gameId, team);
        final var lastPath = getLastPath(ship);
        if (!lastPath.surfaced()) {
            throw new NotSubmergedException();
        }
        final var lastNode = lastPath.nodes().getLast();
        if (!lastNode.finished()) {
            throw new PreviousRoundNotFinishedException();
        }
        final var newLocation = lastNode.location();
        final var newPath = gameDataService.saveNewPath(ship, newLocation, Instant.now());

        return listeners.sendMessage(
                gameDataService
                        .findGameById(payload.gameId())
                        .orElseThrow(() -> new GameNotFoundException(payload.gameId())),
                payload.team(),
                GameEvent.SUBMERGE,
                newPath);
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

    public ExplodedTorpedoInfo torpedo(final GameIdTeamLocationPayload gameIdTeamLocationPayload)
            throws GameException {
        // TODO: Check Torpedo Switch loaded and reset
        final var game = findGame(gameIdTeamLocationPayload.gameId());
        final var torpedoLocation = gameIdTeamLocationPayload.location();
        final var teamShip =
                gameDataService.findTeamShip(
                        gameIdTeamLocationPayload.gameId(), gameIdTeamLocationPayload.team());
        final var lastTeamLocation = getLastPathNotSurfaced(teamShip).nodes().getLast().location();
        Torpedo.checkValidLocation(lastTeamLocation, torpedoLocation);
        final var torpedo = new Torpedo(torpedoLocation);
        final var info = calculateDamage(gameIdTeamLocationPayload.gameId(), torpedo.location());
        return listeners.sendMessage(
                game,
                gameIdTeamLocationPayload.team(),
                TORPEDO,
                new ExplodedTorpedoInfo(torpedo, info),
                null);
    }

    public Mine placeMine(final GameIdTeamLocationPayload payload) throws GameException {
        // TODO: Check Mine Switch loaded and reset
        final var game = findGame(payload.gameId());
        final var mineLocation = payload.location();
        final var teamShip = gameDataService.findTeamShip(payload.gameId(), payload.team());
        final var lastTeamLocation = getLastPathNotSurfaced(teamShip).nodes().getLast().location();
        Mine.checkValidLocation(lastTeamLocation, mineLocation, getLastPathNotSurfaced(teamShip));
        final var mine = new Mine(mineLocation);
        return listeners.sendMessage(
                game, payload.team(), MINE, new Information("Mine Placed"), mine);
    }

    public DroneResult drone(final GameIdTeamSectorPayload gameIdTeamSectorPayload)
            throws GameException {
        final var game = findGame(gameIdTeamSectorPayload.gameId());
        // TODO: Check and Reset Switch
        final var otherTeamShip =
                gameDataService.findTeamShip(
                        gameIdTeamSectorPayload.gameId(), gameIdTeamSectorPayload.team().other());
        final var otherTeamLocation = getLastPathNode(otherTeamShip);
        final var hit = gameIdTeamSectorPayload.sector().contains(otherTeamLocation.location());
        final var result =
                new DroneResult(
                        gameIdTeamSectorPayload.sector(), gameIdTeamSectorPayload.team(), hit);
        return listeners.sendMessage(game, gameIdTeamSectorPayload.team(), DRONE, result, null);
    }

    public Information sonar(final GameIdTeamPayload gameIdTeamPayload) throws GameException {
        final var game = findGame(gameIdTeamPayload.gameId());
        // TODO: Check and Reset Switch
        return listeners.sendMessage(
                game,
                gameIdTeamPayload.team(),
                SONAR,
                new RequestSonarInformation(gameIdTeamPayload.team()),
                new Information("Sonar sent, waiting for response from the other team."));
    }

    public StealthInformation stealth(final GameIdTeamDirectionAmountPayload payload)
            throws GameException {
        final var game = findGame(payload.gameId());
        // TODO: Check and Reset Switch
        final var team = payload.team().get(game);
        final var lastPath = getLastPathNotSurfaced(team.ship());
        final var lastNode = lastPath.nodes().getLast();
        if (!lastNode.finished()) {
            throw new PreviousRoundNotFinishedException();
        }
        final var result =
                performStealth(
                        payload.gameId(),
                        payload.team(),
                        payload.amount(),
                        payload.direction(),
                        lastPath);
        return listeners.sendMessage(
                game,
                payload.team(),
                STEALTH,
                new StealthOtherTeamInformation(payload.team(), payload.direction()),
                result);
    }

    private StealthInformation performStealth(
            final long gameId,
            final Team.ID team,
            final int amount,
            final Direction direction,
            final Path lastPath) {
        if (amount <= 0) {
            return new StealthInformation(team, 0, direction, lastPath);
        }
        final var newLocations =
                IntStream.range(1, amount)
                        .mapToObj(
                                getNewLocationAndChecks(gameId, team, lastPath, direction.vector()))
                        .toList();
        final var path = gameDataService.savePathNodes(lastPath.id(), newLocations, Instant.now());
        return new StealthInformation(team, amount, direction, path);
    }

    private IntFunction<Location> getNewLocationAndChecks(
            final long gameId,
            final Team.ID team,
            final Path lastPath,
            final Location.DirectionVector vector) {
        return i -> {
            final var lastNode = lastPath.nodes().getLast();
            final var newLocation = VectorUtils.add(lastNode.location(), vector, i);
            if (lastPath.isOnPath(newLocation)) {
                throw new CannotCrossOwnPathException(newLocation);
            }
            if (gameDataService.findMine(gameId, team, newLocation).isPresent()) {
                throw new CannotMoveIntoOwnMineException(newLocation);
            }
            return newLocation;
        };
    }

    public SwitchInfo moveSwitch(final GameIdTeamActionPayload gameIdSwitchPayload)
            throws GameNotFoundException {
        final var game = findGame(gameIdSwitchPayload.gameId());
        // TODO: Move Switch
        return listeners.sendMessage(
                game, gameIdSwitchPayload.team(), GameEvent.SWITCH, new SwitchInfo(List.of()));
    }

    public DefectInfo crossDefect(final GameIdTeamDefectPayload payload) {
        final var game = findGame(payload.gameId());
        final var team =
                switch (payload.team()) {
                    case A -> game.a();
                    case B -> game.b();
                };
        // TODO: Check Defect for this move not yet crossed
        if (payload.direction() != team.getLastDirection()) {
            throw new Exception();
        }
        final var defects =
                gameDataService.crossDefect(
                        payload.gameId(),
                        payload.team(),
                        payload.direction(),
                        payload.defectLocation());
        DefectCircuit.EXISTING_CIRCUITS.stream()
                .filter(
                        circuit ->
                                circuit.defects().stream()
                                        .allMatch(
                                                circuitObj ->
                                                        defects.stream()
                                                                .anyMatch(circuitObj::equals)))
                .forEach(c -> gameDataService.clearDefects(team.ship(), c));
        return listeners.sendMessage(
                game,
                payload.team(),
                GameEvent.CROSS_DEFECT,
                new DefectInfo(
                        gameDataService.findTeamShip(payload.gameId(), payload.team()).defects()));
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

    private PathNode getLastPathNode(final Ship ship) throws IllegalGameStateException {
        final var lastPath =
                ship.paths().stream()
                        .sorted(Comparator.reverseOrder())
                        .filter(Predicate.not(Path::surfaced))
                        .collect(MoreCollectors.onlyElement());
        if (lastPath.nodes().isEmpty()) {
            throw new IllegalGameStateException("Path is empty.");
        }
        return lastPath.nodes().getLast();
    }

    private Path getLastPath(final Ship ship) {
        return ship.paths().stream().max(Path::compareTo).orElseThrow();
    }

    private Path getLastPathNotSurfaced(final Ship ship)
            throws IllegalGameStateException, NotSubmergedException {
        final var lastPath =
                ship.paths().stream()
                        .sorted(Comparator.reverseOrder())
                        .filter(Predicate.not(Path::surfaced))
                        .collect(MoreCollectors.toOptional())
                        .orElseThrow(NotSubmergedException::new);
        if (lastPath.nodes().isEmpty()) {
            throw new IllegalGameStateException("Path is empty.");
        }
        return lastPath;
    }

    public Optional<Player> updatePlayerWsSessionId(final long playerId, final String wsSessionId) {
        return gameDataService.updatePlayerWsSessionId(playerId, wsSessionId);
    }

    public Optional<Game> findGameWithPlayer(final Player player) {
        return gameDataService.findGameWithPlayerId(player.id());
    }
}
