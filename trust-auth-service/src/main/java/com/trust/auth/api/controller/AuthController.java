package com.trust.auth.api.controller;

import com.trust.common.core.api.R;
import com.trust.common.core.context.UserContextHolder;
import com.trust.auth.api.request.LoginReq;
import com.trust.auth.api.request.LogoutReq;
import com.trust.auth.api.request.UserIdReq;
import com.trust.auth.api.response.DataScopeRes;
import com.trust.auth.api.response.LoginRes;
import com.trust.auth.api.response.UserInfoRes;
import com.trust.auth.application.AuthApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("/login")
    public R<LoginRes> login(@Valid @RequestBody LoginReq req) {
        return R.success(authApplicationService.login(req), traceId());
    }

    @PostMapping("/logout")
    public R<Boolean> logout(@Valid @RequestBody LogoutReq req) {
        authApplicationService.logout(req.getToken());
        return R.success(true, traceId());
    }

    @PostMapping("/user-info")
    public R<UserInfoRes> getUserInfo(@Valid @RequestBody UserIdReq req) {
        return R.success(authApplicationService.getUserInfo(req.getUserId()), traceId());
    }

    @PostMapping("/scope/depts")
    public R<DataScopeRes> getDataScopeDepts(@Valid @RequestBody UserIdReq req) {
        return R.success(authApplicationService.getDataScopeDepts(req), traceId());
    }

    private String traceId() {
        return UserContextHolder.getContext() == null ? null : UserContextHolder.getContext().getTraceId();
    }
}
