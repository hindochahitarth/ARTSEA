package com.art.artsea.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL path "/uploads/**" to the actual folder location
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/Users/minha/IdeaProjects/ARTSEA - Copy/uploads/");
    }
}
