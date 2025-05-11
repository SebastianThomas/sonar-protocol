package ch.sthomas.sonar.protocol.model.api;

import ch.sthomas.sonar.protocol.model.Team;
import ch.sthomas.sonar.protocol.model.play.DefectLocation;
import ch.sthomas.sonar.protocol.model.play.Direction;

public record GameIdTeamDefectPayload(
        long gameId, Team.ID team, Direction direction, DefectLocation defectLocation)
        implements GenericGameIdTeamPayload {}
