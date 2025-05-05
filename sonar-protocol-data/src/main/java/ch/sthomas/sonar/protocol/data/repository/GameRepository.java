package ch.sthomas.sonar.protocol.data.repository

sonar.protocol.data.protocol.repository;

import ch.sthomas.sonar.protocol.data.entity.GameEntity;
import ch.sthomas.sonar.protocol.data.protocol.entity.ProtocolFrameEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, Long> {
}
