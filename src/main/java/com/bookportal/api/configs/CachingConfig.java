package com.bookportal.api.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableCaching
@EnableScheduling
public class CachingConfig {
    private static final Logger log = LoggerFactory.getLogger(CachingConfig.class);
    private static final long ONE_HOUR = 3600000L;
    private static final long FOUR_HOUR = 14400000L;
    private static final long TWELVE_DAY = 43200000L;

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("homePage");
    }

    @Scheduled(fixedRate = FOUR_HOUR)
    @CacheEvict(value = {"homePage"})
    public void clearCache() {
        log.info("Cache {homePage} deleted.");
    }
}
