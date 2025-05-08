package ch.sthomas.sonar.protocol.ws.controller;

import ch.sthomas.sonar.protocol.model.Game;
import ch.sthomas.sonar.protocol.model.api.PlayerIdPayload;
import ch.sthomas.sonar.protocol.model.exception.GameNotFoundException;
import ch.sthomas.sonar.protocol.model.exception.PlayerNotFoundException;
import ch.sthomas.sonar.protocol.service.GameService;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/v1/game")
public class GameController {
    private final GameService gameService;

    public GameController(final GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("")
    public Game createGame(@RequestBody final PlayerIdPayload playerIdPayload)
            throws PlayerNotFoundException {
        return gameService.createGame(playerIdPayload.playerId());
    }

    @GetMapping("")
    public Game getGame(@RequestParam final long gameId) throws GameNotFoundException {
        return gameService.findGame(gameId);
    }
}
