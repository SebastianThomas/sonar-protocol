package ch.sthomas.sonar.protocol.ws.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("ch.sthomas.sonar.protocol")
@EntityScan("ch.sthomas.sonar.protocol.data")
public class WsDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.ws")
    public DataSourceProperties wsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.ws.hikari")
    public DataSource wsDataSource(
            @Qualifier("wsDataSourceProperties")
                    final DataSourceProperties wsDataSourceProperties) {
        return wsDataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    @ConfigurationProperties("spring.flyway.ws")
    public FlywayProperties wsFlywayProperties() {
        return new FlywayProperties();
    }

    @Bean
    Flyway wsFlyway(
            @Qualifier("wsDataSource") final DataSource wsDataSource,
            @Qualifier("wsFlywayProperties") final FlywayProperties wsFlywayProperties) {
        final var fluentConfig = Flyway.configure().dataSource(wsDataSource);
        final var flyway = fluentConfig.load();
        final var map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        final var locations = wsFlywayProperties.getLocations().toArray(String[]::new);
        map.from(wsFlywayProperties.isFailOnMissingLocations())
                .to(fluentConfig::failOnMissingLocations);
        map.from(locations).to(fluentConfig::locations);
        flyway.migrate();
        return flyway;
    }
}
