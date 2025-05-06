package ch.sthomas.sonar.protocol.ws.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

@Configuration
@ComponentScan(basePackages = "ch.sthomas.sonar.protocol")
public class WsBaseConfig {

    static {
        StaticSpringDocConfig.setDefaultConfig();
    }

    @Bean
    RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new ProblemModule())
                .registerModule(new ConstraintViolationProblemModule());
    }
}
