package com.kevin.intellieventback.controller;

import com.kevin.basecore.common.domin.PageResult;
import com.kevin.intellieventback.domin.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.kevin.intellieventback.domin.entity.Users;
import com.kevin.intellieventback.service.UsersService;
import com.kevin.basecore.common.domin.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.BeanUtils;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2025-12-21
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@Tag(name = "用户管理")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        Map<String, Object> result = usersService.login(loginDTO.getUserName(), loginDTO.getPassword());
        return Result.success(result);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Boolean> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        // 将 DTO 转换为 Entity
        Users user = new Users();
        BeanUtils.copyProperties(registerDTO, user);
        user.setPasswordHash(registerDTO.getPassword()); // 特殊处理密码字段名不一致
        boolean result = usersService.register(user);
        return Result.success(result);
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码")
    public Result<Boolean> changePassword(@Valid @RequestBody ChangePasswordDTO passwordDTO) {
        boolean result = usersService.changePassword(
                passwordDTO.getUserId(),
                passwordDTO.getOldPassword(),
                passwordDTO.getNewPassword()
        );
        return Result.success(result);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "重置密码")
    public Result<Boolean> resetPassword(@RequestParam @Valid @Email(message = "邮箱格式不正确") String email) {
        boolean result = usersService.resetPassword(email);
        return Result.success(result);
    }

    @PutMapping("/status")
    @Operation(summary = "更新用户状态")
    public Result<Boolean> updateStatus(@RequestParam String userId,
                                        @RequestParam Byte status) {
        boolean result = usersService.updateStatus(userId, status);
        return Result.success(result);
    }

    @GetMapping("/check-username")
    @Operation(summary = "检查用户名是否存在")
    public Result<Boolean> checkUsernameExist(@RequestParam String username) {
        boolean exists = usersService.checkUserNameExist(username);
        return Result.success(exists);
    }

    @GetMapping("/check-email")
    @Operation(summary = "检查邮箱是否存在")
    public Result<Boolean> checkEmailExist(@RequestParam String email) {
        boolean exists = usersService.checkEmailExist(email);
        return Result.success(exists);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户")
    public Result<Users> getById(@Parameter(description = "用户ID", required = true) @PathVariable String id) {
        Users entity = usersService.getById(id);
        // 移除敏感信息
        if (entity != null) {
            entity.setPasswordHash(null);
            entity.setSalt(null);
        }
        return Result.success(entity);
    }

    @PostMapping
    @Operation(summary = "新增用户（管理员）")
    public Result<Boolean> save(@Valid @RequestBody UserRegisterDTO registerDTO) {
        Users user = new Users();
        BeanUtils.copyProperties(registerDTO, user);
        user.setPasswordHash(registerDTO.getPassword());
        boolean result = usersService.register(user);
        return Result.success(result);
    }

    @PutMapping
    @Operation(summary = "修改用户信息")
    public Result<Boolean> update(@Valid @RequestBody UserUpdateDTO updateDTO) {
        // 将 DTO 转换为 Entity
        Users user = new Users();
        BeanUtils.copyProperties(updateDTO, user);
        // 防止更新密码和盐值
        user.setPasswordHash(null);
        user.setSalt(null);
        boolean result = usersService.updateById(user);
        
        // 更新组织关系
        if (result && StringUtils.isNotBlank(user.getOrganizationId())) {
            usersService.updateUserOrganization(user.getId(), user.getOrganizationId());
        }
        
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Boolean> delete(@Parameter(description = "用户ID", required = true) @PathVariable String id) {
        boolean result = usersService.removeById(id);
        return Result.success(result);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询用户列表")
    public Result page(@RequestBody Users entity) {
        IPage<Users> page = usersService.pageList(entity);
        // 移除敏感信息
        page.getRecords().forEach(user -> {
            user.setPasswordHash(null);
            user.setSalt(null);
        });
        return Result.success(PageResult.returnResult(page.getTotal(), page.getRecords()));
    }
}