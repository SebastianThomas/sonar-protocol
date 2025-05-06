package ch.sthomas.sonar.protocol.data.entity;

import ch.sthomas.sonar.protocol.model.Team;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "t_teams")
public class TeamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_team_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_ship_id", referencedColumnName = "pk_ship_id")
    private ShipEntity ship;

    @ManyToOne private GameEntity game;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "teamId")
    private List<PlayerEntity> players;

    public TeamEntity() {}

    public TeamEntity(final ShipEntity ship, final List<PlayerEntity> players) {
        this.ship = ship;
        this.players = players;
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public void setPlayers(final List<PlayerEntity> players) {
        this.players = players;
    }

    public Team toRecord() {
        return new Team(id, players.stream().map(PlayerEntity::toRecord).toList(), ship.toRecord());
    }

    public ShipEntity getShip() {
        return ship;
    }
}
