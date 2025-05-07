package ch.sthomas.sonar.protocol.ws.config;

import ch.sthomas.sonar.protocol.model.event.GameEventListeners;
import ch.sthomas.sonar.protocol.ws.handler.WebSocketGameEventListener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GameEventConfig {
    @Bean
    public GameEventListeners listeners(
            final WebSocketGameEventListener webSocketGameEventListener) {
        return new GameEventListeners(List.of(webSocketGameEventListener));
    }
}
