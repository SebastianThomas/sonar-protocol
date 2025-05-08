package ch.sthomas.sonar.protocol.data.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class MineEntityId {
    @Column(name = "fk_ship_id", nullable = false)
    private long shipId;

    @Column(name = "x", nullable = false)
    private int x;

    @Column(name = "y", nullable = false)
    private int y;

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }
}
