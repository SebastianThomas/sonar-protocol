package ch.sthomas.sonar.protocol.ws.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class WsSchedulingConfig {

    private static final Logger logger = LoggerFactory.getLogger(WsSchedulingConfig.class);

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Berlin")
    public void test() {}
}
