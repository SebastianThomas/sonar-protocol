package ch.sthomas.sonar.protocol.model.action;

import static ch.sthomas.sonar.protocol.model.action.ActionCategory.*;
import static ch.sthomas.sonar.protocol.model.play.Direction.*;

import ch.sthomas.sonar.protocol.model.play.DefectLocation;
import ch.sthomas.sonar.protocol.model.play.Direction;

import com.google.common.collect.MoreCollectors;

import java.util.List;
import java.util.Set;

public record DefectCircuit(Set<Defect> defects) {
    public static List<Defect> EXISTING_DEFECTS =
            List.of(
                    new Defect(new DefectLocation(0, 0), ATTACK, WEST, false),
                    new Defect(new DefectLocation(1, 0), SPECIAL, WEST, false),
                    new Defect(new DefectLocation(1, 1), INTELLIGENCE, WEST, false),
                    new Defect(new DefectLocation(0, 2), INTELLIGENCE, WEST, false),
                    new Defect(new DefectLocation(1, 2), ATOM, WEST, false),
                    new Defect(new DefectLocation(2, 2), ATOM, WEST, false),
                    new Defect(new DefectLocation(0, 0), SPECIAL, NORTH, false),
                    new Defect(new DefectLocation(0, 1), ATTACK, NORTH, false),
                    new Defect(new DefectLocation(1, 1), SPECIAL, NORTH, false),
                    new Defect(new DefectLocation(0, 2), INTELLIGENCE, NORTH, false),
                    new Defect(new DefectLocation(1, 2), ATTACK, NORTH, false),
                    new Defect(new DefectLocation(2, 2), ATOM, NORTH, false),
                    new Defect(new DefectLocation(0, 0), INTELLIGENCE, SOUTH, false),
                    new Defect(new DefectLocation(0, 1), SPECIAL, SOUTH, false),
                    new Defect(new DefectLocation(1, 1), ATTACK, SOUTH, false),
                    new Defect(new DefectLocation(0, 2), ATTACK, SOUTH, false),
                    new Defect(new DefectLocation(1, 2), ATOM, SOUTH, false),
                    new Defect(new DefectLocation(2, 2), SPECIAL, SOUTH, false),
                    new Defect(new DefectLocation(0, 0), INTELLIGENCE, EAST, false),
                    new Defect(new DefectLocation(0, 1), SPECIAL, EAST, false),
                    new Defect(new DefectLocation(1, 1), ATTACK, EAST, false),
                    new Defect(new DefectLocation(0, 2), ATOM, EAST, false),
                    new Defect(new DefectLocation(1, 2), INTELLIGENCE, EAST, false),
                    new Defect(new DefectLocation(2, 2), ATOM, EAST, false));

    public static List<DefectCircuit> EXISTING_CIRCUITS =
            List.of(
                    new DefectCircuit(
                            Set.of(
                                    getDefect(WEST, new DefectLocation(0, 0)),
                                    getDefect(WEST, new DefectLocation(1, 0)),
                                    getDefect(WEST, new DefectLocation(1, 1)),
                                    getDefect(EAST, new DefectLocation(1, 1)))),
                    new DefectCircuit(
                            Set.of(
                                    getDefect(NORTH, new DefectLocation(1, 1)),
                                    getDefect(NORTH, new DefectLocation(0, 1)),
                                    getDefect(NORTH, new DefectLocation(0, 0)),
                                    getDefect(EAST, new DefectLocation(0, 0)))),
                    new DefectCircuit(
                            Set.of(
                                    getDefect(SOUTH, new DefectLocation(0, 0)),
                                    getDefect(SOUTH, new DefectLocation(0, 1)),
                                    getDefect(SOUTH, new DefectLocation(1, 1)),
                                    getDefect(EAST, new DefectLocation(0, 1)))));

    public static Defect getDefect(final Direction direction, final DefectLocation location) {
        return EXISTING_DEFECTS.stream()
                .filter(d -> d.direction() == direction && d.location().equals(location))
                .collect(MoreCollectors.toOptional())
                .orElseThrow(AssertionError::new);
    }
}
