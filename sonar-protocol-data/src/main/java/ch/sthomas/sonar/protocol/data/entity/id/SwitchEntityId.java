package ch.sthomas.sonar.protocol.data.entity.id;

import ch.sthomas.sonar.protocol.model.action.Action;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class SwitchEntityId {
    @Column(name = "fk_ship_id")
    private long shipId;

    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    private Action action;

    public Action action() {
        return action;
    }
}
