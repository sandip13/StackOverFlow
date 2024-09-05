package com.stackoverflow.beta.config;

import com.stackoverflow.beta.filter.JwtAuthEntryPoint;
import com.stackoverflow.beta.filter.JwtAuthenticationFilter;
import com.stackoverflow.beta.filter.JwtUtil;
import com.stackoverflow.beta.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
public class SecurityConfig {

    private final JwtAuthEntryPoint unauthorizedHandler;
    private final UserServiceImpl userServiceImpl;
    private final JwtUtil jwtUtil;

    @Autowired
    public SecurityConfig(JwtAuthEntryPoint unauthorizedHandler,
                          UserServiceImpl userServiceImpl,
                          JwtUtil jwtUtil) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.userServiceImpl = userServiceImpl;
        this.jwtUtil = jwtUtil;
    }

    // Updated Security filter chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        // Create the JwtAuthenticationFilter inside this method
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtUtil, userServiceImpl);

        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless API
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/register/**").permitAll() // Public endpoints
                        .requestMatchers("/swagger-ui/**").permitAll() // Allow access to Swagger UI
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN") // Admin can delete
                        .requestMatchers(HttpMethod.POST, "/**").hasAnyRole("ADMIN", "WRITER") // Admin and Writer can create
                        .requestMatchers(HttpMethod.PUT, "/**").hasAnyRole("ADMIN", "WRITER") // Admin and Writer can update
                        .requestMatchers(HttpMethod.GET, "/**").hasAnyRole("ADMIN", "WRITER", "READER") // Everyone can read
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Add filter here
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)); // Handle unauthorized access

        return http.build();
    }

    // AuthenticationManager is injected via AuthenticationConfiguration
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
