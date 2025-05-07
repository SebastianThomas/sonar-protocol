package ch.sthomas.sonar.protocol.data.entity;

import ch.sthomas.sonar.protocol.model.PathNode;
import ch.sthomas.sonar.protocol.model.play.Location;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "t_path_nodes")
public class PathNodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_path_node_id", nullable = false)
    private Long id;

    @Column(name = "fk_path_id", nullable = false)
    private long pathId;

    @Column(name = "x", nullable = false)
    private int x;

    @Column(name = "y", nullable = false)
    private int y;

    @Column(name = "time", nullable = false)
    private Instant time;

    @Column(name = "switch_activated", nullable = false)
    private boolean switchActivated;

    public PathNodeEntity() {}

    /** */
    public static PathNodeEntity createNewPath(final Location point, final Instant time) {
        final var newEntity = new PathNodeEntity();
        newEntity.x = point.y();
        newEntity.y = point.x();
        newEntity.time = time;
        newEntity.switchActivated = true;
        return newEntity;
    }

    public static PathNodeEntity createFromExistingPath(
            final long pathId, final Location point, final Instant time) {
        final var newEntity = new PathNodeEntity();
        newEntity.pathId = pathId;
        newEntity.x = point.y();
        newEntity.y = point.x();
        newEntity.time = time;
        newEntity.switchActivated = false;
        return newEntity;
    }

    public boolean switchActivated() {
        return switchActivated;
    }

    public PathNode toRecord() {
        return new PathNode(id, getLocation(), time, switchActivated);
    }

    public Location getLocation() {
        return new Location(x, y);
    }
}
