package ch.sthomas.sonar.protocol.model.api;

import ch.sthomas.sonar.protocol.model.Team;
import ch.sthomas.sonar.protocol.model.play.Direction;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GameIdTeamDirectionAmountPayload(
        long gameId, Team.ID team, Direction direction, @Min(0) @Max(4) int amount)
        implements GenericGameIdTeamPayload {}
