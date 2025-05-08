package ch.sthomas.sonar.protocol.data.service;

import ch.sthomas.sonar.protocol.data.entity.ShipEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShipRepository extends JpaRepository<ShipEntity, Long> {
    @Query("UPDATE ShipEntity s SET s.health = s.health + :damage WHERE s.id = :shipId")
    @Modifying
    void addDamage(long shipId, int damage);
}
