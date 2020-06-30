package com.example.demo.controller;

import com.example.demo.model.CommonResult;
import com.example.demo.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author：lanjy
 * @date：2020/6/30
 * @description：
 */
@RestController
public class StockController {

    @Autowired
    private StockService stockService;
    @GetMapping("/set/stock")
    public String setStock(){
        return stockService.setStock();
    }


    /**
     * 扣减库存,高并发时存在问题
     * @return
     */
    @GetMapping("/ded/stock")
    public String deductStock(){
        return stockService.deductStock();
    }

    /**
     * 模拟高并发下的下单减库存
     * 下单前,剩余库存:60,同时下单30,剩余库存:59
     * @return
     */
    @GetMapping("/bitch/stock")
    public String deduStock(){
        Integer oldstock = stockService.getStock();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                stockService.deductStock();
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 30; i++) {
            executorService.execute(runnable);
        }
        return "下单前,剩余库存:"+oldstock+",同时下单30,剩余库存:"+stockService.getStock();
    }

    /**
     * 模拟高并发下的下单减库存
     * 下单前,剩余库存:60,同时下单30,剩余库存:59
     * @return
     */
    @GetMapping("/lock/stock")
    public String deductStockByLock(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                stockService.deductStockByLock();
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 30; i++) {
            executorService.execute(runnable);
        }
        return "完成";
    }

    @GetMapping("/get/stock")
    public String getStock(){
        return "剩余库存:"+stockService.getStock();
    }
}
