package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    // ✅ EXISTING CODE (keep as it is)
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {

    registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:/app/uploads/");
}

}