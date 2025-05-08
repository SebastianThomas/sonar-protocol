package ch.sthomas.sonar.protocol.data.service;

import ch.sthomas.sonar.protocol.data.entity.*;
import ch.sthomas.sonar.protocol.data.repository.GameRepository;
import ch.sthomas.sonar.protocol.data.repository.PlayerRepository;
import ch.sthomas.sonar.protocol.model.*;
import ch.sthomas.sonar.protocol.model.action.Mine;
import ch.sthomas.sonar.protocol.model.exception.*;
import ch.sthomas.sonar.protocol.model.play.Direction;
import ch.sthomas.sonar.protocol.model.play.Location;
import ch.sthomas.sonar.protocol.model.util.VectorUtils;

import com.google.common.collect.MoreCollectors;
import com.nimbusds.jose.util.Pair;

import jakarta.annotation.Nullable;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
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

    public Path move(final long gameId, final Team.ID teamId, final Direction direction)
            throws GameNotFoundException, GameException {
        final var lastPath = getLastPathEntity(gameId, teamId);
        final var lastNode = getLastNode(lastPath);
        if (!lastNode.isFinished()) {
            throw new PreviousRoundNotFinishedException();
        }
        final var newLocation = VectorUtils.add(lastNode.getLocation(), direction.vector());
        if (isOnPath(lastPath, newLocation)) {
            throw new CannotCrossOwnPathException(newLocation);
        }
        if (findMineEntity(gameId, teamId, newLocation).isPresent()) {
            throw new CannotMoveIntoOwnMineException(newLocation);
        }
        pathNodeEntityRepository.save(
                PathNodeEntity.createFromExistingPath(
                        lastPath.getId(), newLocation, Instant.now()));
        return pathEntityRepository.findById(lastPath.getId()).orElseThrow().toRecord();
    }

    public Path surface(final long gameId, final Team.ID team)
            throws GameException, GameNotFoundException {
        final var lastPath = getLastPathEntity(gameId, team);
        if (lastPath.surfaced()) {
            throw new NotSubmergedException();
        }
        return pathEntityRepository.save(lastPath.setSurfaced(true)).toRecord();
    }

    public Path submerge(final long gameId, final Team.ID team)
            throws GameException, GameNotFoundException {
        final var lastPath = getLastPathEntity(gameId, team);
        if (!lastPath.surfaced()) {
            throw new NotSurfacedException();
        }
        return pathEntityRepository
                .save(
                        constructNewPathEntity(
                                lastPath.getShip(), getLastNode(lastPath).getLocation()))
                .toRecord();
    }

    public Path setStartPosition(final long gameId, final Team.ID teamId, final Location location)
            throws GameNotFoundException {
        final var gameAndTeam = findGameAndTeam(gameId, teamId);
        final var newPath = constructNewPathEntity(gameAndTeam.getRight().getShip(), location);
        pathEntityRepository.deleteByShip(gameAndTeam.getRight().getShip());
        return pathEntityRepository.save(newPath).toRecord();
    }

    private PathEntity constructNewPathEntity(final ShipEntity ship, final Location location) {
        return new PathEntity(ship, PathNodeEntity.createNewPath(location, Instant.now()));
    }

    private PathEntity getLastPathEntity(final long gameId, final Team.ID teamId)
            throws GameNotFoundException, GameException {
        final var gameAndTeam = findGameAndTeam(gameId, teamId);
        if (gameAndTeam.getLeft().getState() == GameState.CREATED) {
            throw new GameNotStartedException();
        }
        final var lastPath =
                gameAndTeam.getRight().getShip().getPaths().stream()
                        .filter(Predicate.not(PathEntity::surfaced))
                        .collect(MoreCollectors.toOptional())
                        .orElseThrow(NotSubmergedException::new);
        if (lastPath.getNodes().isEmpty()) {
            throw new IllegalGameStateException("Path is empty.");
        }
        return lastPath;
    }

    private PathNodeEntity getLastNode(final PathEntity path) {
        return path.getNodes().getLast();
    }

    private boolean isOnPath(final PathEntity lastPath, final Location newLocation) {
        return lastPath.getNodes().stream()
                .map(PathNodeEntity::getLocation)
                .anyMatch(newLocation::equals);
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

    public Game startGame(final long gameId) throws GameException, GameNotFoundException {
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
}
