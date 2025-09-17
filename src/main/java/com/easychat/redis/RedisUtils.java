package com.easychat.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component("redisUtils")
public class RedisUtils<V> {
    @Resource
    private RedisTemplate<String, V> redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    // ============================= 通用操作 =============================
    /**
     * 删除缓存（支持单个或多个key）
     * @param keys 要删除的key数组
     */
    public void delete(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                redisTemplate.delete(keys[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(keys));
            }
        }
    }

    /**
     * 设置过期时间
     * @param key  键
     * @param time 时间(秒)
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置过期时间失败 key: {}", key, e);
            return false;
        }
    }

    // ============================= String操作 =============================
    /**
     * 获取缓存值
     * @param key 键
     * @return 值
     */
    public V get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key   键
     * @param value 值
     */
    public boolean set(String key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("设置缓存失败 key: {}, value: {}", key, value, e);
            return false;
        }
    }

    /**
     * 缓存放入并设置过期时间
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public boolean setex(String key, V value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置缓存失败 key: {}, value: {}, time: {}", key, value, time, e);
            return false;
        }
    }

    // ============================= List操作 =============================
    /**
     * 获取整个列表
     * @param key 键
     * @return 列表数据
     */
    public List<V> getQueueList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 左推单个元素到列表
     * @param key   键
     * @param value 值
     * @param time  过期时间(秒)
     */
    public boolean lpush(String key, V value, long time) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            logger.error("LPUSH操作失败 key: {}, value: {}", key, value, e);
            return false;
        }
    }

    /**
     * 左推多个元素到列表
     * @param key    键
     * @param values 值列表
     * @param time   过期时间(秒)
     */
    public boolean lpushAll(String key, List<V> values, long time) {
        try {
            redisTemplate.opsForList().leftPushAll(key, values);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            logger.error("LPUSH批量操作失败 key: {}, values: {}", key, values, e);
            return false;
        }
    }

    /**
     * 从列表中删除元素
     * @param key   键
     * @param value 要删除的值
     * @return 删除的数量
     */
    public long remove(String key, Object value) {
        try {
            Long count = redisTemplate.opsForList().remove(key, 1, value);
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("删除列表元素失败 key: {}, value: {}", key, value, e);
            return 0;
        }
    }
}
