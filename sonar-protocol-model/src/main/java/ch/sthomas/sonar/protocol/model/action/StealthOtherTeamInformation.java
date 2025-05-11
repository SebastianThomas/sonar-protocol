package ch.sthomas.sonar.protocol.model.action;

import ch.sthomas.sonar.protocol.model.Team;
import ch.sthomas.sonar.protocol.model.play.Direction;

public record StealthOtherTeamInformation(Team.ID team, Direction direction) {}
