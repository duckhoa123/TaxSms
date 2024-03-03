package com.vnnet.kpi.console.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")    // Paths that allow cross-domain access
                .allowedOrigins("*")    // Sources that allow cross-domain access
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")    // Allow request method
                .maxAge(168000)    // Pre-check interval
                .allowedHeaders("*")  // Allow head settings
                .allowCredentials(true);    // Whether to send cookies
    }

}
