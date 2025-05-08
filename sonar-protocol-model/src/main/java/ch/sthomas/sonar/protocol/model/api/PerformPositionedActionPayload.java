package ch.sthomas.sonar.protocol.model.api;

import ch.sthomas.sonar.protocol.model.action.Action;
import ch.sthomas.sonar.protocol.model.play.Location;

public record PerformPositionedActionPayload(Location location, Action action) {}
