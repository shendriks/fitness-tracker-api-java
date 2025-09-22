package dev.shendriks.fitnesstrackerapi.infrastructure.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtAuthenticationProvider);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
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
            "/api/activities/**",
            "/api/challenges/**",
            "/api/challenge-participations",
            "/api/milestones/**",
            "/api/trophies/**",
            "/api/notifications/**",
            "/api/users/me",
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
            "/error",
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
            "/api/access-token",
        };
        http
            .securityMatcher(approvalsPaths)
            .httpBasic(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authenticationManager(authenticationManager())
            .authorizeHttpRequests(matcherRegistry -> matcherRegistry
                .requestMatchers(HttpMethod.GET, "/api/access-token").hasRole("USER")
                .anyRequest().denyAll()
            )
            .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    @Order(4)
    @Profile("demo")
    public SecurityFilterChain swaggerUiSecuredFilterChain(HttpSecurity http) throws Exception {
        String[] approvalsPaths = {
            "/login",
            "/default-ui.css",
            "/api-docs",
            "/api-docs/*",
            "/swagger-ui",
            "/swagger-ui/*",
        };
        http
            .securityMatcher(approvalsPaths)
            .csrf(AbstractHttpConfigurer::disable)
            .authenticationManager(authenticationManager())
            .authorizeHttpRequests(matcherRegistry -> matcherRegistry
                .requestMatchers(HttpMethod.GET, "/api-docs").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api-docs/*").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/swagger-ui").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/swagger-ui/*").hasRole("USER")
                .anyRequest().denyAll()
            )
            .formLogin(formLogin -> formLogin.defaultSuccessUrl("/swagger-ui/index.html"));
        return http.build();
    }

    @Bean
    @Order(4)
    @Profile("!demo")
    public SecurityFilterChain swaggerUiUnsecuredFilterChain(HttpSecurity http) throws Exception {
        String[] approvalPaths = {
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
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().denyAll()
            );
        return http.build();
    }
}


