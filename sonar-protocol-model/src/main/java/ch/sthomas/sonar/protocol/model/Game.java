package ch.sthomas.sonar.protocol.model;

public record Game(long id, Team a, Team b, GameState state) {}
