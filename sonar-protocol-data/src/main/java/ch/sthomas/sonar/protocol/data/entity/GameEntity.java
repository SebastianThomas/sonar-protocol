package ch.sthomas.sonar.protocol.data.entity;

import ch.sthomas.sonar.protocol.model.Game;
import ch.sthomas.sonar.protocol.model.GameState;

import jakarta.persistence.*;

@Entity
@Table(name = "t_games")
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_game_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_team_a", referencedColumnName = "pk_team_id")
    private TeamEntity a;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_team_b", referencedColumnName = "pk_team_id")
    private TeamEntity b;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameState state;

    public GameEntity() {}

    public GameEntity(final TeamEntity a, final TeamEntity b, final GameState state) {
        this.a = a;
        this.b = b;
        this.state = state;
    }

    public long id() {
        return id;
    }

    public TeamEntity a() {
        return a;
    }

    public TeamEntity b() {
        return b;
    }

    public Game toRecord() {
        return new Game(id, a.toRecord(), b.toRecord(), state);
    }

    public GameEntity setState(final GameState state) {
        this.state = state;
        return this;
    }

    public GameState getState() {
        return state;
    }
}
