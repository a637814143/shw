package com.example.housekeeping.config;

import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.housekeeping.security.PasswordHasher;
import com.example.housekeeping.security.Sha256PasswordHasher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .timeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
    }

    @Bean
    public PasswordHasher passwordHasher() {
        return new Sha256PasswordHasher();
    }
}
