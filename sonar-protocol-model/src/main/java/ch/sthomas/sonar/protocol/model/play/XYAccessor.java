package ch.sthomas.sonar.protocol.model.play;

import java.text.MessageFormat;

public interface XYAccessor {
    int x();

    int y();

    default String getPointString() {
        return MessageFormat.format("P({0},{1})", x(), y());
    }
}
