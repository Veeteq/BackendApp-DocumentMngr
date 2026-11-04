package com.veeteq.documentmngr.security;

import com.veeteq.documentmngr.config.CorsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsProperties corsProperties;

    public SecurityConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityProblemSupport problemSupport) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Document write operations
                        .requestMatchers(HttpMethod.POST, "/api/v1/documents").hasAuthority("DOCUMENT_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/documents/**").hasAuthority("DOCUMENT_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/documents/**").hasAuthority("DOCUMENT_ADMIN")

                        // Account write operations
                        .requestMatchers(HttpMethod.POST, "/api/v1/accounts").hasAuthority("ACCOUNT_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/accounts/**").hasAuthority("ACCOUNT_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/accounts/**").hasAuthority("ACCOUNT_ADMIN")

                        // Item write operations
                        .requestMatchers(HttpMethod.POST, "/api/v1/items").hasAuthority("ITEM_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/items/**").hasAuthority("ITEM_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/items/**").hasAuthority("ITEM_ADMIN")

                        // All other endpoints require only valid JWT
                        .anyRequest().authenticated())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(problemSupport)
                        .accessDeniedHandler(problemSupport)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                                .authenticationEntryPoint(problemSupport)
                                .accessDeniedHandler(problemSupport));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new JwtRolesConverter());
        return converter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();

        config.setAllowedOrigins(corsProperties.allowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Location"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
