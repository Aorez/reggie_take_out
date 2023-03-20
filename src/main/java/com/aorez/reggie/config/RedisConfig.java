package com.aorez.reggie.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    //不提供这个类，spring.properties中也会提供
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

        //如果用默认的序列化器，在查看redis数据的适合就会保留许多序列化后的code，不利于观察
        //默认的Key序列化器为：JdkSerializationRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //value不观察也可以
        //redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }
}
