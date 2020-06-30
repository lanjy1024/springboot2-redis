package com.example.demo.service;

import com.example.demo.dao.UserRepository;
import com.example.demo.entity.User;
import com.example.demo.model.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：lanjy
 * @date：2020/6/11
 * @description：
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 注入springboot自动配置好的RedisTemplate
     */
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    public User findByUserName(String userName) {

        return userRepository.findByUserName(userName);
    }

    /**
     * 从缓存中读取userList
     * 为null,则从数据库中读取,再放入redis
     * 在高并发情况下会有缓存穿透的问题
     * @return
     */
    public CommonResult findAll(){
        CommonResult result = new CommonResult();
        result.setCode("0001");
        result.setMessage("从缓存中读取userList");
        //设置redis中的key的序列化器,方便读取
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);

        List<User> userList = (List<User>) redisTemplate.opsForValue().get("userList");
        if (userList == null) {
            result.setMessage("从数据库中读取userList..............");
            log.info("从数据库中读取userList..............");
            userList = userRepository.findAll();
            redisTemplate.opsForValue().set("userList",userList);
        }else {
            result.setMessage("从缓存中读取userList");
            log.info("从缓存中读取userList..............");
        }
        result.setObject(userList);
        return result;
    }

    /**
     * 添加双重检测的同步锁
     * 解决缓存穿透
     * @return
     */
    public CommonResult synFindAll(){
        CommonResult result = new CommonResult();
        result.setCode("0001");

        //设置redis中的key的序列化器,方便读取
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);

        List<User> userList = (List<User>) redisTemplate.opsForValue().get("userListSyn");
        if (userList == null) {
            synchronized (this){
                userList = (List<User>) redisTemplate.opsForValue().get("userListSyn");
                if (userList == null) {
                    result.setMessage("从数据库中读取 userListSyn..............");
                    log.info("从数据库中读取 userListSyn..............");
                    userList = userRepository.findAll();
                    redisTemplate.opsForValue().set("userListSyn",userList);
                }else {
                    result.setMessage("从缓存中读取 userListSyn");
                    log.info("从缓存中读取 userListSyn..............");
                }
            }
        }else {
            result.setMessage("从缓存中读取 userListSyn");
            log.info("从缓存中读取 userListSyn..............");
        }
        result.setObject(userList);
        return result;
    }
}
