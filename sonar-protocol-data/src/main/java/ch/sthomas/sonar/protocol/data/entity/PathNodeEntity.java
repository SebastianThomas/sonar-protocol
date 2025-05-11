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

    @Column(name = "defect_crossed", nullable = false)
    private boolean defectCrossed;

    /** */
    public static PathNodeEntity createNewPath(final Location point, final Instant time) {
        final var newEntity = new PathNodeEntity();
        newEntity.x = point.y();
        newEntity.y = point.x();
        newEntity.time = time;
        newEntity.switchActivated = true;
        newEntity.defectCrossed = true;
        return newEntity;
    }

    public static PathNodeEntity createFromExistingPathFinished(
            final long pathId, final Location point, final Instant time, final boolean finished) {
        final var newEntity = new PathNodeEntity();
        newEntity.pathId = pathId;
        newEntity.x = point.y();
        newEntity.y = point.x();
        newEntity.time = time;
        newEntity.switchActivated = finished;
        newEntity.defectCrossed = finished;
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
        newEntity.defectCrossed = false;
        return newEntity;
    }

    public PathNode toRecord() {
        return new PathNode(id, getLocation(), time, switchActivated, defectCrossed);
    }

    public Location getLocation() {
        return new Location(x, y);
    }
}
