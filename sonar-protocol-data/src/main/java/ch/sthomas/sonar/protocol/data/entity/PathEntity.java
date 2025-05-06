package ch.sthomas.sonar.protocol.data.entity;

import ch.sthomas.sonar.protocol.model.Path;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "t_paths")
public class PathEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_path_id", nullable = false)
    private Long id;

    @Column(name = "fk_ship_id", nullable = false)
    private long shipId;

    @ManyToOne(optional = false)
    private ShipEntity ship;

    @Column(name = "surfaced", nullable = false)
    private boolean surfaced;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pathId")
    @OrderBy("time")
    private List<PathNodeEntity> nodes;

    public PathEntity() {}

    public PathEntity(final ShipEntity ship, final PathNodeEntity firstNode) {
        this.shipId = ship.getId();
        this.ship = ship;
        this.surfaced = false;
        this.nodes = List.of(firstNode);
    }

    public Path toRecord() {
        return new Path(id, nodes.stream().map(PathNodeEntity::toRecord).toList(), surfaced);
    }

    public boolean surfaced() {
        return surfaced;
    }

    public List<PathNodeEntity> getNodes() {
        return nodes;
    }

    public long getId() {
        return id;
    }

    public PathEntity setNodes(final List<PathNodeEntity> nodes) {
        this.nodes = nodes;
        return this;
    }

    public PathEntity setSurfaced(final boolean surfaced) {
        this.surfaced = surfaced;
        return this;
    }

    public ShipEntity getShip() {
        return ship;
    }
}
