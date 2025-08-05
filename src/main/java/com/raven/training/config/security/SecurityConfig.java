package com.raven.training.config.security;

import com.raven.training.config.filter.JwtTokenValidator;
import com.raven.training.service.implementation.UserDetailServiceImpl;
import com.raven.training.util.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Spring Security configuration class for the application.
 * This class defines the security filters, authentication providers, and
 * authorization rules to secure the REST API endpoints.
 * It is responsible for setting up stateless session management,
 * configuring a custom JWT token validator, and defining public and
 * protected API paths.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private JwtUtils jwtUtils;

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * This method sets up the following security rules:
     * <ul>
     * <li>Disables CSRF protection.</li>
     * <li>Configures stateless session management.</li>
     * <li>Allows public access to Swagger UI and authentication endpoints.</li>
     * <li>Requires authentication for all other requests.</li>
     * <li>Adds a custom {@link JwtTokenValidator} filter before the
     * {@link BasicAuthenticationFilter} to validate JWT tokens.</li>
     * </ul>
     *
     * @param httpSecurity The {@link HttpSecurity} object to configure.
     * @return The configured {@link SecurityFilterChain}.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {
                    // Swagger UI
                    http.requestMatchers("/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/swagger-resources/**",
                            "/webjars/**").permitAll();

                    //request public loggin
                    http.requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll();

                    http.anyRequest().authenticated();
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .exceptionHandling(Customizer.withDefaults());
        return httpSecurity.build();
    }

    /**
     * Provides the {@link AuthenticationManager} bean, which is responsible for
     * authenticating users.
     *
     * @param authenticationConfiguration The {@link AuthenticationConfiguration} used to build the manager.
     * @return The configured {@link AuthenticationManager}.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Provides the {@link AuthenticationProvider} bean, which uses a custom
     * {@link UserDetailServiceImpl} to retrieve user details and a
     * {@link PasswordEncoder} for password validation.
     *
     * @param userDetailService The custom user details service.
     * @return The configured {@link DaoAuthenticationProvider}.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Provides a {@link PasswordEncoder} bean for encoding and verifying passwords.
     * It uses the BCrypt algorithm.
     *
     * @return A {@link BCryptPasswordEncoder} instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
