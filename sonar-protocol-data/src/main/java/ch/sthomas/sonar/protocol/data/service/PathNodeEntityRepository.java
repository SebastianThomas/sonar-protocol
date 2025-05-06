package ch.sthomas.sonar.protocol.data.service;

import ch.sthomas.sonar.protocol.data.entity.PathNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface PathNodeEntityRepository extends JpaRepository<PathNodeEntity, Long> {}
