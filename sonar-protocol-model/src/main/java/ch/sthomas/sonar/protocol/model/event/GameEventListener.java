package ch.sthomas.sonar.protocol.model.event;

public interface GameEventListener {
    <T> void pushEvent(GameEventMessage<T> event);
}
