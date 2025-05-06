package ch.sthomas.sonar.protocol.data.service;

import ch.sthomas.sonar.protocol.data.entity.PathEntity;
import ch.sthomas.sonar.protocol.data.entity.ShipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface PathEntityRepository extends JpaRepository<PathEntity, Long> {
    void deleteByShip(ShipEntity ship);
}
