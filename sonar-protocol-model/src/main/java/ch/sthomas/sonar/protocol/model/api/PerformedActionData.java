package ch.sthomas.sonar.protocol.model.api;

import ch.sthomas.sonar.protocol.model.action.Action;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.annotation.Nullable;

public record PerformedActionData<T, R>(
        Action action, T data, @JsonIgnoreProperties @Nullable R dataTeam) {
    public PerformedActionData<R, ?> teamThis() {
        return new PerformedActionData<>(action, dataTeam, null);
    }

    public PerformedActionData<T, ?> teamOther() {
        return new PerformedActionData<>(action, data, null);
    }
}
