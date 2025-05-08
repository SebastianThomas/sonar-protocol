package ch.sthomas.sonar.protocol.data.entity;

import ch.sthomas.sonar.protocol.data.entity.id.MineEntityId;
import ch.sthomas.sonar.protocol.model.action.Mine;
import ch.sthomas.sonar.protocol.model.play.Location;

import jakarta.persistence.*;

@Entity
@Table(name = "t_unexploded_mines")
public class MineEntity {
    @EmbeddedId private MineEntityId id;

    @ManyToOne
    @MapsId("shipId")
    private ShipEntity ship;

    public Mine toRecord() {
        return new Mine(getLocation());
    }

    public Location getLocation() {
        return new Location(id.x(), id.y());
    }
}
