package ch.sthomas.sonar.protocol.data.entity;

import ch.sthomas.sonar.protocol.model.Ship;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "t_ships")
public class ShipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_ship_id", nullable = false)
    private Long id;

    @Column(name = "health", nullable = false)
    private int health;

    @OneToMany(mappedBy = "shipId")
    private List<PathEntity> paths;

    @OneToOne private TeamEntity team;

    @OneToMany private List<SwitchEntity> switches;

    @OneToMany private List<DefectEntity> defects;

    public ShipEntity() {}

    public ShipEntity(final List<PathEntity> paths) {
        this.paths = paths;
    }

    public Ship toRecord() {
        return new Ship(id, health, paths.stream().map(PathEntity::toRecord).toList());
    }

    public List<PathEntity> getPaths() {
        return paths;
    }

    public long getId() {
        return id;
    }
}
