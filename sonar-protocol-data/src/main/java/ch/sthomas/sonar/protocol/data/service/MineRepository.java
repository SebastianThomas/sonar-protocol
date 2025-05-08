package ch.sthomas.sonar.protocol.data.service;

import ch.sthomas.sonar.protocol.data.entity.MineEntity;
import ch.sthomas.sonar.protocol.data.entity.id.MineEntityId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MineRepository extends JpaRepository<MineEntity, MineEntityId> {
    @Query(
            "SELECT m FROM MineEntity m INNER JOIN ShipEntity s ON s.id = m.ship.id AND m.id.x = :x AND m.id.y = :y INNER JOIN TeamEntity t ON t.id = s.team.id INNER JOIN GameEntity g ON g.a.id = t.id AND g.id = :gameId")
    Optional<MineEntity> findByTeamA(long gameId, int x, int y);

    @Query(
            "SELECT m FROM MineEntity m INNER JOIN ShipEntity s ON s.id = m.ship.id AND m.id.x = :x AND m.id.y = :y INNER JOIN TeamEntity t ON t.id = s.team.id INNER JOIN GameEntity g ON g.b.id = t.id AND g.id = :gameId")
    Optional<MineEntity> findByTeamB(long gameId, int x, int y);
}
