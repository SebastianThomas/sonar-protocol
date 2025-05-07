package ch.sthomas.sonar.protocol.model;

import ch.sthomas.sonar.protocol.model.play.Location;

import java.time.Instant;

public record PathNode(long id, Location location, Instant time, boolean switchActivated) {}
