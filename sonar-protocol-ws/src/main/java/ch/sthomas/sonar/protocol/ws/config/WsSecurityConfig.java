package ch.sthomas.sonar.protocol.ws.config;

import static org.springframework.security.config.Customizer.withDefaults;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Profile("!no-security")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WsSecurityConfig {

    private static final String SWAGGER = "SWAGGER";

    @Bean
    SecurityFilterChain basicFilterChain(final HttpSecurity http) throws Exception {
        http.securityMatcher("/api-docs/**", "/api-docs*")
                .authorizeHttpRequests(
                        customizer ->
                                customizer
                                        .requestMatchers("/api-docs/**", "/api-docs*")
                                        .hasRole(SWAGGER))
                .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    SecurityFilterChain bearerFilterChain(final HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(
                customizer ->
                        customizer
                                .dispatcherTypeMatchers(DispatcherType.ERROR)
                                .authenticated()
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/",
                                        "/check",
                                        "/actuator/prometheus",
                                        "/actuator/health/**")
                                .permitAll()
                                .requestMatchers("/v1/**")
                                .permitAll()
                                .anyRequest() // deny all others
                                .denyAll());

        return http.build();
    }

    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        final var user =
                User.builder()
                        .username("sonar-protocol-swagger")
                        .password("$2a$12$AegKAMzNiPdztNgU9CVZh.zr3RyDNht.zYPPgJN6MHb4YwaXTVVEy")
                        .roles(SWAGGER)
                        .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearer-key",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")));
    }
}
