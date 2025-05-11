package ch.sthomas.sonar.protocol.model.action;

import ch.sthomas.sonar.protocol.model.Path;
import ch.sthomas.sonar.protocol.model.Team;
import ch.sthomas.sonar.protocol.model.play.Direction;

public record StealthInformation(Team.ID team, int amount, Direction direction, Path currentPath) {}
