package ch.sthomas.sonar.protocol.model;

import java.util.List;

public record Ship(long id, List<Path> paths) {}
