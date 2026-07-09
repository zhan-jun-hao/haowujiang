package com.haowujiang.sanguosha.infrastructure.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haowujiang.sanguosha.infrastructure.security.filter.HeaderAuthenticationFilter;
import com.haowujiang.sanguosha.infrastructure.security.filter.JwtAuthenticationFilter;
import com.haowujiang.sanguosha.infrastructure.security.handler.CommonAccessDeniedHandler;
import com.haowujiang.sanguosha.infrastructure.security.handler.CommonAuthenticationEntryPoint;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@AutoConfigureBefore(UserDetailsServiceAutoConfiguration.class)
@EnableMethodSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CommonSecurityAutoConfiguration {

    /**
     * 公共的未登录处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public CommonAuthenticationEntryPoint commonAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new CommonAuthenticationEntryPoint(objectMapper);
    }

    /**
     * 已登录但没权限处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public CommonAccessDeniedHandler commonAccessDeniedHandler(ObjectMapper objectMapper) {
        return new CommonAccessDeniedHandler(objectMapper);
    }

    /**
     * 跨域配置
     * @return
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 同时允许 localhost 和 127.0.0.1
        config.addAllowedOriginPattern("http://localhost:*");
        config.addAllowedOriginPattern("http://127.0.0.1:*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain commonSecurityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            HeaderAuthenticationFilter headerAuthenticationFilter,
            CommonAuthenticationEntryPoint authenticationEntryPoint,
            CommonAccessDeniedHandler accessDeniedHandler) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/health", "/error", "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/client/generals/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/client/seckill/generals/*/state").permitAll()
                        .requestMatchers("/api/*/admin/**", "/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(headerAuthenticationFilter, AnonymousAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, HeaderAuthenticationFilter.class)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public HeaderAuthenticationFilter headerAuthenticationFilter() {
        return new HeaderAuthenticationFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
