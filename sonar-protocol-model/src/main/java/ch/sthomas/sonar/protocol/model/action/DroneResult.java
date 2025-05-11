package ch.sthomas.sonar.protocol.model.action;

import ch.sthomas.sonar.protocol.model.Team;
import ch.sthomas.sonar.protocol.model.play.Sector;

/**
 * @param sector
 * @param teamSent the team that sent the drone
 * @param hit whether the other team's ship is in this sector
 */
public record DroneResult(Sector sector, Team.ID teamSent, boolean hit) {}
