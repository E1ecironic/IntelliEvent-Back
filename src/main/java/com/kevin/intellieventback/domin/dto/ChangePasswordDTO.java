package com.kevin.intellieventback.domin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@Schema(description = "修改密码请求")
public class ChangePasswordDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true, example = "1")
    private Integer userId;

    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码", required = true, example = "Old123456")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$",
            message = "密码必须包含大小写字母和数字，长度8-20位")
    @Schema(description = "新密码", required = true, example = "New123456")
    private String newPassword;
}