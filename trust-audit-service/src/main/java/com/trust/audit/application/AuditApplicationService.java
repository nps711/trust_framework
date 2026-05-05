package com.trust.audit.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trust.audit.api.request.AuditDetailReq;
import com.trust.audit.api.request.AuditQueryReq;
import com.trust.audit.api.response.AuditListRes;
import com.trust.audit.api.response.AuditLogRes;
import com.trust.audit.infrastructure.persistence.mapper.AuditLogMapper;
import com.trust.audit.infrastructure.persistence.model.AuditLog;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditApplicationService {

    private final AuditLogMapper auditLogMapper;

    public AuditApplicationService(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    public AuditListRes queryAuditList(AuditQueryReq req) {
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<AuditLog>()
                .eq(StringUtils.hasText(req.getModule()), AuditLog::getModule, req.getModule())
                .eq(StringUtils.hasText(req.getAction()), AuditLog::getAction, req.getAction())
                .eq(StringUtils.hasText(req.getUserId()), AuditLog::getUserId, req.getUserId())
                .orderByDesc(AuditLog::getCreateTime);

        Page<AuditLog> page = new Page<>(req.getPageNum(), req.getPageSize());
        Page<AuditLog> result = auditLogMapper.selectPage(page, wrapper);

        List<AuditLogRes> list = result.getRecords().stream()
                .map(this::toRes)
                .collect(Collectors.toList());

        AuditListRes res = new AuditListRes();
        res.setList(list);
        res.setTotal(result.getTotal());
        res.setPageNum(req.getPageNum());
        res.setPageSize(req.getPageSize());
        return res;
    }

    public AuditLogRes queryAuditDetail(AuditDetailReq req) {
        AuditLog log = auditLogMapper.selectById(req.getId());
        return log == null ? null : toRes(log);
    }

    private AuditLogRes toRes(AuditLog log) {
        AuditLogRes r = new AuditLogRes();
        r.setId(log.getId());
        r.setTraceId(log.getTraceId());
        r.setModule(log.getModule());
        r.setAction(log.getAction());
        r.setBizId(log.getBizId());
        r.setUserId(log.getUserId());
        r.setStatus(log.getStatus());
        r.setCostMs(log.getCostMs());
        r.setIp(log.getIp());
        r.setCreateTime(log.getCreateTime());
        return r;
    }
}
