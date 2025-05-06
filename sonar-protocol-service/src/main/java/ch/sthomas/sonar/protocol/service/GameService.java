package ch.sthomas.sonar.protocol.service;

import ch.sthomas.sonar.protocol.data.service.GameDataService;
import ch.sthomas.sonar.protocol.model.*;
import ch.sthomas.sonar.protocol.model.exception.*;
import ch.sthomas.sonar.protocol.model.play.Direction;

import ch.sthomas.sonar.protocol.model.play.Location;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    private final GameDataService gameDataService;

    public GameService(final GameDataService gameDataService) {
        this.gameDataService = gameDataService;
    }

    public Game findGame(final long gameId) throws GameNotFoundException {
        return gameDataService.findGameById(gameId).orElseThrow(GameNotFoundException::new);
    }

    public Player createPlayer(final String name, final PlayerRole role) {
        return gameDataService.createPlayer(name, role);
    }

    public Game createGame(final long gameMasterId) throws PlayerNotFoundException {
        final var gameMaster =
                gameDataService
                        .findPlayerById(gameMasterId)
                        .orElseThrow(PlayerNotFoundException::new);
        return gameDataService.createGame(List.of(gameMaster), List.of());
    }

    public Game joinGame(final long playerId, final long gameId, final Team.ID team)
            throws GameNotFoundException, PlayerNotFoundException {
        return gameDataService.joinGame(
                gameDataService.findPlayerById(playerId).orElseThrow(PlayerNotFoundException::new),
                gameId,
                team);
    }

    public Game startGame(final long gameId) throws GameNotFoundException, GameException {
        return gameDataService.startGame(gameId);
    }

    public Path move(final long gameId, final Team.ID teamId, final Direction direction)
            throws GameNotFoundException, GameException {
        return gameDataService.move(gameId, teamId, direction);
    }

    public Path surface(final long gameId, final Team.ID team)
            throws GameException, GameNotFoundException {
        return gameDataService.surface(gameId, team);
    }

    public Path submerge(final long gameId, final Team.ID team)
            throws GameException, GameNotFoundException {
        return gameDataService.submerge(gameId, team);
    }

    public Path setStartPosition(final long gameId, final Team.ID team, final Location location)
            throws GameNotFoundException {
        return gameDataService.setStartPosition(gameId, team, location);
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
}
