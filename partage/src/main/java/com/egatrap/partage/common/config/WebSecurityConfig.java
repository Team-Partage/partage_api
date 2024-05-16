package com.egatrap.partage.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {

    /**
     * 인증이 필요 없는 경로
     */
    private final String[] EXCLUDE_PATHS = {
            "/api/health",
            "/api/encrypt",
    };

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

//                폼 로그인 비활성화
                .and()
                .formLogin().disable()
                .headers().frameOptions().disable()
        ;

        return http.build();
    }


}
