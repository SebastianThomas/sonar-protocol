package ch.sthomas.sonar.protocol.model.api;

import ch.sthomas.sonar.protocol.model.Team;
import ch.sthomas.sonar.protocol.model.play.Direction;

public record MovePayload(long gameId, Team.ID teamId, Direction direction) {}
