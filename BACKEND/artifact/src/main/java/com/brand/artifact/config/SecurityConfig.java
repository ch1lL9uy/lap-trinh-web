package com.brand.artifact.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.brand.artifact.config.oauth2.CustomOAuth2UserService;
import com.brand.artifact.config.oauth2.OAuth2FailureHandler;
import com.brand.artifact.config.oauth2.OAuth2SuccessHandler;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;
    
    @Autowired
    private OAuth2SuccessHandler oAuth2LoginSuccessHandler;
    
    @Autowired
    private OAuth2FailureHandler oAuth2LoginFailureHandler;

    /**
     * Repository để lưu OAuth2 authorization requests trong HTTP Session
     * QUAN TRỌNG: OAuth2 BẮT BUỘC cần session để lưu state
     */
    @Bean
    public HttpSessionOAuth2AuthorizationRequestRepository authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                
                // ===== AUTHORIZATION RULES =====
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/static", "/templates", "/META-INF").denyAll()
                        
                        // Root and static resources - Allow without authentication
                        .requestMatchers("/", "/favicon.ico", "/error").permitAll()
                        
                        // Public endpoints - Không cần authentication
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/test/public").permitAll()
                        .requestMatchers("/api/home").permitAll()
                        
                        // OAuth2 endpoints - CHO PHÉP tất cả để Spring OAuth2 xử lý
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/login/oauth2/**").permitAll()

                        // Authenticated endpoints
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/users/{userId}/info").authenticated()
                        .requestMatchers("/api/users/{userId}").authenticated()
                        
                        // Role-based endpoints
                        .requestMatchers("/api/cart/**").hasRole("USER")
                        .requestMatchers("/api/orders/**").hasRole("USER")
                        .requestMatchers("/api/profile/**").hasRole("USER")
                        .requestMatchers("/api/reviews/**").hasRole("USER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/staff/**").hasAnyRole("STAFF", "ADMIN")
                        
                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                // Temporarily disable OAuth2 login to test registration
                .oauth2Login(oauth -> oauth
                    .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                    .successHandler(oAuth2LoginSuccessHandler)
                    .failureHandler(oAuth2LoginFailureHandler)
                )
                // .oauth2Login(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        
        // Cho phép credentials (cookies, authorization headers)
        cfg.setAllowCredentials(true);
        
        // Sử dụng allowedOriginPatterns để hỗ trợ credentials
        // Không thể dùng "*" với allowCredentials(true)
        cfg.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*"
        ));
        
        // Cách khác: Liệt kê cụ thể
        // cfg.setAllowedOrigins(Arrays.asList(
        //         "http://localhost:3000",
        //         "http://localhost:5173",
        //         "http://localhost:8080"
        // ));
        
        cfg.setAllowedMethods(Collections.singletonList("*"));
        cfg.setAllowedHeaders(Collections.singletonList("*"));
        
        // Expose headers để frontend đọc được
        cfg.setExposedHeaders(Arrays.asList(
                "Authorization", 
                "Set-Cookie",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));
        
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
