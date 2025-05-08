package ch.sthomas.sonar.protocol.data.entity;

import ch.sthomas.sonar.protocol.data.entity.id.DefectEntityId;
import ch.sthomas.sonar.protocol.model.action.ActionCategory;
import ch.sthomas.sonar.protocol.model.action.Defect;
import ch.sthomas.sonar.protocol.model.play.DefectLocation;

import jakarta.persistence.*;

@Entity
@Table(name = "t_defect")
public class DefectEntity {
    @EmbeddedId private DefectEntityId id;

    @ManyToOne
    @MapsId("shipId")
    private ShipEntity ship;

    @Column(name = "action_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionCategory actionCategory;

    @Column(name = "critical", nullable = false)
    private boolean critical;

    public Defect toRecord() {
        return new Defect(
                new DefectLocation(id.x(), id.y()), actionCategory, id.direction(), critical);
    }
}
