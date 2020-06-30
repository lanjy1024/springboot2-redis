package com.example.demo.controller;


import com.example.demo.entity.User;
import com.example.demo.model.CommonResult;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author：lanjy
 * @date：2020/6/11
 * @description：UserController用来测试访问，权限全部采用注解的方式。
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //用户查询,缓存穿透的测试
    @GetMapping("/list")
    public CommonResult getUserList(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                userService.findAll();
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 2000; i++) {
            executorService.execute(runnable);
        }

        return userService.findAll();
    }


    //用户查询,缓存穿透的测试
    @GetMapping("/list/syn")
    public CommonResult synGetUserList(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                userService.synFindAll();
            }
        };
        HashMap<String, String> stringStringHashMap = new HashMap<>(22);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 2000; i++) {
            executorService.execute(runnable);
        }

        return userService.synFindAll();
    }

}
