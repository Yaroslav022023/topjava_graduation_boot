package com.topjava.graduation.config;

import com.topjava.graduation.util.DateTimeFormatters;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new DateTimeFormatters.LocalDateTimeFormatter());
    }
}