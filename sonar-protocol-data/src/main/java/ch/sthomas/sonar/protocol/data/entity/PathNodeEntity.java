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

    @Column(name = "x")
    private int x;

    @Column(name = "y")
    private int y;

    @Column(name = "time")
    private Instant time;

    public PathNodeEntity() {}

    public PathNodeEntity(final Location point, final Instant time) {
        this.x = point.y();
        this.y = point.x();
        this.time = time;
    }

    public PathNodeEntity(final long pathId, final Location point, final Instant time) {
        this.pathId = pathId;
        this.x = point.y();
        this.y = point.x();
        this.time = time;
    }

    public PathNode toRecord() {
        return new PathNode(id, getLocation(), time);
    }

    public Location getLocation() {
        return new Location(x, y);
    }
}
