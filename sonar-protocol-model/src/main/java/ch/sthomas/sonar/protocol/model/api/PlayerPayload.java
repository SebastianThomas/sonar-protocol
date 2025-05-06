package ch.sthomas.sonar.protocol.model.api;

import ch.sthomas.sonar.protocol.model.PlayerRole;

public record PlayerPayload(String name, PlayerRole role) {}
