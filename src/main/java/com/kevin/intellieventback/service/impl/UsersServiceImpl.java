package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.intellieventback.domin.entity.Users;
import com.kevin.intellieventback.mapper.UsersMapper;
import com.kevin.intellieventback.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    @Value("${user.default.password:s123456}")
    private String defaultPassword;

    @Value("${user.default.avatar:}")
    private String defaultAvatar;

    @Value("${user.password.salt-length:16}")
    private Integer saltLength;

    @Value("${user.login.max-attempts:5}")
    private Integer maxLoginAttempts;

    @Value("${user.login.lock-time:30}")
    private Integer accountLockTime;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();

        // 根据用户名或邮箱查询用户
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getUsername, username).or().eq(Users::getEmail, username);
        Users user = getOne(queryWrapper);

        if (user == null) {
            log.warn("登录失败：用户不存在 - username: {}", username);
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            log.warn("登录失败：用户已被禁用 - userId: {}", user.getId());
            result.put("success", false);
            result.put("message", "用户已被禁用，请联系管理员");
            return result;
        }

        if (user.getStatus() == 2) {
            log.warn("登录失败：用户未激活 - userId: {}", user.getId());
            result.put("success", false);
            result.put("message", "用户未激活，请先激活账户");
            return result;
        }

        // 验证密码
        String encryptedPassword = encryptPassword(password, user.getSalt());
        if (!encryptedPassword.equals(user.getPasswordHash())) {
            log.warn("登录失败：密码错误 - userId: {}", user.getId());
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        updateById(user);

        log.info("用户登录成功 - userId: {}, username: {}", user.getId(), user.getUsername());

        // 移除敏感信息
        user.setPasswordHash(null);
        user.setSalt(null);

        result.put("success", true);
        result.put("message", "登录成功");
        result.put("user", user);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(Users user) {
        log.info("开始用户注册 - username: {}, email: {}", user.getUsername(), user.getEmail());

        // 检查用户名是否已存在
        if (checkUsernameExist(user.getUsername())) {
            log.error("用户注册失败：用户名已存在 - username: {}", user.getUsername());
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (checkEmailExist(user.getEmail())) {
            log.error("用户注册失败：邮箱已存在 - email: {}", user.getEmail());
            throw new RuntimeException("邮箱已存在");
        }

        // 生成盐值
        String salt = generateSalt();
        user.setSalt(salt);

        // 加密密码
        if (StringUtils.isBlank(user.getPasswordHash())) {
            user.setPasswordHash(encryptPassword(defaultPassword, salt));
            log.debug("使用默认密码 - username: {}", user.getUsername());
        } else {
            user.setPasswordHash(encryptPassword(user.getPasswordHash(), salt));
        }

        // 设置默认头像
        if (StringUtils.isBlank(user.getAvatarUrl()) && StringUtils.isNotBlank(defaultAvatar)) {
            user.setAvatarUrl(defaultAvatar);
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
        if (success) {
            log.info("用户注册成功 - userId: {}, username: {}", user.getId(), user.getUsername());
        } else {
            log.error("用户注册失败 - username: {}", user.getUsername());
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        log.info("用户修改密码 - userId: {}", userId);

        Users user = getById(userId);
        if (user == null) {
            log.error("修改密码失败：用户不存在 - userId: {}", userId);
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        String encryptedOldPassword = encryptPassword(oldPassword, user.getSalt());
        if (!encryptedOldPassword.equals(user.getPasswordHash())) {
            log.error("修改密码失败：旧密码错误 - userId: {}", userId);
            throw new RuntimeException("旧密码错误");
        }

        // 更新密码
        user.setPasswordHash(encryptPassword(newPassword, user.getSalt()));
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
    public boolean resetPassword(String email) {
        log.info("重置密码 - email: {}", email);

        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getEmail, email);
        Users user = getOne(queryWrapper);

        if (user == null) {
            log.error("重置密码失败：邮箱不存在 - email: {}", email);
            throw new RuntimeException("邮箱不存在");
        }

        // 生成新密码
        String newPassword = generateRandomPassword();
        String encryptedPassword = encryptPassword(newPassword, user.getSalt());

        user.setPasswordHash(encryptedPassword);
        user.setUpdatedAt(LocalDateTime.now());

        boolean result = updateById(user);

        if (result) {
            // TODO: 发送邮件通知用户新密码
            log.info("用户密码已重置，新密码为：{} - userId: {}, email: {}",
                    newPassword, user.getId(), email);
            // 实际项目中应该发送邮件，这里只记录日志
        } else {
            log.error("重置密码失败 - email: {}", email);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Integer userId, Byte status) {
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
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();

        log.debug("用户分页查询 - pageNum: {}, pageSize: {}, username: {}, realName: {}",
                user.getPageNum(), user.getPageSize(), user.getUsername(), user.getRealName());

        // 根据条件查询
        if (StringUtils.isNotBlank(user.getUsername())) {
            queryWrapper.like(Users::getUsername, user.getUsername());
        }
        if (StringUtils.isNotBlank(user.getEmail())) {
            queryWrapper.like(Users::getEmail, user.getEmail());
        }
        if (StringUtils.isNotBlank(user.getRealName())) {
            queryWrapper.like(Users::getRealName, user.getRealName());
        }
        if (StringUtils.isNotBlank(user.getPhone())) {
            queryWrapper.like(Users::getPhone, user.getPhone());
        }
        if (StringUtils.isNotBlank(user.getPosition())) {
            queryWrapper.like(Users::getPosition, user.getPosition());
        }
        if (user.getStatus() != null) {
            queryWrapper.eq(Users::getStatus, user.getStatus());
        }

        IPage<Users> result = page(page, queryWrapper);
        log.debug("用户分页查询完成 - 总数: {}", result.getTotal());

        return result;
    }

    @Override
    public boolean checkUsernameExist(String username) {
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getUsername, username);
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
    public boolean updateLastLoginTime(Integer userId) {
        Users user = getById(userId);
        if (user == null) {
            log.warn("更新最后登录时间失败：用户不存在 - userId: {}", userId);
            return false;
        }

        user.setLastLoginAt(LocalDateTime.now());
        boolean success = updateById(user);
        log.debug("更新最后登录时间 - userId: {}, success: {}", userId, success);
        return success;
    }

    /**
     * 加密密码
     */
    private String encryptPassword(String password, String salt) {
        String str = password + salt;
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }

    /**
     * 生成盐值
     */
    private String generateSalt() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, Math.min(saltLength, uuid.length()));
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
        if (status == null) return "未知";
        switch (status) {
            case 0: return "禁用";
            case 1: return "正常";
            case 2: return "待激活";
            default: return "未知";
        }
    }
}