package ch.sthomas.sonar.protocol.data.service;

import ch.sthomas.sonar.protocol.data.entity.TeamEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {}
