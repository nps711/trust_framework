package com.trust.common.core.api;

import java.io.Serializable;

public class BaseRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
