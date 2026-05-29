package com.nju.partner.config;

import com.nju.partner.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    public void addInterceptors(InterceptorRegistry registry) {
        // paths to exclude from JWT check (login/register/public)
        String[] exclude = new String[]{
                "/api/user/login",
                "/api/user/register",
                "/api/public/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/error"
        };
        registry.addInterceptor((org.springframework.web.servlet.HandlerInterceptor) jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(exclude);
    }
}
