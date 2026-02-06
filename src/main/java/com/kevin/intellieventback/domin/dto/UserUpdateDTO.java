package com.kevin.intellieventback.domin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Data
@Schema(description = "用户更新请求")
public class UserUpdateDTO {

    @Schema(description = "用户ID", required = true)
    private String id;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "kevin@example.com")
    private String email;

    @Schema(description = "真实姓名", example = "凯文")
    private String realName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "职位", example = "软件工程师")
    private String position;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "个性化设置（JSON格式）", example = "{\"theme\":\"dark\",\"language\":\"en\"}")
    private String settings;

    @Schema(description = "组织ID", example = "2019234348271816705")
    private String organizationId;

    @Schema(description = "状态", example = "1")
    private Byte status;
}
