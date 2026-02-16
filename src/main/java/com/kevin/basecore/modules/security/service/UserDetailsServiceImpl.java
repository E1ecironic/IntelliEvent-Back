package com.kevin.basecore.modules.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kevin.basecore.modules.security.entity.SysPermission;
import com.kevin.basecore.modules.security.entity.SysRole;
import com.kevin.basecore.modules.security.entity.SysRolePermission;
import com.kevin.basecore.modules.security.entity.SysUserRole;
import com.kevin.basecore.modules.security.mapper.SysPermissionMapper;
import com.kevin.basecore.modules.security.mapper.SysRoleMapper;
import com.kevin.basecore.modules.security.mapper.SysRolePermissionMapper;
import com.kevin.basecore.modules.security.mapper.SysUserRoleMapper;
import com.kevin.basecore.modules.security.model.LoginUser;
import com.kevin.intellieventback.domin.entity.Users;
import com.kevin.intellieventback.mapper.UsersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersMapper usersMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysPermissionMapper permissionMapper;
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

        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_USER");

        List<SysUserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, user.getId()));
        if (userRoles != null && !userRoles.isEmpty()) {
            List<String> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            if (!roleIds.isEmpty()) {
                List<SysRole> roles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getId, roleIds));
                for (SysRole role : roles) {
                    if (StringUtils.isNotBlank(role.getCode())) {
                        String roleCode = role.getCode();
                        if (roleCode.startsWith("ROLE_")) {
                            permissions.add(roleCode);
                        } else {
                            permissions.add("ROLE_" + roleCode);
                        }
                    }
                }

                List<SysRolePermission> rolePermissions = rolePermissionMapper.selectList(new LambdaQueryWrapper<SysRolePermission>()
                        .in(SysRolePermission::getRoleId, roleIds));
                if (rolePermissions != null && !rolePermissions.isEmpty()) {
                    List<String> permissionIds = rolePermissions.stream()
                            .map(SysRolePermission::getPermissionId)
                            .collect(Collectors.toList());
                    if (!permissionIds.isEmpty()) {
                        List<SysPermission> permissionList = permissionMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                                .in(SysPermission::getId, permissionIds));
                        for (SysPermission permission : permissionList) {
                            if (StringUtils.isNotBlank(permission.getCode())) {
                                permissions.add(permission.getCode());
                            }
                        }
                    }
                }
            }
        }

        return new LoginUser(user, permissions);
    }
}
