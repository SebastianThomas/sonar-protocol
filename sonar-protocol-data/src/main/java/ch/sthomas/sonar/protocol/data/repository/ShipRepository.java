package ch.sthomas.sonar.protocol.data.repository;

import ch.sthomas.sonar.protocol.data.entity.ShipEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShipRepository extends JpaRepository<ShipEntity, Long> {
    @Query("UPDATE ShipEntity s SET s.health = s.health + :damage WHERE s.id = :shipId")
    @Modifying
    void addDamage(long shipId, int damage);

    @Query(
            "SELECT s FROM ShipEntity s WHERE s.team.game = :gameId AND s.team.id = s.team.game.a.id")
    ShipEntity findByGameIdAndTeamA(long gameId);

    @Query(
            "SELECT s FROM ShipEntity s WHERE s.team.game = :gameId AND s.team.id = s.team.game.b.id")
    ShipEntity findByGameIdAndTeamB(long gameId);
}
