package com.example.demo;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Springboot2RedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(Springboot2RedisApplication.class, args);
	}

    /**
     * 注入redisson,实现分布式锁
     * @return
     */
	@Bean
	public Redisson redisson(){
		//单机版
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379").setDatabase(0);
        return (Redisson) Redisson.create(config);
    }
}
