package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author：lanjy
 * @date：2020/6/30
 * @description：
 */
@Service
@Slf4j
public class StockService {

    private final String STOCK_LOCK = "STOCK_LOCK";

    /**
     * 注入springboot自动配置好的RedisTemplate
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private Redisson redisson;




    /**
     * 高并发下的下单减库存的问题重现
     * @return
     */
    public String deductStock(){
        //设置redis中的key的序列化器,方便读取
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        stringRedisTemplate.setKeySerializer(stringRedisSerializer);
        Integer stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
        if (stock > 0){
            int realStock = stock -1;
            stringRedisTemplate.opsForValue().set("stock", String.valueOf(realStock));
            return "扣减库存成功,剩余:"+realStock;
        }
        return "扣减库存失败,剩余库存不足";
    }

    /**
     * 高并发下的下单减库存的问题重现
     * 简单的分布式锁实现
     * @return
     */
    public String deductStockByLock(){
        //设置redis中的key的序列化器,方便读取
        String threadId = String.valueOf(Thread.currentThread().getId());
        try{
            //加锁,设置过期时间
            /*Boolean ifLock = stringRedisTemplate.opsForValue().setIfAbsent(STOCK_LOCK, 1);
            stringRedisTemplate.expire(STOCK_LOCK,30, TimeUnit.SECONDS);*/
            //把上面的两步操作合成一个原子操作
            Boolean ifLock = stringRedisTemplate.opsForValue().setIfAbsent(STOCK_LOCK,threadId, 30, TimeUnit.SECONDS);
            if (!ifLock){
                log.info("无法获取锁,扣减库存失败");
                return "无法获取锁,扣减库存失败";
            }
            Integer stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0){
                int realStock = stock -1;
                stringRedisTemplate.opsForValue().set("stock", String.valueOf(realStock));
                log.info("扣减库存成功,剩余:{}",realStock);
                return "扣减库存成功,剩余:"+realStock;
            }else {
                log.info("扣减库存失败,剩余库存不足");
                return "扣减库存失败,剩余库存不足";
            }
        }catch (Exception e){
            log.error("扣减库存失败:{}",e.getMessage());
        }finally {
            //避免自己的锁被其他线程释放掉
            if(threadId.equals(stringRedisTemplate.opsForValue().get(STOCK_LOCK))){
                stringRedisTemplate.delete(STOCK_LOCK);
            }
        }
        return "扣减库存失败,异常";
    }


    /**
     * 高并发下的下单减库存的问题重现
     * Redisson分布式锁实现
     * @return
     */
    public String deductStockByRedisson(){
        //设置redis中的key的序列化器,方便读取
        String threadId = String.valueOf(Thread.currentThread().getId());
        RLock redissonLock = redisson.getLock(STOCK_LOCK);
        try{
            redissonLock.lock();
            Integer stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0){
                int realStock = stock -1;
                stringRedisTemplate.opsForValue().set("stock", String.valueOf(realStock));
                log.info("扣减库存成功,剩余:{}",realStock);
                return "扣减库存成功,剩余:"+realStock;
            }else {
                log.info("扣减库存失败,剩余库存不足");
                return "扣减库存失败,剩余库存不足";
            }
        }catch (Exception e){
            log.error("扣减库存失败:{}",e.getMessage());
        }finally {
            redissonLock.unlock();
        }
        return "扣减库存失败,异常";
    }

    /**
     * 查询剩余库存
     * @return
     */
    public Integer getStock(){
        //设置redis中的key的序列化器,方便读取
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        stringRedisTemplate.setKeySerializer(stringRedisSerializer);
        stringRedisTemplate.opsForValue().get("stock");
        return  Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
    }

    /**
     * 库存重置
     * @return
     */
    public String setStock(){
        //设置redis中的key的序列化器,方便读取
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        stringRedisTemplate.setKeySerializer(stringRedisSerializer);
        stringRedisTemplate.opsForValue().set("stock", String.valueOf(60));
        return "库存重置成功";
    }
}
