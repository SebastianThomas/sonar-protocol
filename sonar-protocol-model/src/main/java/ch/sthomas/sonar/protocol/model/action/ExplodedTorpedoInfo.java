package ch.sthomas.sonar.protocol.model.action;

import ch.sthomas.sonar.protocol.model.game.GameOverInfo;

public record ExplodedTorpedoInfo(Torpedo torpedo, GameOverInfo gameOverInfo) {}
