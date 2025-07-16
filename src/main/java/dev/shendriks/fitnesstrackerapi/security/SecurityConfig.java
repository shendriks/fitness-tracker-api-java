package dev.shendriks.fitnesstrackerapi.security;

import dev.shendriks.fitnesstrackerapi.domain.user.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(
        JwtAuthenticationProvider jwtAuthenticationProvider,
        UserDetailsServiceImpl userDetailsService
    ) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtAuthenticationProvider);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(daoAuthenticationProvider());
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiKeySecuredFilterChain(HttpSecurity http) throws Exception {
        String[] approvalPaths = {
//            "/api/activities",
//            "/api/challenges",
//            "/api/achievements",
            "/api/activities/**",
            "/api/challenges/**",
            "/api/achievements/**",
            "/api/users/me"
        };
        
        http
            .securityMatcher(approvalPaths)
            .csrf(AbstractHttpConfigurer::disable)
            .authenticationProvider(jwtAuthenticationProvider)
            .addFilterAfter(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
            )
            .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain unsecuredFilterChain(HttpSecurity http) throws Exception {
        String[] approvalPaths = {
            "/api/ping",
            "/api/users/signup",
            "/h2-console",
            "/actuator/shutdown",
            "/error",
            "/api-docs",
            "/api-docs/*",
            "/swagger-ui",
            "/swagger-ui/*",
        };

        http
            .securityMatcher(approvalPaths)
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(matcherRegistry -> matcherRegistry
                .anyRequest().permitAll()
            )
            .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain basicAuthSecuredFilterChain(HttpSecurity http) throws Exception {
        String[] approvalsPaths = {
            "/api/users/{id:[a-zA-Z0-9]{26}}",
            "/api/access-token"
        };
        http
            .securityMatcher(approvalsPaths)
            .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authenticationManager(authenticationManager())
            .authorizeHttpRequests(matcherRegistry -> matcherRegistry
                .requestMatchers(HttpMethod.GET, "/api/users/{id:[a-zA-Z0-9]{26}}").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/access-token").hasRole("USER")
                .anyRequest().denyAll()
            )
            .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().denyAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
