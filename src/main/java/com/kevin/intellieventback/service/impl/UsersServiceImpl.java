package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.basecore.modules.email.service.EmailService;
import com.kevin.basecore.modules.system.service.SysConfigService;
import com.kevin.intellieventback.domin.entity.Users;
import com.kevin.intellieventback.domin.entity.UserOrganization;
import com.kevin.intellieventback.mapper.UsersMapper;
import com.kevin.intellieventback.service.UsersService;
import com.kevin.intellieventback.service.UserOrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2025-12-21
 */
@Slf4j
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserOrganizationService userOrganizationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SysConfigService sysConfigService;

    private String getDefaultPassword() {
        return sysConfigService.getValue("user.default.password", "Aa123456");
    }

    private String getDefaultAvatar() {
        return sysConfigService.getValue("user.default.avatar", "");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(Users user) {
        log.info("开始用户注册 - userName: {}, email: {}, organizationId: {}",
                user.getUserName(), user.getEmail(), user.getOrganizationId());

        // 检查用户名是否已存在
        if (checkUserNameExist(user.getUserName())) {
            log.error("用户注册失败：用户名已存在 - userName: {}", user.getUserName());
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (checkEmailExist(user.getEmail())) {
            log.error("用户注册失败：邮箱已存在 - email: {}", user.getEmail());
            throw new RuntimeException("邮箱已存在");
        }

        String passwordToCheck;
        if (StringUtils.isBlank(user.getPasswordHash())) {
            // 如果用户没有提供密码，使用默认密码
            passwordToCheck = getDefaultPassword();
        } else {
            // 如果用户提供了密码，校验格式
            passwordToCheck = user.getPasswordHash();
        }

        // 校验密码格式（如果不为空才校验）
        if (StringUtils.isNotBlank(passwordToCheck)) {
            if (!isValidPasswordFormat(passwordToCheck)) {
                log.error("用户注册失败：密码格式不符合要求 - userName: {}", user.getUserName());
                throw new RuntimeException("密码必须包含大小写字母和数字，长度8-20位");
            }
        }

        // 生成盐值（BCrypt 内部自带盐值，不需要额外设置 salt 字段）
        user.setSalt(UUID.randomUUID().toString().substring(0, 16));

        // 使用 BCrypt 加密密码
        if (StringUtils.isBlank(user.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(getDefaultPassword()));
        } else {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }

        // 设置默认头像
        if (StringUtils.isBlank(user.getAvatarUrl()) && StringUtils.isNotBlank(getDefaultAvatar())) {
            user.setAvatarUrl(getDefaultAvatar());
        }

        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus((byte) 2); // 默认待激活状态
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());

        // 默认设置
        if (StringUtils.isBlank(user.getSettings())) {
            user.setSettings("{\"theme\":\"light\",\"language\":\"zh-CN\"}");
        }

        boolean success = save(user);

        // 保存组织关联关系
        if (success && StringUtils.isNotBlank(user.getOrganizationId())) {
            userOrganizationService.updateUserOrganization(user.getId(), user.getOrganizationId());
        }

        if (success) {
            log.info("用户注册成功 - userId: {}, UserName: {}", user.getId(), user.getUserName());
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        log.info("用户修改密码 - userId: {}", userId);

        Users user = getById(userId);
        if (user == null) {
            log.error("修改密码失败：用户不存在 - userId: {}", userId);
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            log.error("修改密码失败：旧密码错误 - userId: {}", userId);
            throw new RuntimeException("旧密码错误");
        }

        // 更新密码
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        boolean success = updateById(user);
        if (success) {
            log.info("用户修改密码成功 - userId: {}", userId);
        } else {
            log.error("用户修改密码失败 - userId: {}", userId);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(List<String> userIds) {
        log.info("批量重置密码 - userIds: {}", userIds);

        if (userIds == null || userIds.isEmpty()) {
            return true;
        }

        List<Users> users = listByIds(userIds);
        if (users.size() != userIds.size()) {
            log.warn("部分用户不存在 - 期望数量: {}, 实际数量: {}", userIds.size(), users.size());
        }

        for (Users user : users) {
            // 生成新密码
            String newPassword = getDefaultPassword(); // 批量重置通常统一重置为默认密码
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            log.info("用户密码已重置为默认密码 - userId: {}, username: {}", user.getId(), user.getUserName());
        }

        boolean success = updateBatchById(users);
        if (success) {
            // 批量发送邮件通知
            List<String> emails = users.stream()
                    .map(Users::getEmail)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
            
            if (!emails.isEmpty()) {
                String subject = "账户密码重置通知";
                String content = String.format(
                    "<h3>您的账户密码已被管理员重置</h3>" +
                    "<p>您的新密码为: <strong>%s</strong></p>" +
                    "<p>为了您的账号安全，请在登录后尽快修改密码。</p>", 
                    getDefaultPassword()
                );
                emailService.sendHtmlMailBatch(emails, subject, content);
            }
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(String userId, Byte status) {
        log.info("更新用户状态 - userId: {}, status: {}", userId, status);

        Users user = getById(userId);
        if (user == null) {
            log.error("更新状态失败：用户不存在 - userId: {}", userId);
            throw new RuntimeException("用户不存在");
        }

        String oldStatus = statusToString(user.getStatus());
        String newStatus = statusToString(status);

        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());

        boolean success = updateById(user);
        if (success) {
            log.info("用户状态更新成功：{} -> {} - userId: {}",
                    oldStatus, newStatus, userId);
        } else {
            log.error("用户状态更新失败 - userId: {}", userId);
        }

        return success;
    }

    @Override
    public IPage<Users> pageList(Users user) {
        Page<Users> page = new Page<>(user.getPageNum(), user.getPageSize());
        log.debug("用户分页查询(含组织ID) - pageNum: {}, pageSize: {}, username: {}, realName: {}",
                user.getPageNum(), user.getPageSize(), user.getUserName(), user.getRealName());

        IPage<Users> result = baseMapper.selectUserPageWithOrg(page, user);
        log.debug("用户分页查询完成 - 总数: {}", result.getTotal());

        return result;
    }

    @Override
    public boolean checkUserNameExist(String username) {
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getUserName, username);
        boolean exists = count(queryWrapper) > 0;
        log.debug("检查用户名是否存在 - username: {}, exists: {}", username, exists);
        return exists;
    }

    @Override
    public boolean checkEmailExist(String email) {
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getEmail, email);
        boolean exists = count(queryWrapper) > 0;
        log.debug("检查邮箱是否存在 - email: {}, exists: {}", email, exists);
        return exists;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLastLoginTime(String userId) {
        log.info("更新最后登录时间 - userId: {}", userId);
        Users user = new Users();
        user.setId(userId);
        user.setLastLoginAt(LocalDateTime.now());
        return updateById(user);
    }

    /**
     * 生成随机密码
     */
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    /**
     * 状态转换为字符串
     */
    private String statusToString(Byte status) {
        if (status == null)
            return "未知";
        switch (status) {
            case 0:
                return "禁用";
            case 1:
                return "正常";
            case 2:
                return "待激活";
            default:
                return "未知";
        }
    }

    /**
     * 校验密码格式
     * 
     * @param password 明文密码
     * @return 是否符合格式要求
     */
    private boolean isValidPasswordFormat(String password) {
        if (StringUtils.isBlank(password)) {
            return false; // 空密码不允许
        }

        // 长度校验
        if (password.length() < 8 || password.length() > 20) {
            return false;
        }

        // 包含大小写字母和数字的校验
        boolean hasLowercase = false;
        boolean hasUppercase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        return hasLowercase && hasUppercase && hasDigit;
    }
}