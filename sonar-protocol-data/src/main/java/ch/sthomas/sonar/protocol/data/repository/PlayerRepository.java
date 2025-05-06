package ch.sthomas.sonar.protocol.data.repository;

import ch.sthomas.sonar.protocol.data.entity.PlayerEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {}
