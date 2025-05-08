package ch.sthomas.sonar.protocol.data.entity;

import ch.sthomas.sonar.protocol.data.entity.id.SwitchEntityId;
import ch.sthomas.sonar.protocol.model.action.Switch;

import jakarta.persistence.*;

@Entity
@Table(name = "t_switch")
public class SwitchEntity {
    @EmbeddedId private SwitchEntityId id;

    @ManyToOne
    @MapsId("shipId")
    private ShipEntity ship;

    @Column(name = "position", nullable = false)
    private int position;

    public Switch toRecord() {
        return new Switch(id.action(), position);
    }
}
