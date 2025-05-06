package ch.sthomas.sonar.protocol.model;

import java.util.List;

public record Path(long id, List<PathNode> nodes, boolean surfaced) {}
