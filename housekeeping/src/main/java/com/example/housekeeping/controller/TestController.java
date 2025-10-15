package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/hello")
    public Result<Map<String, Object>> hello() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello from Housekeeping Management System!");
        data.put("status", "running");
        data.put("timestamp", System.currentTimeMillis());
        return Result.success(data);
    }
}
