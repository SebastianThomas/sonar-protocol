package ch.sthomas.sonar.protocol.ws.controller;

import ch.sthomas.sonar.protocol.model.Location;
import ch.sthomas.sonar.protocol.model.Path;
import ch.sthomas.sonar.protocol.model.Team;
import ch.sthomas.sonar.protocol.model.exception.GameException;
import ch.sthomas.sonar.protocol.model.exception.GameNotFoundException;
import ch.sthomas.sonar.protocol.model.play.Direction;
import ch.sthomas.sonar.protocol.service.GameService;

import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/v1/game/play")
public class PlayController {
    private final GameService gameService;

    public PlayController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/move")
    public Path move(
            @RequestParam final long gameId,
            @RequestParam final Team.ID team,
            @RequestBody final Direction direction)
            throws GameNotFoundException, GameException {
        return gameService.move(gameId, team, direction);
    }

    @PostMapping("set-start-position")
    public Path setStartPosition(
            @RequestParam final long gameId,
            @RequestParam final Team.ID team,
            @RequestBody @Valid final Location location)
            throws GameNotFoundException {
        return gameService.setStartPosition(gameId, team, location);
    }

    @PostMapping("/surface")
    public Path surface(@RequestParam final long gameId, @RequestParam final Team.ID team)
            throws GameException, GameNotFoundException {
        return gameService.surface(gameId, team);
    }

    @PostMapping("/submerge")
    public Path submerge(@RequestParam final long gameId, @RequestParam final Team.ID team)
            throws GameException, GameNotFoundException {
        return gameService.submerge(gameId, team);
    }
}
