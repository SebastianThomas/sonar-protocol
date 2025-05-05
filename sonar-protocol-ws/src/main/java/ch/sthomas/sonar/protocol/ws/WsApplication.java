package ch.sthomas.sonar.protocol.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.zalando.problem.spring.web.autoconfigure.security.ProblemSecurityAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = {"ch.sthomas.sonar.protocol.ws"},
        exclude = {ErrorMvcAutoConfiguration.class, ProblemSecurityAutoConfiguration.class})
@ConfigurationPropertiesScan("ch.sthomas.sonar.protocol")
public class WsApplication {

    public static void main(final String[] args) {
        SpringApplication.run(WsApplication.class, args);
    }
}
