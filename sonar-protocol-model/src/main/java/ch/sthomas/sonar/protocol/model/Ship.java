package ch.sthomas.sonar.protocol.model;

import ch.sthomas.sonar.protocol.model.action.Defect;

import java.util.List;

public record Ship(long id, int health, List<Defect> defects, List<Path> paths) {
    public boolean isDown() {
        return !isHealthy();
    }

    public boolean isHealthy() {
        return health > 0;
    }
}
