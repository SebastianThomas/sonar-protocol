package ch.sthomas.sonar.protocol.model.action;

import ch.sthomas.sonar.protocol.model.play.DefectLocation;
import ch.sthomas.sonar.protocol.model.play.Direction;

public record Defect(
        DefectLocation location, ActionCategory action, Direction direction, boolean critical) {}
