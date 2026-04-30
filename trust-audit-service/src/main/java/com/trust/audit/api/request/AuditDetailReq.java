package com.trust.audit.api.request;

import com.trust.common.core.api.BaseRequest;
import jakarta.validation.constraints.NotNull;

public class AuditDetailReq extends BaseRequest {
    @NotNull
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
