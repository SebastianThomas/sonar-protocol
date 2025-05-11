package ch.sthomas.sonar.protocol.ws.handler;

import ch.sthomas.sonar.protocol.model.event.GameEventListener;
import ch.sthomas.sonar.protocol.model.event.GameEventMessage;
import ch.sthomas.sonar.protocol.service.event.EventService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DBGameEventListener implements GameEventListener {

    private static final Logger logger = LoggerFactory.getLogger(DBGameEventListener.class);
    private final EventService eventService;

    public DBGameEventListener(final EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public <T> void pushEvent(final GameEventMessage<T> event) {
        final var count = eventService.insertEvent(event);
        if (count == 0) {
            logger.info("Inserted no event entities into DB for event {}.", event);
        } else {
            logger.info("Inserted {} event entities into DB for event.", count);
        }
    }
}
