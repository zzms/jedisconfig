package com.css.platform.common.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by leosoft on 2018/10/12.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.redis")
@Component
public class RedisProperties {

    private int    expireSeconds;
    private String clusterNodes;
    private String password;
    private int    commandTimeout;
    private boolean cluster;
    private String host;
}
