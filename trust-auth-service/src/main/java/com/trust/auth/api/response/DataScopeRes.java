package com.trust.auth.api.response;

import java.util.List;

public class DataScopeRes {
    private List<Long> deptIds;

    public List<Long> getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(List<Long> deptIds) {
        this.deptIds = deptIds;
    }
}
