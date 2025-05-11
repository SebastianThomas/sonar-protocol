package ch.sthomas.sonar.protocol.model.action;

import ch.sthomas.sonar.protocol.model.Team;

import java.time.Instant;

public record RequestSonarInformation(Team.ID team, Instant time, Action action) {
    public RequestSonarInformation(final Team.ID team) {
        this(team, Instant.now(), Action.SONAR);
    }
}
