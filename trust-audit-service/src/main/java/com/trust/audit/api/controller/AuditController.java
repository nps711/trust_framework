package com.trust.audit.api.controller;

import com.trust.audit.api.request.AuditDetailReq;
import com.trust.audit.api.request.AuditQueryReq;
import com.trust.audit.api.response.AuditListRes;
import com.trust.audit.api.response.AuditLogRes;
import com.trust.audit.application.AuditApplicationService;
import com.trust.common.core.api.R;
import com.trust.common.core.context.UserContextHolder;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditApplicationService auditApplicationService;

    public AuditController(AuditApplicationService auditApplicationService) {
        this.auditApplicationService = auditApplicationService;
    }

    @PostMapping("/list")
    public R<AuditListRes> queryAuditList(@Valid @RequestBody AuditQueryReq req) {
        return R.success(auditApplicationService.queryAuditList(req), traceId());
    }

    @PostMapping("/detail")
    public R<AuditLogRes> queryAuditDetail(@Valid @RequestBody AuditDetailReq req) {
        return R.success(auditApplicationService.queryAuditDetail(req), traceId());
    }

    private String traceId() {
        return UserContextHolder.getContext() == null ? null : UserContextHolder.getContext().getTraceId();
    }
}
