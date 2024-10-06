package com.auth.auth_service.shared.config;

import com.auth.auth_service.shared.constant.RoleEnum;
import com.auth.auth_service.shared.utils.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authRequest ->
                                authRequest.requestMatchers("/api/auth/**").permitAll()
                                    .requestMatchers("/api/news/**").permitAll()
                                    .requestMatchers("/api/admin/**").hasRole(RoleEnum.ADMIN.name())
                                    .requestMatchers("/api/journalist/**").hasRole(RoleEnum.JOURNALIST.name())  // Only journalists can create/edit news
                                    .requestMatchers("/api/publisher/**").hasRole(RoleEnum.PUBLISHER.name())    // Only publishers can review/publish
                                    .requestMatchers("/api/reader/**").hasRole(RoleEnum.READERS.name())         // Readers need to be logged in to subscribe
                                    .anyRequest().authenticated()
                )
                .sessionManagement(sessionManger -> sessionManger.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
