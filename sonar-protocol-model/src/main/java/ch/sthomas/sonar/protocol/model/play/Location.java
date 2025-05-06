package ch.sthomas.sonar.protocol.model.play;

import ch.sthomas.sonar.protocol.model.util.VectorUtils;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record Location(
        @Min(VectorUtils.MIN_X) @Max(VectorUtils.MAX_X) int x,
        @Min(VectorUtils.MIN_Y) @Max(VectorUtils.MAX_Y) int y)
        implements XYAccessor {
    public record DirectionVector(int x, int y) implements XYAccessor {}
}
