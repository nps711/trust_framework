package com.trust.audit.application;

import com.trust.audit.api.request.AuditDetailReq;
import com.trust.audit.api.request.AuditQueryReq;
import com.trust.audit.api.response.AuditListRes;
import com.trust.audit.api.response.AuditLogRes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuditApplicationService {

    private final JdbcTemplate jdbcTemplate;

    public AuditApplicationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AuditListRes queryAuditList(AuditQueryReq req) {
        StringBuilder sql = new StringBuilder("SELECT * FROM sys_audit_log WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (req.getModule() != null && !req.getModule().isEmpty()) {
            sql.append(" AND module = ?");
            params.add(req.getModule());
        }
        if (req.getAction() != null && !req.getAction().isEmpty()) {
            sql.append(" AND action = ?");
            params.add(req.getAction());
        }
        if (req.getUserId() != null && !req.getUserId().isEmpty()) {
            sql.append(" AND user_id = ?");
            params.add(req.getUserId());
        }

        String countSql = sql.toString().replace("SELECT *", "SELECT COUNT(*)");
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, params.toArray());

        sql.append(" ORDER BY create_time DESC LIMIT ? OFFSET ?");
        int offset = (req.getPageNum() - 1) * req.getPageSize();
        params.add(req.getPageSize());
        params.add(offset);

        List<AuditLogRes> list = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            AuditLogRes r = new AuditLogRes();
            r.setId(rs.getLong("id"));
            r.setTraceId(rs.getString("trace_id"));
            r.setModule(rs.getString("module"));
            r.setAction(rs.getString("action"));
            r.setBizId(rs.getString("biz_id"));
            r.setUserId(rs.getString("user_id"));
            r.setStatus(rs.getString("status"));
            r.setCostMs(rs.getLong("cost_ms"));
            r.setIp(rs.getString("ip"));
            r.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            return r;
        }, params.toArray());

        AuditListRes res = new AuditListRes();
        res.setList(list);
        res.setTotal(total != null ? total : 0);
        res.setPageNum(req.getPageNum());
        res.setPageSize(req.getPageSize());
        return res;
    }

    public AuditLogRes queryAuditDetail(AuditDetailReq req) {
        List<AuditLogRes> list = jdbcTemplate.query(
                "SELECT * FROM sys_audit_log WHERE id = ?",
                (rs, rowNum) -> {
                    AuditLogRes r = new AuditLogRes();
                    r.setId(rs.getLong("id"));
                    r.setTraceId(rs.getString("trace_id"));
                    r.setModule(rs.getString("module"));
                    r.setAction(rs.getString("action"));
                    r.setBizId(rs.getString("biz_id"));
                    r.setUserId(rs.getString("user_id"));
                    r.setStatus(rs.getString("status"));
                    r.setCostMs(rs.getLong("cost_ms"));
                    r.setIp(rs.getString("ip"));
                    r.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                    return r;
                }, req.getId());
        return list.isEmpty() ? null : list.get(0);
    }
}
