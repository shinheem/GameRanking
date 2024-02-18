package org.example.novicesranking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 프론트에 api 보내기 위한 주소설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://ranking.codenovices.store")
                .allowedOrigins("https://ranking-backend.codenovice.store")
                .allowedMethods("GET","POST","PUT","DELETE")
                .allowedHeaders("*");

    }
}
