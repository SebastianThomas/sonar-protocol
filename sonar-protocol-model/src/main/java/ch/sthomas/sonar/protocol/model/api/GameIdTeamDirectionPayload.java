package ch.sthomas.sonar.protocol.model.api;

import ch.sthomas.sonar.protocol.model.Team;
import ch.sthomas.sonar.protocol.model.play.Direction;

public record GameIdTeamDirectionPayload(long gameId, Team.ID team, Direction direction)
        implements GenericGameIdTeamPayload {}
