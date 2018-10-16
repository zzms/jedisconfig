package com.css.platform.common.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by leosoft on 2018/10/12.
 */
@Component
@ConfigurationProperties(prefix = "spring.redis.pool")
@Getter
@Setter
public class PoolProperties {

    /**
     * 最大连接数
     */
    private  int maxActive;
    // 最大空闲连接数
    private  int maxIdle;
    //最小空闲连接数
    private  int minIdle;
    //连接最大等待时间
    private  int maxWait;

}
