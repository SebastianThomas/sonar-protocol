package ch.sthomas.sonar.protocol.model;

import ch.sthomas.sonar.protocol.model.play.Location;

import java.util.List;

public record Path(long id, List<PathNode> nodes, boolean surfaced) implements Comparable<Path> {
    @Override
    public int compareTo(final Path o) {
        return Long.compare(id, o.id);
    }

    public boolean isOnPath(final Location newLocation) {
        return nodes.stream().map(PathNode::location).anyMatch(newLocation::equals);
    }
}
