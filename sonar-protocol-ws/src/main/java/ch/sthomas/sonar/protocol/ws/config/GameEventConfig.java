package ch.sthomas.sonar.protocol.ws.config;

import ch.sthomas.sonar.protocol.model.event.GameEventListeners;

import org.springframework.context.annotation.Bean;

import java.util.List;

public class GameEventConfig {
    @Bean
    public GameEventListeners listeners() {
        return new GameEventListeners(List.of());
    }
}
