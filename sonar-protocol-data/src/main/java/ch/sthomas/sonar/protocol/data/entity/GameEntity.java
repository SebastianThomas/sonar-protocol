package ch.sthomas.sonar.protocol.data.entity

sonar.protocol.data.protocol.entity;

import ch.sthomas.sonar-protocol.model.frame.*;

import jakarta.persistence.*;

@Entity
@Table(name = "t_game")
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_game_id", nullable = false)
    private Long id;

    public Long id() {
        return id;
    }
}
