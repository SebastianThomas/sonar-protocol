package ch.sthomas.sonar.protocol.data.entity;

import ch.sthomas.sonar.protocol.model.event.GameEvent;
import ch.sthomas.sonar.protocol.model.event.GameEventMessage;
import ch.sthomas.sonar.protocol.model.event.GameEventNotificationPolicy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "t_game_events")
public class GameEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_game_event_id")
    private int id;

    @Column(name = "fk_game_id", nullable = false)
    private long gameId;

    // @ManyToOne(optional = false, cascade = CascadeType.ALL)
    // private GameEntity game;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private PlayerEntity player;

    @Column(name = "event", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameEvent event;

    @Column(name = "notification_policy")
    @Enumerated(EnumType.STRING)
    private GameEventNotificationPolicy notificationPolicy;

    @Column(name = "clazz", nullable = false)
    private String clazz;

    @Column(name = "time")
    private Instant time;

    @Column(name = "data")
    @Lob
    private String data;

    public GameEventEntity() {}

    public <T> GameEventEntity(
            final ObjectMapper objectMapper,
            final PlayerEntity player,
            final GameEventMessage<T> message)
            throws JsonProcessingException {
        this(
                objectMapper,
                message.gameId(),
                player,
                message.event(),
                message.policy(),
                message.time(),
                message.data());
    }

    public <T> GameEventEntity(
            final ObjectMapper objectMapper,
            final long gameId,
            final PlayerEntity player,
            final GameEvent event,
            final GameEventNotificationPolicy notificationPolicy,
            final Instant time,
            final T data)
            throws JsonProcessingException {
        this.gameId = gameId;
        this.player = player;
        this.event = event;
        this.notificationPolicy = notificationPolicy;
        this.time = time;
        this.clazz = data.getClass().getCanonicalName();
        this.data = objectMapper.writeValueAsString(data);
    }

    public GameEventMessage<?> toRecord(final ObjectMapper objectMapper)
            throws ClassNotFoundException, JsonProcessingException {
        final var dataClass = Class.forName(clazz);
        return new GameEventMessage<>(
                gameId,
                time,
                List.of(player.toRecord()),
                notificationPolicy,
                event,
                objectMapper.readValue(data, dataClass));
    }
}
