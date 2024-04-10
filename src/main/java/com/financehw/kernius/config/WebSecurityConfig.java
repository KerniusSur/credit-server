package com.financehw.kernius.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Profile("!test")
@Configuration
public class WebSecurityConfig {
  public static final String[] AUTH_WHITELIST = {
    "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api/v1/auth/**"
  };

  public static final String[] USER_URL_LIST = {"/api/v1/**"};

  private final AuthEntryPointJwt unauthorizedHandler;
  private final JwtAuthFilter jwtAuthFilter;

  public WebSecurityConfig(AuthEntryPointJwt unauthorizedHandler, JwtAuthFilter jwtAuthFilter) {
    this.unauthorizedHandler = unauthorizedHandler;
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(
            (exceptionHandling) -> exceptionHandling.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(
            (sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            (authorizeHttpRequests) ->
                authorizeHttpRequests.requestMatchers(AUTH_WHITELIST).permitAll())
        .authorizeHttpRequests(
            (authorizeHttpRequests) ->
                authorizeHttpRequests.requestMatchers(USER_URL_LIST).authenticated())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
