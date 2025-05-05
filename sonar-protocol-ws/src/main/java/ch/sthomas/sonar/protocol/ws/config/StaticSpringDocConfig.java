package ch.sthomas.sonar.protocol.ws.config;

import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.StringSchema;

import org.springdoc.core.utils.SpringDocUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class StaticSpringDocConfig {
    private StaticSpringDocConfig() {}

    @SuppressWarnings("unused")
    public static void setDefaultConfig() {
        setDefaultConfigWith(customizer -> {});
    }

    public static void setDefaultConfigWith(final Consumer<SpringDocUtils> customizer) {
        final var nowEuZh = ZonedDateTime.now();
        final var nowEuZhMillis = BigDecimal.valueOf(nowEuZh.toInstant().toEpochMilli());
        final var nowEuZhLocalTimeFormatted = nowEuZh.format(DateTimeFormatter.ISO_LOCAL_TIME);
        final var durationToday =
                BigDecimal.valueOf(
                        Duration.between(nowEuZh.truncatedTo(ChronoUnit.DAYS), nowEuZh).toMillis());

        final var config =
                SpringDocUtils.getConfig()
                        // Instant as millis
                        .replaceWithSchema(
                                Instant.class, new NumberSchema()._default(nowEuZhMillis))
                        // ZonedDateTime as millis
                        .replaceWithSchema(
                                ZonedDateTime.class, new NumberSchema()._default(nowEuZhMillis))
                        // LocalTime as ISO HH:mm:ss and optionally nanos
                        .replaceWithSchema(
                                LocalTime.class,
                                new StringSchema()._default(nowEuZhLocalTimeFormatted))
                        // Duration as millis
                        .replaceWithSchema(
                                Duration.class, new NumberSchema()._default(durationToday));

        customizer.accept(config);
    }
}
