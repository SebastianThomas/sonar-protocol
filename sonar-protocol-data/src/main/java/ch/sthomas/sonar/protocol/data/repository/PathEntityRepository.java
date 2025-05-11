package ch.sthomas.sonar.protocol.data.repository;

import ch.sthomas.sonar.protocol.data.entity.PathEntity;
import ch.sthomas.sonar.protocol.data.entity.ShipEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PathEntityRepository extends JpaRepository<PathEntity, Long> {
    void deleteByShip(ShipEntity ship);

    @Query("UPDATE PathEntity SET surfaced = TRUE WHERE id = :pathId")
    @Modifying
    void setSurfaced(long pathId);
}
