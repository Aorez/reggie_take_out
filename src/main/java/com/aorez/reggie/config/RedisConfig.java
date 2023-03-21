package com.aorez.reggie.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//之前在想redis使用内存进行数据存储，那为什么不直接在代码中实现，存储在jvm运行内存中呢？
//直到看到短信验证码保存在redis中的那个视频的一条弹幕，说多服务器的适合session无法共享
//而jvm运行内存的数据肯定不是对外共享的，而只要有一个redis，就可以有多个数据库连接，进而解决了访问效率加数据共享的问题

//使用redis后，回顾了一下变量共享，BaseContext，HttpSession
//BaseContext是为了解决MyMetaObjectHandler中获取不到session的问题
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
