package ch.sthomas.sonar.protocol.model.play;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record DefectLocation(@Min(0) @Max(2) int x, @Min(0) @Max(2) int y) implements XYAccessor {}
