package com.kmicro.user.security;

import com.kmicro.user.security.filter.CsrfCookieFilter;
import com.kmicro.user.security.filter.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class ProjectSecurityConfig {

    private final UserDetailServiceMs customUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    // --- Authentication Manager and Providers ---

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- Security Filter Chain Configuration ---

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

        http
//                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless REST APIs
                .csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
//                        .ignoringRequestMatchers( "/contact","/register", "/apiLogin")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints for authentication and registration
                        .requestMatchers("/api/auth/**", "/api/products/public").permitAll()
                        // Require ADMIN role for specific endpoints
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        // Crucial: Set session creation policy to stateless for JWT
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Add the custom JWT filter before Spring's default filter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*@Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        CsrfTokenRequestAttributeHandler requestAttributeHandler = new CsrfTokenRequestAttributeHandler();
//        requestAttributeHandler.setCsrfRequestAttributeName("_csrf");

        httpSecurity.csrf((csrf)->csrf.disable()
//                        .csrfTokenRequestHandler(requestAttributeHandler)
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
//                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
//                .headers(Customizer.withDefaults())
//                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests((reqs)-> reqs
//                                .anyRequest().permitAll()

                        .requestMatchers("/api/users/address").authenticated()
                                .requestMatchers("/api/users/","/api/users/create/").permitAll()
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return  httpSecurity.build();
    }*/

/*    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }*/

}//EC
