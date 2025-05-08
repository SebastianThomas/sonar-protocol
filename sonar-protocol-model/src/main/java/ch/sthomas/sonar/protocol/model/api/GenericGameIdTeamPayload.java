package ch.sthomas.sonar.protocol.model.api;

import ch.sthomas.sonar.protocol.model.Team;

public interface GenericGameIdTeamPayload {
    long gameId();

    Team.ID team();
}
