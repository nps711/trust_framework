package com.trust.auth.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trust.auth.api.request.LoginReq;
import com.trust.auth.api.request.UserIdReq;
import com.trust.auth.api.response.DataScopeRes;
import com.trust.auth.api.response.LoginRes;
import com.trust.auth.api.response.UserInfoRes;
import com.trust.auth.infrastructure.persistence.mapper.SysDataScopeMapper;
import com.trust.auth.infrastructure.persistence.mapper.SysDeptMapper;
import com.trust.auth.infrastructure.persistence.mapper.SysUserMapper;
import com.trust.auth.infrastructure.persistence.mapper.SysUserRoleMapper;
import com.trust.auth.infrastructure.persistence.model.SysDataScope;
import com.trust.auth.infrastructure.persistence.model.SysDept;
import com.trust.auth.infrastructure.persistence.model.SysUser;
import com.trust.auth.infrastructure.persistence.model.SysUserRole;
import com.trust.auth.infrastructure.security.JwtUtil;
import com.trust.common.core.error.BusinessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthApplicationService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysDataScopeMapper sysDataScopeMapper;
    private final SysDeptMapper sysDeptMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthApplicationService(SysUserMapper sysUserMapper,
                                  SysUserRoleMapper sysUserRoleMapper,
                                  SysDataScopeMapper sysDataScopeMapper,
                                  SysDeptMapper sysDeptMapper,
                                  JwtUtil jwtUtil) {
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysDataScopeMapper = sysDataScopeMapper;
        this.sysDeptMapper = sysDeptMapper;
        this.jwtUtil = jwtUtil;
    }

    public LoginRes login(LoginReq req) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, req.getUsername()));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getDeptId());
        LoginRes res = new LoginRes();
        res.setAccessToken(token);
        res.setTokenType("Bearer");
        res.setExpiresIn(7200);
        return res;
    }

    public void logout(String token) {
        // 演示版本：实际应加入 Redis 黑名单
    }

    public UserInfoRes getUserInfo(String userId) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, userId));
        if (user == null) {
            return null;
        }
        UserInfoRes res = new UserInfoRes();
        res.setUserId(user.getUserId());
        res.setUsername(user.getUsername());
        res.setDeptId(user.getDeptId());
        if (user.getDeptId() != null) {
            SysDept dept = sysDeptMapper.selectOne(
                    new LambdaQueryWrapper<SysDept>().eq(SysDept::getDeptId, user.getDeptId()));
            if (dept != null) {
                res.setDeptName(dept.getDeptName());
            }
        }
        return res;
    }

    public DataScopeRes getDataScopeDepts(UserIdReq req) {
        DataScopeRes res = new DataScopeRes();

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, req.getUserId()));
        if (userRoles.isEmpty()) {
            res.setDeptIds(new ArrayList<>());
            return res;
        }

        List<String> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        List<SysDataScope> scopes = sysDataScopeMapper.selectList(
                new LambdaQueryWrapper<SysDataScope>().in(SysDataScope::getRoleId, roleIds));

        List<Long> allDeptIds = new ArrayList<>();
        for (SysDataScope scope : scopes) {
            if ("ALL".equals(scope.getScopeType())) {
                List<Long> all = sysDeptMapper.selectList(new LambdaQueryWrapper<>())
                        .stream().map(SysDept::getDeptId).collect(Collectors.toList());
                res.setDeptIds(all);
                return res;
            } else if ("CUSTOM".equals(scope.getScopeType())
                    && scope.getDeptIds() != null && !scope.getDeptIds().isEmpty()) {
                Arrays.stream(scope.getDeptIds().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .forEach(allDeptIds::add);
            }
        }
        res.setDeptIds(allDeptIds.stream().distinct().collect(Collectors.toList()));
        return res;
    }
}
