package com.kevin.intellieventback.controller;

import com.kevin.basecore.common.domin.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "测试接口")
public class TestController {

    @GetMapping("/hello")
    @Operation(summary = "测试接口")
    public Result<String> hello() {
        return Result.success("Hello, IntelliEvent Backend is running!");
    }
}