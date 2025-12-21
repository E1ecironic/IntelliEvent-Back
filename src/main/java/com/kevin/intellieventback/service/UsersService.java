package com.kevin.intellieventback.service;

import com.kevin.intellieventback.domin.entity.Users;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author kevin
 * @since 2025-12-21
 */
public interface UsersService extends IService<Users> {

    /**
     * 用户登录
     * @param username 用户名或邮箱
     * @param password 密码
     * @return 登录结果
     */
    Map<String, Object> login(String username, String password);

    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    boolean register(Users user);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Integer userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     * @param email 邮箱
     * @return 是否成功
     */
    boolean resetPassword(String email);

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Integer userId, Byte status);

    /**
     * 分页查询用户列表（带条件）
     * @param user 查询条件
     * @return 分页结果
     */
    IPage<Users> pageList(Users user);

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean checkUsernameExist(String username);

    /**
     * 检查邮箱是否已存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean checkEmailExist(String email);

    /**
     * 更新最后登录时间
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean updateLastLoginTime(Integer userId);
}