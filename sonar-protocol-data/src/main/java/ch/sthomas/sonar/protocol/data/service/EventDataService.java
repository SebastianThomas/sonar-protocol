package ch.sthomas.sonar.protocol.data.service;

import ch.sthomas.sonar.protocol.data.entity.GameEventEntity;
import ch.sthomas.sonar.protocol.data.entity.PlayerEntity;
import ch.sthomas.sonar.protocol.data.repository.GameEventRepository;
import ch.sthomas.sonar.protocol.model.event.GameEventMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingFunction;

import java.time.Instant;
import java.util.Collection;

@Service
public class EventDataService {
    private final ObjectMapper objectMapper;
    private final GameEventRepository gameEventRepository;

    public EventDataService(
            final ObjectMapper objectMapper, final GameEventRepository gameEventRepository) {
        this.objectMapper = objectMapper;
        this.gameEventRepository = gameEventRepository;
    }

    public <T> int insertEvent(final GameEventMessage<T> event) {
        final var entities =
                event.notifiedPlayers().stream()
                        .map(
                                ThrowingFunction.of(
                                        player ->
                                                new GameEventEntity(
                                                        objectMapper,
                                                        new PlayerEntity(player),
                                                        event)))
                        .toList();
        return gameEventRepository.saveAll(entities).size();
    }

    public Collection<GameEventMessage<?>> findMessagesForPlayerSince(
            final long playerId, final Instant since) {
        return gameEventRepository.findByPlayer_IdAndTimeAfter(playerId, since).stream()
                .<GameEventMessage<?>>map(ThrowingFunction.of(g -> g.toRecord(objectMapper)))
                .toList();
    }
}
