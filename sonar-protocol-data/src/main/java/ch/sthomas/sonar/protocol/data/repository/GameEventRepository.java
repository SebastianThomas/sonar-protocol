package ch.sthomas.sonar.protocol.data.repository;

import ch.sthomas.sonar.protocol.data.entity.GameEventEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collection;

public interface GameEventRepository extends JpaRepository<GameEventEntity, Integer> {
    Collection<GameEventEntity> findByPlayer_IdAndTimeAfter(Long playerId, Instant timeAfter);
}
