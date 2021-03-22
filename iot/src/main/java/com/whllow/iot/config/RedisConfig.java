package com.whllow.iot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){

        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        //序列化redis String类型key的值，防止乱码
        template.setKeySerializer(RedisSerializer.string());
        //序列化redis String类型value的值，防止乱码
        template.setValueSerializer(RedisSerializer.json());
        //序列化redis hash类型key的值，防止乱码
        template.setHashKeySerializer(RedisSerializer.string());
        //序列化redis hash类型value的值，防止乱码
        template.setHashKeySerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }


}
