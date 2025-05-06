package ch.sthomas.sonar.protocol.model.api;

import ch.sthomas.sonar.protocol.model.Team;

public record JoinGamePayload(long playerId, long gameId, Team.ID team) {}
