package com.trust.common.security.client;

import com.trust.common.core.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "trust-auth-service", contextId = "authServiceClient")
public interface AuthServiceClient {

    @PostMapping("/api/scope/depts")
    R<List<Long>> getDataScopeDepts(@RequestBody UserIdReq req);

    class UserIdReq {
        private String userId;

        public UserIdReq() {
        }

        public UserIdReq(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
