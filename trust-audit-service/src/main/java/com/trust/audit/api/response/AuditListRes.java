package com.trust.audit.api.response;

import java.util.List;

public class AuditListRes {
    private List<AuditLogRes> list;
    private long total;
    private int pageNum;
    private int pageSize;

    public List<AuditLogRes> getList() {
        return list;
    }

    public void setList(List<AuditLogRes> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
