package io.github.wolfandw.chassis.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Авто-конфигурация безопасности.
 */
@AutoConfiguration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityWebFilterConfiguration {
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(spec -> spec
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .oauth2Client(withDefaults())
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        Converter<Jwt, Flux<GrantedAuthority>> jwtGrantedAuthoritiesConverter = jwt ->
                Mono.justOrEmpty(jwt.getClaimAsMap("realm_access"))
                        .flatMapMany(realmAccess -> {
                            Object roles = realmAccess.get("roles");
                            if (roles instanceof Collection<?> rolesList) {
                                return Flux.fromIterable(rolesList)
                                        .filter(String.class::isInstance)
                                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role));
                            }
                            return Flux.empty();
                        });
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }
}
