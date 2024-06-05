package com.egatrap.partage.common.config;

import com.egatrap.partage.security.JwtSecurityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {

    @Value("${security.cors.allowed-origins}")
    private String ALLOWED_ORIGINS;

    /**
     * 인증이 필요 없는 경로
     */
    private final String[] EXCLUDE_PATHS = {
            "/api/health",
            "/api/encrypt",
            "/api/v1/auth/login",
            "/api/v1/user/check-email",
            "/api/v1/user/check-nickname",
            "/api/v1/user/auth-email",
            "/api/v1/user/auth-number",
            "/api/v1/user/join",
            "/api/v1/channel/search",
            "/ws",
            "/stomp/**",
            "/channel/**",
            "/test/**",
            "/api/test/**",
    };

    private final JwtSecurityConfig jwtSecurityConfig;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                csrf 비활성화
                .csrf().disable()
                .cors()
                .and()
//                세션 비활성화
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

//                접근 권한 설정
                .and()
                .authorizeRequests()
                .antMatchers(EXCLUDE_PATHS).permitAll()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/").denyAll()
                .anyRequest().permitAll()

//                폼 로그인 비활성화
                .and()
                .formLogin().disable()
                .headers().frameOptions().disable()

                .and()
                .apply(jwtSecurityConfig)
        ;

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(ALLOWED_ORIGINS));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
