package ch.sthomas.sonar.protocol.model;

import java.util.List;

public record Ship(long id, int health, List<Path> paths) {
    public boolean isDown() {
        return !isHealthy();
    }

    public boolean isHealthy() {
        return health > 0;
    }
}
