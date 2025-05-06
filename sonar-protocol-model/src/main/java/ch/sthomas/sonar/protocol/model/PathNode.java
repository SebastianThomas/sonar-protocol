package ch.sthomas.sonar.protocol.model;

import java.time.Instant;

public record PathNode(long id, Location point, Instant time) {}
