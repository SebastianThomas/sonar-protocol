package ch.sthomas.sonar.protocol.ws.controller;

import ch.sthomas.sonar.protocol.model.Game;
import ch.sthomas.sonar.protocol.model.Player;
import ch.sthomas.sonar.protocol.model.api.JoinGamePayload;
import ch.sthomas.sonar.protocol.model.api.PlayerIdBody;
import ch.sthomas.sonar.protocol.model.api.PlayerPayload;
import ch.sthomas.sonar.protocol.model.exception.GameException;
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

    @PostMapping("/player")
    public Player createPlayer(@RequestBody final PlayerPayload playerPayload) {
        return gameService.createPlayer(playerPayload, null);
    }

    @PostMapping("")
    public Game createGame(@RequestBody final PlayerIdBody playerIdBody)
            throws PlayerNotFoundException {
        return gameService.createGame(playerIdBody.playerId());
    }

    @PostMapping("/start")
    public Game startGame(@RequestParam final long gameId)
            throws GameNotFoundException, GameException {
        return gameService.startGame(gameId);
    }

    @PostMapping("/join")
    public Game joinGame(@RequestBody final JoinGamePayload joinGamePayload)
            throws PlayerNotFoundException, GameNotFoundException {
        return gameService.joinGame(joinGamePayload);
    }

    @GetMapping("")
    public Game getGame(@RequestParam final long gameId) throws GameNotFoundException {
        return gameService.findGame(gameId);
    }
}
