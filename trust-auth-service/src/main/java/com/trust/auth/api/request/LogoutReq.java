package com.trust.auth.api.request;

import com.trust.common.core.api.BaseRequest;

public class LogoutReq extends BaseRequest {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
