package com.kevin.intellieventback.domin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "用户登录请求")
public class UserLoginDTO {

    @NotBlank(message = "用户名或邮箱不能为空")
    @Schema(description = "用户名或邮箱", required = true, example = "admin")
    private String userName;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", required = true, example = "Test123456")
    private String password;

    @Schema(description = "验证码")
    private String code;

    @Schema(description = "验证码UUID")
    private String uuid;
}