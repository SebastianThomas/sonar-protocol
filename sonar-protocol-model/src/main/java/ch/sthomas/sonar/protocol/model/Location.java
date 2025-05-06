package ch.sthomas.sonar.protocol.model;

import jakarta.validation.constraints.PositiveOrZero;

public record Location(@PositiveOrZero int x, @PositiveOrZero int y) {}
