package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.intellieventback.domin.entity.Users;
import com.kevin.intellieventback.domin.entity.UserOrganization;
import com.kevin.intellieventback.mapper.UsersMapper;
import com.kevin.intellieventback.service.UsersService;
import com.kevin.intellieventback.service.UserOrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserOrganizationService userOrganizationService;

    @Value("${user.default.password:Aa123456}")
    private String defaultPassword;

    @Value("${user.default.avatar:}")
    private String defaultAvatar;

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
        queryWrapper.eq(Users::getUserName, username).or().eq(Users::getEmail, username);
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
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("登录失败：密码错误 - userId: {}", user.getId());
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        updateById(user);

        log.info("用户登录成功 - userId: {}, username: {}", user.getId(), user.getUserName());

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
            passwordToCheck = defaultPassword;
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
            user.setPasswordHash(passwordEncoder.encode(defaultPassword));
        } else {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
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

        // 保存组织关联关系
        if (success && StringUtils.isNotBlank(user.getOrganizationId())) {
            UserOrganization userOrg = new UserOrganization();
            userOrg.setUserId(user.getId());
            userOrg.setOrganizationId(user.getOrganizationId());
            userOrg.setRoleType((byte) 1); // 默认普通成员
            userOrg.setIsPrimary(true);
            userOrg.setStatus((byte) 1);
            userOrg.setCreatedAt(LocalDateTime.now());
            userOrg.setUpdatedAt(LocalDateTime.now());
            userOrganizationService.save(userOrg);
            log.info("关联用户组织成功 - userId: {}, organizationId: {}", user.getId(), user.getOrganizationId());
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
        user.setPasswordHash(passwordEncoder.encode(newPassword));
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserOrganization(String userId, String organizationId) {
        log.info("更新用户组织关系 - userId: {}, organizationId: {}", userId, organizationId);
        
        if (StringUtils.isBlank(organizationId)) {
            return true;
        }

        // 1. 查询现有的主组织关系
        LambdaQueryWrapper<UserOrganization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserOrganization::getUserId, userId)
                    .eq(UserOrganization::getIsPrimary, true);
        UserOrganization existing = userOrganizationService.getOne(queryWrapper);

        if (existing != null) {
            // 如果组织 ID 没变，直接返回
            if (organizationId.equals(existing.getOrganizationId())) {
                return true;
            }
            // 2. 更新现有的主组织关系
            existing.setOrganizationId(organizationId);
            existing.setUpdatedAt(LocalDateTime.now());
            return userOrganizationService.updateById(existing);
        } else {
            // 3. 不存在则新增
            UserOrganization userOrg = new UserOrganization();
            userOrg.setUserId(userId);
            userOrg.setOrganizationId(organizationId);
            userOrg.setRoleType((byte) 1);
            userOrg.setIsPrimary(true);
            userOrg.setStatus((byte) 1);
            userOrg.setCreatedAt(LocalDateTime.now());
            userOrg.setUpdatedAt(LocalDateTime.now());
            return userOrganizationService.save(userOrg);
        }
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


    /**
     * 校验密码格式
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