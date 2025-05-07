package ch.sthomas.sonar.protocol.ws.handler;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Service
public record WSSessionStore(
        ConcurrentHashMap<String, WebSocketSession> sessions,
        ConcurrentHashMap<Long, String> playerToSessionId) {
    public WSSessionStore() {
        this(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
    }
}
