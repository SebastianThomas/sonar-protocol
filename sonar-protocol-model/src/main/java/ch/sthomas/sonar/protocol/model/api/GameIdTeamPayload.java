package ch.sthomas.sonar.protocol.model.api;

import ch.sthomas.sonar.protocol.model.Team;

public record GameIdTeamPayload(long gameId, Team.ID team) implements GenericGameIdTeamPayload {}
