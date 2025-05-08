package ch.sthomas.sonar.protocol.model.api;

import ch.sthomas.sonar.protocol.model.Team;
import ch.sthomas.sonar.protocol.model.play.Sector;

public record GameIdTeamSectorPayload(long gameId, Team.ID team, Sector sector)
        implements GenericGameIdTeamPayload {}
