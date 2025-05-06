package ch.sthomas.sonar.protocol.data.repository;

import ch.sthomas.sonar.protocol.data.entity.PlayerEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {
    @Modifying
    @Query("UPDATE PlayerEntity p SET p.wsSessionId = :wsSessionId WHERE p.id = :playerId")
    int updatePlayerWsSessionId(long playerId, String wsSessionId);
}
