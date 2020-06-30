package com.example.demo.model;

import lombok.Data;

/**
 * @author：lanjy
 * @date：2020/6/11
 * @description：
 */
@Data
public class CommonResult {
    private String code;
    private String message;
    private Object object;
}
