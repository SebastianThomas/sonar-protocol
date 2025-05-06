package ch.sthomas.sonar.protocol.data.repository;

import ch.sthomas.sonar.protocol.data.entity.GameEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, Long> {
    @Query(
            "SELECT g FROM GameEntity g INNER JOIN TeamEntity t ON g.a = t OR g.b = t INNER JOIN PlayerEntity p ON t.id = p.teamId")
    Optional<GameEntity> findByPlayer(long playerId);
}
