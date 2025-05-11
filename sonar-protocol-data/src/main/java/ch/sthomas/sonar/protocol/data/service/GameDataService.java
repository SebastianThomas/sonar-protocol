package ch.sthomas.sonar.protocol.data.service;

import ch.sthomas.sonar.protocol.data.entity.*;
import ch.sthomas.sonar.protocol.data.repository.*;
import ch.sthomas.sonar.protocol.model.*;
import ch.sthomas.sonar.protocol.model.action.Mine;
import ch.sthomas.sonar.protocol.model.exception.*;
import ch.sthomas.sonar.protocol.model.play.Location;

import com.nimbusds.jose.util.Pair;

import jakarta.annotation.Nullable;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GameDataService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final PathEntityRepository pathEntityRepository;
    private final PathNodeEntityRepository pathNodeEntityRepository;
    private final MineRepository mineRepository;
    private final ShipRepository shipRepository;

    public GameDataService(
            final GameRepository gameRepository,
            final PlayerRepository playerRepository,
            final TeamRepository teamRepository,
            final PathEntityRepository pathEntityRepository,
            final PathNodeEntityRepository pathNodeEntityRepository,
            final MineRepository mineRepository,
            final ShipRepository shipRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.pathEntityRepository = pathEntityRepository;
        this.pathNodeEntityRepository = pathNodeEntityRepository;
        this.mineRepository = mineRepository;
        this.shipRepository = shipRepository;
    }

    public Optional<Game> findGameById(final long id) {
        return gameRepository.findById(id).map(GameEntity::toRecord);
    }

    public Optional<Player> findPlayerById(final long id) {
        return playerRepository.findById(id).map(PlayerEntity::toRecord);
    }

    public Game createGame(final List<Player> playersA, final List<Player> playersB) {
        final var teamA = createTeamEntity(playersA);
        final var teamB = createTeamEntity(playersB);
        final var game = new GameEntity(teamA, teamB, GameState.CREATED);
        return gameRepository.save(game).toRecord();
    }

    private TeamEntity createTeamEntity(final List<Player> players) {
        final var ship = createShipEntity();
        final var team =
                new TeamEntity(ship, players.stream().map(PlayerEntity::fromRecord).toList());
        return teamRepository.save(team);
    }

    private ShipEntity createShipEntity() {
        return new ShipEntity();
    }

    public Player createPlayer(
            final String name, final PlayerRole role, @Nullable final String wsSessionId) {
        final var player = new PlayerEntity(name, role, wsSessionId);
        return playerRepository.save(player).toRecord();
    }

    public Game joinGame(final Player player, final long gameId, final Team.ID team)
            throws GameNotFoundException {
        final var gameAndTeam = findGameAndTeam(gameId, team);
        final var newPlayers =
                Stream.concat(
                                gameAndTeam.getRight().getPlayers().stream(),
                                Stream.of(PlayerEntity.fromRecord(player)))
                        .toList();
        gameAndTeam.getRight().setPlayers(newPlayers);
        return gameRepository.save(gameAndTeam.getLeft()).toRecord();
    }

    public Path setSurfaced(final long pathId) {
        pathEntityRepository.setSurfaced(pathId);
        return pathEntityRepository.findById(pathId).orElseThrow().toRecord();
    }

    public Path saveNewPath(final Ship ship, final Location location, final Instant time) {
        final var shipEntity = shipRepository.findById(ship.id()).orElseThrow();
        return pathEntityRepository
                .save(constructNewPathEntity(shipEntity, location, time))
                .toRecord();
    }

    public Path setStartPosition(final long gameId, final Team.ID teamId, final Location location)
            throws GameNotFoundException {
        final var gameAndTeam = findGameAndTeam(gameId, teamId);
        final var newPath =
                constructNewPathEntity(gameAndTeam.getRight().getShip(), location, Instant.now());
        pathEntityRepository.deleteByShip(gameAndTeam.getRight().getShip());
        return pathEntityRepository.save(newPath).toRecord();
    }

    private PathEntity constructNewPathEntity(
            final ShipEntity ship, final Location location, final Instant time) {
        return new PathEntity(ship, PathNodeEntity.createNewPath(location, time));
    }

    public Pair<GameEntity, TeamEntity> findGameAndTeam(final long gameId, final Team.ID teamId)
            throws GameNotFoundException {
        return gameRepository
                .findById(gameId)
                .map(
                        game ->
                                Pair.of(
                                        game,
                                        switch (teamId) {
                                            case A -> game.a();
                                            case B -> game.b();
                                        }))
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }

    public Game startGame(final long gameId) throws GameException {
        final var gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            throw new GameNotFoundException(gameId);
        }
        final var game = gameOpt.get();
        if (pathsNotEmpty(game)) {
            throw new GameNotStartedException();
        }
        game.setState(GameState.RUNNING);
        return gameRepository.save(game).toRecord();
    }

    private boolean pathsNotEmpty(final GameEntity game) {
        return pathsNotEmpty(game.a()) && pathsNotEmpty(game.b());
    }

    private boolean pathsNotEmpty(final TeamEntity team) {
        return !team.getShip().getPaths().isEmpty()
                && !team.getShip().getPaths().getFirst().getNodes().isEmpty();
    }

    public Optional<Game> findGameWithPlayerId(final long playerId) {
        return gameRepository.findByPlayer(playerId).map(GameEntity::toRecord);
    }

    public Optional<Player> updatePlayerWsSessionId(final long playerId, final String wsSessionId) {
        playerRepository.updatePlayerWsSessionId(playerId, wsSessionId);
        return playerRepository.findById(playerId).map(PlayerEntity::toRecord);
    }

    public Optional<Mine> findMine(final long gameId, final Team.ID team, final Location location) {
        return (switch (team) {
                    case A -> mineRepository.findByTeamA(gameId, location.x(), location.y());
                    case B -> mineRepository.findByTeamB(gameId, location.x(), location.y());
                })
                .map(MineEntity::toRecord);
    }

    public Optional<MineEntity> findMineEntity(
            final long gameId, final Team.ID team, final Location location) {
        return (switch (team) {
            case A -> mineRepository.findByTeamA(gameId, location.x(), location.y());
            case B -> mineRepository.findByTeamB(gameId, location.x(), location.y());
        });
    }

    public void deleteMine(final MineEntity mine) {
        mineRepository.delete(mine);
    }

    public void addDamage(final Ship ship, final int damage) {
        shipRepository.addDamage(ship.id(), damage);
    }

    public void finishGame(final Game game) {
        gameRepository.setGameState(game.id(), GameState.STOPPED);
    }

    public Ship findTeamShip(final long gameId, final Team.ID team) {
        return (switch (team) {
                    case A -> shipRepository.findByGameIdAndTeamA(gameId);
                    case B -> shipRepository.findByGameIdAndTeamB(gameId);
                })
                .toRecord();
    }

    public Path savePathNode(final long pathId, final Location newLocation, final Instant time) {
        pathNodeEntityRepository.save(
                PathNodeEntity.createFromExistingPath(pathId, newLocation, time));

        return pathEntityRepository.findById(pathId).orElseThrow().toRecord();
    }

    public Path savePathNodes(
            final long pathId, final List<Location> newLocations, final Instant time) {
        final var targetLocation = newLocations.getLast();
        newLocations.forEach(
                newLocation ->
                        pathNodeEntityRepository.save(
                                PathNodeEntity.createFromExistingPathFinished(
                                        pathId,
                                        newLocation,
                                        time,
                                        newLocation.equals(targetLocation))));
        return pathEntityRepository.findById(pathId).orElseThrow().toRecord();
    }
}
