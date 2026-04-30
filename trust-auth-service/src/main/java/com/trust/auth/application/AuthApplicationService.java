package com.trust.auth.application;

import com.trust.auth.api.request.LoginReq;
import com.trust.auth.api.request.UserIdReq;
import com.trust.auth.api.response.DataScopeRes;
import com.trust.auth.api.response.LoginRes;
import com.trust.auth.api.response.UserInfoRes;
import com.trust.auth.infrastructure.persistence.model.SysDept;
import com.trust.auth.infrastructure.persistence.model.SysUser;
import com.trust.auth.infrastructure.security.JwtUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthApplicationService {

    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthApplicationService(JdbcTemplate jdbcTemplate, JwtUtil jwtUtil) {
        this.jdbcTemplate = jdbcTemplate;
        this.jwtUtil = jwtUtil;
    }

    public LoginRes login(LoginReq req) {
        List<SysUser> users = jdbcTemplate.query(
                "SELECT * FROM sys_user WHERE username = ?",
                (rs, rowNum) -> {
                    SysUser u = new SysUser();
                    u.setId(rs.getLong("id"));
                    u.setUserId(rs.getString("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setDeptId(rs.getLong("dept_id"));
                    u.setStatus(rs.getString("status"));
                    return u;
                }, req.getUsername());

        if (users.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        SysUser user = users.get(0);
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
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
        List<SysUser> users = jdbcTemplate.query(
                "SELECT u.*, d.dept_name FROM sys_user u LEFT JOIN sys_dept d ON u.dept_id = d.dept_id WHERE u.user_id = ?",
                (rs, rowNum) -> {
                    SysUser u = new SysUser();
                    u.setUserId(rs.getString("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setDeptId(rs.getLong("dept_id"));
                    return u;
                }, userId);
        if (users.isEmpty()) {
            return null;
        }
        SysUser user = users.get(0);
        UserInfoRes res = new UserInfoRes();
        res.setUserId(user.getUserId());
        res.setUsername(user.getUsername());
        res.setDeptId(user.getDeptId());
        String deptName = jdbcTemplate.queryForObject(
                "SELECT dept_name FROM sys_dept WHERE dept_id = ?", String.class, user.getDeptId());
        res.setDeptName(deptName);
        return res;
    }

    public DataScopeRes getDataScopeDepts(UserIdReq req) {
        DataScopeRes res = new DataScopeRes();
        String userId = req.getUserId();

        // 查询用户的角色
        List<String> roleIds = jdbcTemplate.queryForList(
                "SELECT role_id FROM sys_user_role WHERE user_id = ?", String.class, userId);

        if (roleIds.isEmpty()) {
            res.setDeptIds(new ArrayList<>());
            return res;
        }

        // 查询数据权限规则
        List<Long> allDeptIds = new ArrayList<>();
        for (String roleId : roleIds) {
            List<String> scopes = jdbcTemplate.queryForList(
                    "SELECT scope_type, dept_ids FROM sys_data_scope WHERE role_id = ?", String.class, roleId);

            for (String scope : scopes) {
                if ("ALL".equals(scope)) {
                    // 全部权限，返回所有部门
                    List<Long> all = jdbcTemplate.queryForList(
                            "SELECT dept_id FROM sys_dept", Long.class);
                    res.setDeptIds(all);
                    return res;
                } else if ("CUSTOM".equals(scope)) {
                    String deptIdsStr = jdbcTemplate.queryForObject(
                            "SELECT dept_ids FROM sys_data_scope WHERE role_id = ?", String.class, roleId);
                    if (deptIdsStr != null && !deptIdsStr.isEmpty()) {
                        Arrays.stream(deptIdsStr.split(","))
                                .map(Long::parseLong)
                                .forEach(allDeptIds::add);
                    }
                }
            }
        }
        res.setDeptIds(allDeptIds.stream().distinct().collect(Collectors.toList()));
        return res;
    }
}
