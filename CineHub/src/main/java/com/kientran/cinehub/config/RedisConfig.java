package com.kientran.cinehub.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

/**
 * Redis Cache configuration.
 *
 * Cache names và TTL:
 * - premiumPackages   : 10 phút  — hiếm thay đổi (chỉ admin sửa)
 * - activeSubscription: 5 phút   — cần tương đối fresh sau khi user mua
 * - recommendations   : 15 phút  — thuật toán CF tốn tài nguyên nhất
 * - similarMovies     : 30 phút  — ít thay đổi
 * - popularMovies     : 30 phút  — rất ổn định
 * - movieDetail       : 20 phút  — ít thay đổi
 * - movieList         : 10 phút  — có thể thêm/xóa phim
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * ObjectMapper dùng cho Redis serialization.
     * Bật JavaTimeModule để handle LocalDate/LocalDateTime.
     * Bật DefaultTyping để lưu type info → deserialize đúng class.
     */
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return mapper;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper());

        // Cấu hình mặc định: key String, value JSON, TTL 10 phút
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        // TTL riêng cho từng cache
        Map<String, RedisCacheConfiguration> cacheConfigs = Map.of(
                "premiumPackages",    defaultConfig.entryTtl(Duration.ofMinutes(10)),
                "activeSubscription", defaultConfig.entryTtl(Duration.ofMinutes(5)),
                "recommendations",    defaultConfig.entryTtl(Duration.ofMinutes(15)),
                "similarMovies",      defaultConfig.entryTtl(Duration.ofMinutes(30)),
                "popularMovies",      defaultConfig.entryTtl(Duration.ofMinutes(30)),
                "movieDetail",        defaultConfig.entryTtl(Duration.ofMinutes(20)),
                "movieList",          defaultConfig.entryTtl(Duration.ofMinutes(10))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}
