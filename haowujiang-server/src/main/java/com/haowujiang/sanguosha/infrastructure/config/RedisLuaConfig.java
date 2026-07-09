package com.haowujiang.sanguosha.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * redis导入lua脚本配置类
 */
@Configuration
public class RedisLuaConfig {

    @Bean("seckillScript")
    public DefaultRedisScript<Long> seckillScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/seckill.lua"));
        script.setResultType(Long.class);
        return script;
    }

    @Bean("compensateScript")
    DefaultRedisScript<Long> compensateScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/compensate.lua"));
        script.setResultType(Long.class);
        return script;
    }
}
