package ch.sthomas.sonar.protocol.model.action;

import ch.sthomas.sonar.protocol.model.game.GameOverInfo;

public record ExplodedMineInfo(Mine mine, GameOverInfo gameOverInfo) {}
