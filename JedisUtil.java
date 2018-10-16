package com.css.platform.common.redis;

import com.css.platform.common.exception.SimpleErrorException;
import com.css.platform.common.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by leosoft on 2018/10/12.
 */

@Component
public class JedisUtil {

    @Autowired
    private RedisProperties redisProperties;

    @Autowired
    private PoolProperties poolProperties;


    private JedisPoolConfig jedisPoolConfig;


    private void initPoolConfig() {
        // Jedis连接池配置
        jedisPoolConfig = new JedisPoolConfig();
        // 最大空闲连接数, 默认8个
        jedisPoolConfig.setMaxIdle(poolProperties.getMaxIdle());
        // 最大连接数, 默认8个
        jedisPoolConfig.setMaxTotal(poolProperties.getMaxActive());
        //最小空闲连接数, 默认0
        jedisPoolConfig.setMinIdle(poolProperties.getMinIdle());
        // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        jedisPoolConfig.setMaxWaitMillis(poolProperties.getMaxWait()); // 设置2秒
        //对拿到的connection进行validateObject校验
        jedisPoolConfig.setTestOnBorrow(true);
    }

    protected JedisCluster bulidJedisCluster() {
        if (!this.redisProperties.isCluster()) {
            return null;
        }
        initPoolConfig();
        String[] serverArray = this.redisProperties.getClusterNodes().split(",");//获取服务器数组(这里要相信自己的输入，所以没有考虑空指针问题)
        Set<HostAndPort> nodes = new HashSet<>();
        if (serverArray.length <= 2) {
            throw new SimpleErrorException("服务器节点数不符合要求");
        }
        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }
        return
                StringUtil.isEmpty(this.redisProperties.getPassword())
                        ? new JedisCluster(nodes, this.redisProperties.getCommandTimeout(), 1000, 1, this.jedisPoolConfig)// 不需要密码连接的创建对象方式
                        : new JedisCluster(nodes, this.redisProperties.getCommandTimeout(), 1000, 1, this.redisProperties.getPassword(), this.jedisPoolConfig);//需要密码连接的创建对象方式
    }


    protected Jedis buildJedis() {
        if (this.redisProperties.isCluster()) {
            return null;
        }
        initPoolConfig();
        String[] split = this.redisProperties.getHost().split(":");
        if (split.length != 2) {
            throw new SimpleErrorException("redis host配置错误");
        }
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, split[0], Integer.parseInt(split[1]), this.redisProperties.getCommandTimeout(), this.redisProperties.getPassword());
        return jedisPool.getResource();
    }

    public void useJedis(Consumer<Jedis> consumer) {
        Jedis jedis = null;
        try {
            jedis = this.buildJedis();
            consumer.accept(jedis);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void useJedisCluster(Consumer<JedisCluster> consumer){
        JedisCluster jedisCluster=null;
        try {
            jedisCluster=this.bulidJedisCluster();
            consumer.accept(jedisCluster);
        } catch (Exception ex) {
            throw ex;
        }
    }
}
