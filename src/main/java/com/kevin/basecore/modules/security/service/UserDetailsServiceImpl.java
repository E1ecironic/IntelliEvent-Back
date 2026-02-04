package com.kevin.basecore.modules.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kevin.basecore.modules.security.model.LoginUser;
import com.kevin.intellieventback.domin.entity.Users;
import com.kevin.intellieventback.mapper.UsersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersMapper usersMapper;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // 调试专用：通用 sa 账号处理
        if ("sa".equals(userName)) {
            Users saUser = new Users();
            saUser.setId("0");
            saUser.setUserName("sa");
            saUser.setRealName("超级管理员(调试)");
            // 重新生成一个标准的 BCrypt 哈希
            saUser.setPasswordHash(passwordEncoder.encode("1"));
            saUser.setStatus((byte) 1);
            
            Set<String> permissions = new HashSet<>();
            permissions.add("*:*:*"); // 拥有所有权限
            permissions.add("ROLE_ADMIN");
            
            return new LoginUser(saUser, permissions);
        }

        Users user = usersMapper.selectOne(new LambdaQueryWrapper<Users>()
                .eq(Users::getUserName, userName));

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + userName);
        }

        // 这里可以根据实际权限体系加载权限标识
        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_USER"); // 默认角色
        
        return new LoginUser(user, permissions);
    }
}
