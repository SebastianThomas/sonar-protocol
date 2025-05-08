package ch.sthomas.sonar.protocol.model.api;

import ch.sthomas.sonar.protocol.model.Team;
import ch.sthomas.sonar.protocol.model.action.Action;

public record GameIdTeamActionPayload(long gameId, Team.ID team, Action action)
        implements GenericGameIdTeamPayload {}
