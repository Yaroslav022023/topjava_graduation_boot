package com.topjava.graduation.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;

@Configuration
@EnableCaching
@EnableScheduling
@Slf4j
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new CaffeineCacheManager();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void clearCacheDaily() {
        CacheManager cacheManager = cacheManager();
        cacheManager.getCacheNames().forEach(cacheName -> {
            log.info("Clearing cache: " + cacheName);
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        });
    }
}