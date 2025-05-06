package ch.sthomas.sonar.protocol.data.service;

import ch.sthomas.sonar.protocol.data.entity.PathEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface PathEntityRepository extends JpaRepository<PathEntity, Long> {}
