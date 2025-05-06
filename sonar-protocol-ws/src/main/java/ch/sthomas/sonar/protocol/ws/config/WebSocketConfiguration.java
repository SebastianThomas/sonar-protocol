package ch.sthomas.sonar.protocol.ws.config;

import ch.sthomas.sonar.protocol.ws.handler.GameWSHandler;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final GameWSHandler gameWSHandler;

    public WebSocketConfiguration(final GameWSHandler gameWSHandler) {
        this.gameWSHandler = gameWSHandler;
    }

    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        // TODO: Set allowed origins
        registry.addHandler(gameWSHandler, "/ws/game");
    }
}
