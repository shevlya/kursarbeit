package ru.ssau.srestapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import ru.ssau.srestapp.security.JwtFilter;
import ru.ssau.srestapp.service.CustomUserDetailsService;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String ORGANIZER = "ORGANIZER";

    private final JwtFilter jwtFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            PasswordEncoder passwordEncoder,
            CustomUserDetailsService customUserDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(this::configureAuthorization)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void configureAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        configurePublicEndpoints(auth);
        configureAuthenticatedEndpoints(auth);
        configureOrganizerEndpoints(auth);
        configureAdminEndpoints(auth);
        auth.anyRequest().authenticated();
    }

    private void configurePublicEndpoints(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers("/auth/**").permitAll()
                .requestMatchers("/users/register").permitAll()
                .requestMatchers(GET, "/api/categories/**").permitAll()
                .requestMatchers(GET, "/api/places/**").permitAll()
                .requestMatchers(GET, "/api/online-places/**").permitAll()
                .requestMatchers(GET, "/api/physical-places/**").permitAll()
                .requestMatchers(GET, "/api/avatars/**").permitAll()
                .requestMatchers(GET, "/api/events/**").permitAll()
                .requestMatchers(GET, "/api/roles/**").permitAll();
    }

    private void configureAuthenticatedEndpoints(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(GET, "/api/users/me").authenticated()
                .requestMatchers(PUT, "/api/users/me").authenticated()
                .requestMatchers(PATCH, "/api/users/me/**").authenticated()
                .requestMatchers(POST, "/api/organizer-requests/me").authenticated()
                .requestMatchers(GET, "/api/organizer-requests/me").authenticated()
                .requestMatchers("/api/users/me/interests/**").authenticated()
                .requestMatchers("/auth/me").authenticated();
    }

    private void configureOrganizerEndpoints(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(POST, "/api/online-places").hasAnyRole(ADMIN, ORGANIZER)
                .requestMatchers(POST, "/api/physical-places").hasAnyRole(ADMIN, ORGANIZER)
                .requestMatchers(POST, "/api/events").hasAnyRole(ADMIN, ORGANIZER)
                .requestMatchers(PUT, "/api/events/{id}/submit").hasAnyRole(ADMIN, ORGANIZER)
                .requestMatchers(DELETE, "/api/events/**").hasAnyRole(ADMIN, ORGANIZER);
    }

    private void configureAdminEndpoints(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers("/api/categories/**").hasRole(ADMIN)
                .requestMatchers("/api/places/**").hasRole(ADMIN)
                .requestMatchers("/api/online-places/**").hasRole(ADMIN)
                .requestMatchers("/api/physical-places/**").hasRole(ADMIN)
                .requestMatchers("/api/avatars/**").hasRole(ADMIN)
                .requestMatchers(PUT, "/api/events/**").denyAll()
                .requestMatchers(PATCH, "/api/events/{id}/verify").hasRole(ADMIN)
                .requestMatchers(POST, "/api/events/update-statuses").hasRole(ADMIN)
                .requestMatchers("/api/events/admin/**").hasRole(ADMIN)
                .requestMatchers(GET, "/api/users/{id}").hasRole(ADMIN)
                .requestMatchers(PUT, "/api/users/{id}").hasRole(ADMIN)
                .requestMatchers(DELETE, "/api/users/{id}").hasRole(ADMIN)
                .requestMatchers(PATCH, "/api/users/{id}/status").hasRole(ADMIN)
                .requestMatchers(PATCH, "/api/users/{id}/role").hasRole(ADMIN)
                .requestMatchers(GET, "/api/users").hasRole(ADMIN)
                .requestMatchers("/api/organizer-requests/**").hasRole(ADMIN)
                .requestMatchers(GET, "/api/users/me/interests/user/{userId}").hasRole(ADMIN)
                .requestMatchers("/api/roles/**").hasRole(ADMIN)
                .requestMatchers("/api/admin/**").hasRole(ADMIN);
    }
}
