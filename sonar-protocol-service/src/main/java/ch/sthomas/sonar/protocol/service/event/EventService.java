package ch.sthomas.sonar.protocol.service.event;

import ch.sthomas.sonar.protocol.data.service.EventDataService;
import ch.sthomas.sonar.protocol.model.event.GameEventMessage;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;

@Service
public class EventService {
    private final EventDataService eventDataService;

    public EventService(final EventDataService eventDataService) {
        this.eventDataService = eventDataService;
    }

    public <T> int insertEvent(final GameEventMessage<T> event) {
        return eventDataService.insertEvent(event);
    }

    public Collection<GameEventMessage<?>> findEventsForPlayerSince(
            final long playerId, final Instant timestamp) {
        return eventDataService.findMessagesForPlayerSince(playerId, timestamp);
    }
}
