package ch.sthomas.sonar.protocol.data.entity;

import ch.sthomas.sonar.protocol.model.Player;
import ch.sthomas.sonar.protocol.model.PlayerRole;

import jakarta.persistence.*;

@Entity
@Table(name = "t_players")
public class PlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_player_id", nullable = false)
    private Long id;

    @Column(name = "fk_team_id", nullable = false)
    private Long teamId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlayerRole role;

    @Column(name = "ws_session_id")
    private String wsSessionId;

    public long id() {
        return id;
    }

    public PlayerEntity() {}

    public PlayerEntity(final String name, final PlayerRole role, final String wsSessionId) {
        this.name = name;
        this.role = role;
        this.wsSessionId = wsSessionId;
    }

    public Player toRecord() {
        return new Player(id, name, role, wsSessionId);
    }

    public static PlayerEntity fromRecord(final Player player) {
        final var p = new PlayerEntity(player.name(), player.role(), player.wsSessionId());
        p.id = player.id();
        return p;
    }
}
