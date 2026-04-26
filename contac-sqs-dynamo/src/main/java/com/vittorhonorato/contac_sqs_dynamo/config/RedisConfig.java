package com.vittorhonorato.contac_sqs_dynamo.config;

import com.vittorhonorato.contac_sqs_dynamo.controller.dto.response.ContactDetailsResponseDTO;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        RedisCacheConfiguration contactDetailsCacheConfig =
                createCacheConfig(ContactDetailsResponseDTO.class);

        RedisCacheConfiguration contactsAllCacheConfig =
                createCacheConfig(ArrayList.class);

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("contacts", contactDetailsCacheConfig);
        cacheConfigurations.put("contactsByEmail", contactDetailsCacheConfig);
        cacheConfigurations.put("contactsAll", contactsAllCacheConfig);

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(contactDetailsCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    private <T> RedisCacheConfiguration createCacheConfig(Class<T> type) {

        JacksonJsonRedisSerializer<T> serializer =
                new JacksonJsonRedisSerializer<>(type);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                );
    }
}