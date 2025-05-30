package ch.sthomas.sonar.protocol.model.api;

import ch.sthomas.sonar.protocol.model.Team;
import ch.sthomas.sonar.protocol.model.play.Location;

public record SetLocationPayload(long gameId, Team.ID team, Location location) {}
